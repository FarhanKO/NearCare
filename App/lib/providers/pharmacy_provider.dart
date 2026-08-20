import 'package:flutter/material.dart';
import 'package:geolocator/geolocator.dart';
import '../models/pharmacy.dart';
import '../services/healthcare_service.dart';

enum PharmacySort { bestMatch, distance, availability, rating, openNow }

class _ScoreComponent {
  final double weight;
  final double value;
  const _ScoreComponent(this.weight, this.value);
}

class PharmacyProvider with ChangeNotifier {
  final _service = HealthcareService();

  // Known pharmacy chains / brands used to infer "brand" tier. Edit for your
  // region — this is a heuristic, not a definitive list.
  static const List<String> _brands = [
    'pharmacy', 'pharma', 'chemist', 'medico', 'medicine corner',
    'lazz', 'wellbeing', 'guardian', 'arogga', 'tamanna', 'apollo',
    'square', 'popular', 'cvs', 'walgreens', 'boots',
  ];

  List<EnhancedPharmacy> _results = [];
  List<EnhancedPharmacy> get results => _results;

  bool _isLoading = false;
  bool get isLoading => _isLoading;

  String _statusMessage = "";
  String get statusMessage => _statusMessage;

  PharmacySort _sort = PharmacySort.bestMatch;
  PharmacySort get sort => _sort;

  double userLat = 0.0;
  double userLon = 0.0;
  String locationLabel = "";
  List<String> searchedMedicines = [];

  Future<void> updateLocation(double lat, double lon, String label) async {
    userLat = lat;
    userLon = lon;
    locationLabel = label;
    notifyListeners();
    await search();
  }

  Future<void> updateMedicines(String raw) async {
    searchedMedicines = raw
        .split(',')
        .map((s) => s.trim())
        .where((s) => s.isNotEmpty)
        .toList();
    notifyListeners();
    await search();
  }

  void setSort(PharmacySort s) {
    _sort = s;
    _applySort();
    notifyListeners();
  }

  Future<void> search() async {
    _isLoading = true;
    _statusMessage = "Finding nearby pharmacies...";
    notifyListeners();

    try {
      int radiusMeters = 5000;
      var hospitals = await _service.fetchNearbyHospitals(userLat, userLon, radiusMeters);
      var pharmacies = await _service.fetchNearbyPharmacies(userLat, userLon, radiusMeters);

      if (pharmacies.length < 5) {
        radiusMeters = 15000;
        _statusMessage = "Expanding search radius...";
        notifyListeners();
        hospitals = await _service.fetchNearbyHospitals(userLat, userLon, radiusMeters);
        pharmacies = await _service.fetchNearbyPharmacies(userLat, userLon, radiusMeters);
      }

      final radiusKm = radiusMeters / 1000.0;

      _results = pharmacies.map((p) {
        final distKm = _distanceKm(userLat, userLon, p.latitude, p.longitude);
        final nearestHospitalKm = _nearestHospitalKm(p, hospitals);
        final tier = _inferTier(p, nearestHospitalKm);

        // Availability is projected from tier breadth while estimated.
        final projected = tier.breadth;

        final score = _scorePharmacy(
          p,
          distKm,
          radiusKm,
          tier,
          nearestHospitalKm,
        );

        return EnhancedPharmacy(
          pharmacy: p,
          distanceKm: distKm,
          tier: tier,
          matchScore: score,
          projectedAvailability: projected,
          tierIsEstimated: true,
          availabilityIsEstimated: true,
          distanceToNearestHospitalKm: nearestHospitalKm,
          searchedMedicines: searchedMedicines,
        );
      }).toList();

      _results.sort((a, b) => b.matchScore.compareTo(a.matchScore));
      _results = _results.take(15).toList();
      _applySort();

      _statusMessage = "Found ${_results.length} pharmacies near you!";
    } catch (e) {
      _statusMessage = "Error finding pharmacies.";
      _results = [];
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  void _applySort() {
    switch (_sort) {
      case PharmacySort.bestMatch:
        _results.sort((a, b) => b.matchScore.compareTo(a.matchScore));
        break;
      case PharmacySort.distance:
        _results.sort((a, b) => a.distanceKm.compareTo(b.distanceKm));
        break;
      case PharmacySort.availability:
        _results.sort((a, b) =>
            b.projectedAvailability.compareTo(a.projectedAvailability));
        break;
      case PharmacySort.rating:
        _results.sort((a, b) => b.pharmacy.rating.compareTo(a.pharmacy.rating));
        break;
      case PharmacySort.openNow:
        _results.sort((a, b) =>
            ((b.pharmacy.openNow ?? false) ? 1 : 0)
                .compareTo((a.pharmacy.openNow ?? false) ? 1 : 0));
        break;
    }
  }

  double _distanceKm(double lat1, double lon1, double lat2, double lon2) {
    return Geolocator.distanceBetween(lat1, lon1, lat2, lon2) / 1000;
  }

  double? _nearestHospitalKm(Pharmacy p, List<HospitalRef> hospitals) {
    if (hospitals.isEmpty) return null;
    double? best;
    for (final h in hospitals) {
      final d = _distanceKm(p.latitude, p.longitude, h.latitude, h.longitude);
      if (best == null || d < best) best = d;
    }
    return best;
  }

  PharmacyTier _inferTier(Pharmacy p, double? nearestHospitalKm) {
    final n = p.name.toLowerCase();
    if ((nearestHospitalKm != null && nearestHospitalKm <= 0.25) ||
        n.contains('hospital')) {
      return PharmacyTier.hospitalPharmacy;
    }
    if (_brands.any(n.contains)) return PharmacyTier.chainBrand;
    if (p.reviewsCount >= 80) return PharmacyTier.medium;
    return PharmacyTier.small;
  }

  /// Weighted score (0..100). Because tier/availability are the same inferred
  /// signal, we score tier breadth directly (the "size" ranking the product is
  /// built on) rather than double-counting a separate estimated availability.
  /// When a real inventory source is wired in, swap the tier term for a real
  /// availability term.
  int _scorePharmacy(
    Pharmacy p,
    double distKm,
    double radiusKm,
    PharmacyTier tier,
    double? nearestHospitalKm,
  ) {
    final components = <_ScoreComponent>[];

    // Proximity to user (real).
    final proximity = radiusKm <= 0
        ? 0.0
        : ((radiusKm - distKm).clamp(0.0, radiusKm)) / radiusKm;
    components.add(_ScoreComponent(30, proximity));

    // Rating (real) — Bayesian to dampen tiny review counts.
    const priorCount = 20.0;
    const priorMean = 3.5;
    final bayes =
        (p.reviewsCount * p.rating + priorCount * priorMean) / (p.reviewsCount + priorCount);
    components.add(_ScoreComponent(25, (bayes / 5.0).clamp(0.0, 1.0)));

    // Size / stock breadth (inferred) — the pharmacy-specific ranking signal.
    components.add(_ScoreComponent(30, tier.breadth));

    // Near a hospital (real) — convenience + likelihood of specialist meds.
    if (nearestHospitalKm != null) {
      final v = (1.0 - (nearestHospitalKm / 5.0)).clamp(0.0, 1.0);
      components.add(_ScoreComponent(15, v));
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
