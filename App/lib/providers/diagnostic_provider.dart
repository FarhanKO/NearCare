import 'package:flutter/material.dart';
import '../models/diagnostic_center.dart';
import '../services/healthcare_service.dart';
import 'package:geolocator/geolocator.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';

enum SortMode { bestMatch, distance, priceLowToHigh, rating, waitTime }

/// One weighted input to the match score: [weight] out of the active total,
/// [value] normalized to 0..1.
class _ScoreComponent {
  final double weight;
  final double value;
  const _ScoreComponent(this.weight, this.value);
}

class DiagnosticProvider with ChangeNotifier {
  final _service = HealthcareService();

  List<EnhancedCenter> _centers = [];
  List<EnhancedCenter> get centers => _centers;

  List<LatLng> _currentRoutePoints = [];
  List<LatLng> get currentRoutePoints => _currentRoutePoints;

  bool _isLoading = false;
  bool get isLoading => _isLoading;

  String _statusMessage = "";
  String get statusMessage => _statusMessage;

  SortMode _sortMode = SortMode.bestMatch;
  SortMode get sortMode => _sortMode;

  double userLat = 0.0;
  double userLon = 0.0;
  String myLocationLabel = "";
  String selectedTest = ""; // Initially empty

  Future<void> updateLocation(double lat, double lon, String label) async {
    userLat = lat;
    userLon = lon;
    myLocationLabel = label;
    notifyListeners();
    await performSmartSearch();
  }

  Future<void> updateTest(String test) async {
    selectedTest = test;
    notifyListeners();
    await performSmartSearch();
  }

  /// Re-order the already-fetched results without a new network call.
  void setSortMode(SortMode mode) {
    _sortMode = mode;
    _applySort();
    notifyListeners();
  }

  Future<void> performSmartSearch() async {
    _isLoading = true;
    _statusMessage = "Searching Google database for labs...";
    _currentRoutePoints = [];
    notifyListeners();

    try {
      // Track the radius actually used so proximity is scored against it,
      // not a hard-coded 25 km that would flatten every far-away result.
      int radiusMeters = 25000;
      var found = await _service.fetchRealCentersNearby(userLat, userLon, radiusMeters);

      if (found.length < 5) {
        radiusMeters = 50000;
        _statusMessage = "Expanding search radius to 50km...";
        notifyListeners();
        found = await _service.fetchRealCentersNearby(userLat, userLon, radiusMeters);
      }

      final radiusKm = radiusMeters / 1000.0;

      // 1) Attach distance and drop centers that don't match the test category.
      final compatible = found
          .where((c) => _isCompatible(c, selectedTest))
          .map((c) => (
                center: c,
                distanceKm: _calculateDistance(userLat, userLon, c.latitude, c.longitude),
              ))
          .toList();

      // 2) Establish price/wait ranges — but only over REAL data. Estimated
      //    values never enter the ranking (see _calculateScore).
      double? realPriceLow, realPriceHigh;
      for (final rc in compatible) {
        if (rc.center.priceIsEstimated) continue;
        final p = rc.center.testPrices[selectedTest];
        if (p == null) continue;
        realPriceLow = (realPriceLow == null) ? p : (p < realPriceLow ? p : realPriceLow);
        realPriceHigh = (realPriceHigh == null) ? p : (p > realPriceHigh ? p : realPriceHigh);
      }
      int? realWaitLow, realWaitHigh;
      for (final rc in compatible) {
        if (rc.center.waitIsEstimated) continue;
        final w = rc.center.estimatedWaitMinutes;
        realWaitLow = (realWaitLow == null) ? w : (w < realWaitLow ? w : realWaitLow);
        realWaitHigh = (realWaitHigh == null) ? w : (w > realWaitHigh ? w : realWaitHigh);
      }

      // 3) Score every candidate.
      _centers = compatible.map((rc) {
        final c = rc.center;
        final score = _calculateScore(
          c,
          rc.distanceKm,
          radiusKm,
          realPriceLow,
          realPriceHigh,
          realWaitLow,
          realWaitHigh,
        );
        return EnhancedCenter(
          center: c,
          distanceKm: rc.distanceKm,
          testPrice: c.testPrices[selectedTest] ?? 25.0,
          matchScore: score,
          priceIsEstimated: c.priceIsEstimated,
        );
      }).toList();

      // 4) Keep the best 12 by match score, then apply the user's chosen sort.
      _centers.sort((a, b) => b.matchScore.compareTo(a.matchScore));
      _centers = _centers.take(12).toList();
      _applySort();

      _statusMessage = "Found ${_centers.length} centers near you!";

      // Automatically fetch route for the #1 Top Match
      if (_centers.isNotEmpty) {
        await fetchRouteForCenter(_centers.first);
      }
    } catch (e) {
      _statusMessage = "Error connecting to Google Services.";
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> fetchRouteForCenter(EnhancedCenter ec) async {
    final encoded = await _service.fetchRoutePolyline(
      userLat, userLon, ec.center.latitude, ec.center.longitude
    );
    if (encoded != null) {
      _currentRoutePoints = _decodePolyline(encoded);
      _statusMessage = "Route found for ${ec.center.name}";
    } else {
      _currentRoutePoints = [];
      _statusMessage = "Could not fetch road directions. Check API activation.";
    }
    notifyListeners();
  }

  void _applySort() {
    switch (_sortMode) {
      case SortMode.bestMatch:
        _centers.sort((a, b) => b.matchScore.compareTo(a.matchScore));
        break;
      case SortMode.distance:
        _centers.sort((a, b) => a.distanceKm.compareTo(b.distanceKm));
        break;
      case SortMode.priceLowToHigh:
        _centers.sort((a, b) => a.testPrice.compareTo(b.testPrice));
        break;
      case SortMode.rating:
        _centers.sort((a, b) => b.center.rating.compareTo(a.center.rating));
        break;
      case SortMode.waitTime:
        _centers.sort((a, b) =>
            a.center.estimatedWaitMinutes.compareTo(b.center.estimatedWaitMinutes));
        break;
    }
  }

  List<LatLng> _decodePolyline(String encoded) {
    List<LatLng> poly = [];
    int index = 0, len = encoded.length;
    int lat = 0, lng = 0;

    while (index < len) {
      int b, shift = 0, result = 0;
      do {
        b = encoded.codeUnitAt(index++) - 63;
        result |= (b & 0x1f) << shift;
        shift += 5;
      } while (b >= 0x20);
      int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
      lat += dlat;

      shift = 0;
      result = 0;
      do {
        b = encoded.codeUnitAt(index++) - 63;
        result |= (b & 0x1f) << shift;
        shift += 5;
      } while (b >= 0x20);
      int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
      lng += dlng;

      poly.add(LatLng(lat / 1E5, lng / 1E5));
    }
    return poly;
  }

  // ---------------------------------------------------------------------------
  // Category matching
  // ---------------------------------------------------------------------------

  bool _isImagingTest(String t) =>
      t.contains("mri") ||
      t.contains("scan") ||
      t.contains("x-ray") ||
      t.contains("x ray") ||
      t.contains("ct") ||
      t.contains("ultrasound") ||
      t.contains("ecg");

  /// Broad keep/drop filter: could this center plausibly offer this test?
  bool _isCompatible(DiagnosticCenter c, String test) {
    if (test.isEmpty) return true; // no test chosen -> show everything
    final n = c.name.toLowerCase();
    final t = test.toLowerCase();

    if (_isImagingTest(t)) {
      return n.contains("imaging") ||
          n.contains("radiology") ||
          n.contains("scan") ||
          n.contains("diagnostic") ||
          n.contains("hospital") ||
          n.contains("medical") ||
          n.contains("centre") ||
          n.contains("center");
    }
    // Blood work / pathology
    return n.contains("lab") ||
        n.contains("pathology") ||
        n.contains("clinic") ||
        n.contains("diagnostic") ||
        n.contains("hospital") ||
        n.contains("medical") ||
        n.contains("centre") ||
        n.contains("center");
  }

  /// Graded specialization signal (0..1). A name that clearly indicates a
  /// specialized facility outranks a generic one — but a generic-yet-compatible
  /// center is no longer zeroed out the way the old binary bonus did.
  double _specializationSignal(DiagnosticCenter c, String test) {
    if (test.isEmpty) return 0.5;
    final n = c.name.toLowerCase();
    const strong = ["lab", "pathology", "imaging", "radiology", "diagnostic", "scan"];
    const generic = ["hospital", "medical", "clinic", "centre", "center"];
    if (strong.any(n.contains)) return 1.0;
    if (generic.any(n.contains)) return 0.6;
    return 0.3;
  }

  // ---------------------------------------------------------------------------
  // Scoring
  // ---------------------------------------------------------------------------

  double _calculateDistance(double lat1, double lon1, double lat2, double lon2) {
    return Geolocator.distanceBetween(lat1, lon1, lat2, lon2) / 1000;
  }

  /// Weighted match score (0..100) over whichever components have trustworthy
  /// data. Weights are normalized by the active total, so the score stays on a
  /// 0..100 scale whether or not a test is chosen and whether or not real
  /// price/wait data is available.
  int _calculateScore(
    DiagnosticCenter c,
    double distKm,
    double radiusKm,
    double? realPriceLow,
    double? realPriceHigh,
    int? realWaitLow,
    int? realWaitHigh,
  ) {
    final components = <_ScoreComponent>[];

    // Proximity (real) — normalized to the radius actually searched.
    final proximity = radiusKm <= 0
        ? 0.0
        : ((radiusKm - distKm).clamp(0.0, radiusKm)) / radiusKm;
    components.add(_ScoreComponent(35, proximity));

    // Rating (real) — Bayesian average so a 5.0 with a handful of reviews does
    // not beat a strong rating backed by hundreds of reviews.
    const priorCount = 20.0;
    const priorMean = 3.5;
    final bayesRating =
        (c.reviewsCount * c.rating + priorCount * priorMean) / (c.reviewsCount + priorCount);
    components.add(_ScoreComponent(40, (bayesRating / 5.0).clamp(0.0, 1.0)));

    final hasTest = selectedTest.isNotEmpty;

    // Specialization (real, name-based) — only when a test is chosen.
    if (hasTest) {
      components.add(_ScoreComponent(25, _specializationSignal(c, selectedTest)));
    }

    // Price — only scored when it is REAL (cheaper ranks higher).
    if (hasTest && !c.priceIsEstimated && realPriceLow != null && realPriceHigh != null) {
      final price = c.testPrices[selectedTest];
      if (price != null) {
        final span = realPriceHigh - realPriceLow;
        final v = span <= 0 ? 1.0 : ((realPriceHigh - price) / span).clamp(0.0, 1.0);
        components.add(_ScoreComponent(30, v));
      }
    }

    // Wait time — only scored when it is REAL (shorter ranks higher).
    if (!c.waitIsEstimated && realWaitLow != null && realWaitHigh != null) {
      final span = (realWaitHigh - realWaitLow).toDouble();
      final v = span <= 0
          ? 1.0
          : ((realWaitHigh - c.estimatedWaitMinutes) / span).clamp(0.0, 1.0);
      components.add(_ScoreComponent(15, v));
    }

    double weightSum = 0, weightedValue = 0;
    for (final comp in components) {
      weightSum += comp.weight;
      weightedValue += comp.weight * comp.value;
    }
    if (weightSum == 0) return 0;
    return (100 * weightedValue / weightSum).round();
  }
}
