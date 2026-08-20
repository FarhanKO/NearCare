import 'package:flutter/material.dart';
import 'package:geolocator/geolocator.dart';
import '../models/doctor.dart';
import '../services/healthcare_service.dart';
import '../services/symptom_triage.dart';

enum DoctorSort { bestMatch, distance, rating, openNow }

class _ScoreComponent {
  final double weight;
  final double value;
  const _ScoreComponent(this.weight, this.value);
}

class DoctorProvider with ChangeNotifier {
  final _service = HealthcareService();
  final _triage = const SymptomTriage();

  List<EnhancedDoctorPlace> _results = [];
  List<EnhancedDoctorPlace> get results => _results;

  /// Specialties the current search resolved to (for symptom searches, these
  /// are what the triage inferred; shown to the user for transparency).
  List<Specialty> _matchedSpecialties = [];
  List<Specialty> get matchedSpecialties => _matchedSpecialties;

  bool _isLoading = false;
  bool get isLoading => _isLoading;

  String _statusMessage = "";
  String get statusMessage => _statusMessage;

  DoctorSort _sort = DoctorSort.bestMatch;
  DoctorSort get sort => _sort;

  double userLat = 0.0;
  double userLon = 0.0;
  String locationLabel = "";

  List<Specialty> get allSpecialties => SymptomTriage.specialties;

  void setLocation(double lat, double lon, String label) {
    userLat = lat;
    userLon = lon;
    locationLabel = label;
  }

  void setSort(DoctorSort s) {
    _sort = s;
    _applySort();
    notifyListeners();
  }

  /// Search directly by a chosen specialty.
  Future<void> searchBySpecialty(Specialty specialty) async {
    await _runSearch([specialty]);
  }

  /// Search from free-text symptoms. Uses the top matched specialties (capped
  /// to keep API calls bounded).
  Future<void> searchBySymptoms(String symptoms) async {
    final matched = _triage.analyzeSymptoms(symptoms);
    final top = matched.take(2).toList();
    await _runSearch(top);
  }

  Future<void> _runSearch(List<Specialty> specialties) async {
    _matchedSpecialties = specialties;
    _isLoading = true;
    _statusMessage = "Searching clinics & specialists nearby...";
    notifyListeners();

    try {
      int radiusMeters = 10000;
      final merged = <String, DoctorPlace>{};

      for (final s in specialties) {
        final places = await _service.fetchDoctorPlaces(userLat, userLon, radiusMeters, s);
        for (final p in places) {
          merged[p.id] = p; // dedupe across specialties by placeId
        }
      }

      if (merged.length < 5) {
        radiusMeters = 25000;
        _statusMessage = "Expanding search radius...";
        notifyListeners();
        for (final s in specialties) {
          final places = await _service.fetchDoctorPlaces(userLat, userLon, radiusMeters, s);
          for (final p in places) {
            merged[p.id] = p;
          }
        }
      }

      final radiusKm = radiusMeters / 1000.0;

      _results = merged.values.map((p) {
        final distKm = _distanceKm(userLat, userLon, p.latitude, p.longitude);
        return EnhancedDoctorPlace(
          place: p,
          distanceKm: distKm,
          matchScore: _score(p, distKm, radiusKm),
        );
      }).toList();

      _results.sort((a, b) => b.matchScore.compareTo(a.matchScore));
      _results = _results.take(15).toList();
      _applySort();

      _statusMessage = _results.isEmpty
          ? "No matching clinics found nearby."
          : "Found ${_results.length} matching clinics & specialists.";
    } catch (e) {
      _statusMessage = "Error searching for doctors.";
      _results = [];
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  void _applySort() {
    switch (_sort) {
      case DoctorSort.bestMatch:
        _results.sort((a, b) => b.matchScore.compareTo(a.matchScore));
        break;
      case DoctorSort.distance:
        _results.sort((a, b) => a.distanceKm.compareTo(b.distanceKm));
        break;
      case DoctorSort.rating:
        _results.sort((a, b) => b.place.rating.compareTo(a.place.rating));
        break;
      case DoctorSort.openNow:
        _results.sort((a, b) =>
            ((b.place.openNow ?? false) ? 1 : 0)
                .compareTo((a.place.openNow ?? false) ? 1 : 0));
        break;
    }
  }

  double _distanceKm(double lat1, double lon1, double lat2, double lon2) {
    return Geolocator.distanceBetween(lat1, lon1, lat2, lon2) / 1000;
  }

  int _score(DoctorPlace p, double distKm, double radiusKm) {
    final components = <_ScoreComponent>[];

    // Proximity (real).
    final proximity = radiusKm <= 0
        ? 0.0
        : ((radiusKm - distKm).clamp(0.0, radiusKm)) / radiusKm;
    components.add(_ScoreComponent(35, proximity));

    // Rating (real) — Bayesian.
    const priorCount = 20.0;
    const priorMean = 3.5;
    final bayes =
        (p.reviewsCount * p.rating + priorCount * priorMean) / (p.reviewsCount + priorCount);
    components.add(_ScoreComponent(40, (bayes / 5.0).clamp(0.0, 1.0)));

    // Specialty-name confidence: does the place name actually signal this
    // specialty? Boosts obvious matches over generic listings.
    final n = p.name.toLowerCase();
    final nameMatch = p.specialty.nameHints.any(n.contains) ? 1.0 : 0.4;
    components.add(_ScoreComponent(15, nameMatch));

    // Open now (real, when reported).
    if (p.openNow != null) {
      components.add(_ScoreComponent(10, p.openNow! ? 1.0 : 0.0));
    }

    double wSum = 0, wVal = 0;
    for (final c in components) {
      wSum += c.weight;
      wVal += c.weight * c.value;
    }
    if (wSum == 0) return 0;
    return (100 * wVal / wSum).round();
  }
}
