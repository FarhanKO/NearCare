import 'dart:math';
import 'package:google_maps_webservice/places.dart' as places;
import 'package:google_maps_webservice/directions.dart' as directions;
import '../config.dart';
import '../models/diagnostic_center.dart';
import '../models/pharmacy.dart';
import '../models/doctor.dart';

class HealthcareService {
  final _places = places.GoogleMapsPlaces(apiKey: AppConfig.googleMapsApiKey);
  final _directions = directions.GoogleMapsDirections(apiKey: AppConfig.googleMapsApiKey);

  Future<List<DiagnosticCenter>> fetchRealCentersNearby(double lat, double lon, int radius) async {
    // Perform parallel searches with generic category keywords for coverage.
    // (Do NOT put a specific brand name here — it biases every result set.)
    final keywords = [
      "diagnostic centre pathology lab",
      "medical imaging radiology scan",
      "clinic hospital blood test",
    ];

    final searchJobs = keywords.map((kw) => _places.searchNearbyWithRadius(
      places.Location(lat: lat, lng: lon),
      radius,
      keyword: kw,
    ));

    final responses = await Future.wait(searchJobs);
    final allResults = <places.PlacesSearchResult>[];

    for (var res in responses) {
      print("Search Result Status [${res.status}]");
      if (res.status == "OK") {
        allResults.addAll(res.results);
      }
    }

    // Deduplicate by placeId
    final uniqueMap = <String, places.PlacesSearchResult>{};
    for (var r in allResults) {
      uniqueMap[r.placeId] = r;
    }

    final uniqueResults = uniqueMap.values.toList();
    print("Total Unique Centers Found: ${uniqueResults.length}");

    final centers = <DiagnosticCenter>[];
    for (final p in uniqueResults) {
      // Skip malformed results instead of force-unwrapping geometry (crash-safe).
      final geometry = p.geometry;
      if (geometry == null) continue;

      // NOTE: Google Places does not return prices, wait times, crowd level,
      // certification or hours. The values below are HEURISTIC ESTIMATES derived
      // from a stable per-name seed so the UI has something to show — they are
      // flagged as estimates (priceIsEstimated / waitIsEstimated) and are ignored
      // by the ranker until a real data source is wired in.
      final seed = p.name.hashCode.abs();
      final waitTime = 10 + (seed % 45);
      final basePrice = 12.0 + (seed % 25);

      centers.add(DiagnosticCenter(
        id: p.placeId,
        name: p.name,
        address: p.vicinity ?? "Nearby Location",
        suburb: _extractSuburb(p.vicinity),
        latitude: geometry.location.lat,
        longitude: geometry.location.lng,
        rating: p.rating?.toDouble() ?? 3.5,
        reviewsCount: p.userRatingsTotal?.toInt() ?? 0,
        crowdLevel: waitTime < 20 ? "Low" : (waitTime < 35 ? "Moderate" : "High"),
        estimatedWaitMinutes: waitTime,
        phone: "Tap to call",
        timing: "08:00 AM - 08:30 PM",
        certified: false,
        testPrices: {
          "CBC": basePrice,
          "Lipid": basePrice + 12,
          "MRI Brain": basePrice * 11,
          "X-Ray Chest": basePrice + 18,
          "Glucose": basePrice - 4,
          "CT Scan Abdomen": basePrice * 9,
        },
        priceIsEstimated: true,
        waitIsEstimated: true,
      ));
    }
    return centers;
  }

  /// Nearby hospitals (real). Used to compute "near a hospital" proximity for
  /// pharmacies and as anchors for care.
  Future<List<HospitalRef>> fetchNearbyHospitals(double lat, double lon, int radius) async {
    final res = await _places.searchNearbyWithRadius(
      places.Location(lat: lat, lng: lon),
      radius,
      type: "hospital",
    );
    final out = <HospitalRef>[];
    if (res.status != "OK") return out;
    for (final p in res.results) {
      final g = p.geometry;
      if (g == null) continue;
      out.add(HospitalRef(name: p.name, latitude: g.location.lat, longitude: g.location.lng));
    }
    return out;
  }

  /// Nearby pharmacies (real facts only — tier/availability are inferred later
  /// by the provider, not here).
  Future<List<Pharmacy>> fetchNearbyPharmacies(double lat, double lon, int radius) async {
    final res = await _places.searchNearbyWithRadius(
      places.Location(lat: lat, lng: lon),
      radius,
      type: "pharmacy",
    );
    final unique = <String, places.PlacesSearchResult>{};
    if (res.status == "OK") {
      for (final r in res.results) {
        unique[r.placeId] = r;
      }
    }
    final out = <Pharmacy>[];
    for (final p in unique.values) {
      final g = p.geometry;
      if (g == null) continue;
      out.add(Pharmacy(
        id: p.placeId,
        name: p.name,
        address: p.vicinity ?? "Nearby Location",
        latitude: g.location.lat,
        longitude: g.location.lng,
        rating: p.rating?.toDouble() ?? 3.5,
        reviewsCount: p.userRatingsTotal?.toInt() ?? 0,
        openNow: p.openingHours?.openNow,
      ));
    }
    return out;
  }

  /// Clinics / specialist practices / hospitals matching a specialty keyword.
  /// (Places cannot return individual named doctors — see [Specialty] docs.)
  Future<List<DoctorPlace>> fetchDoctorPlaces(
      double lat, double lon, int radius, Specialty specialty) async {
    final res = await _places.searchNearbyWithRadius(
      places.Location(lat: lat, lng: lon),
      radius,
      keyword: specialty.searchKeyword,
    );
    final unique = <String, places.PlacesSearchResult>{};
    if (res.status == "OK") {
      for (final r in res.results) {
        unique[r.placeId] = r;
      }
    }
    final out = <DoctorPlace>[];
    for (final p in unique.values) {
      final g = p.geometry;
      if (g == null) continue;
      out.add(DoctorPlace(
        id: p.placeId,
        name: p.name,
        address: p.vicinity ?? "Nearby Location",
        latitude: g.location.lat,
        longitude: g.location.lng,
        rating: p.rating?.toDouble() ?? 3.5,
        reviewsCount: p.userRatingsTotal?.toInt() ?? 0,
        openNow: p.openingHours?.openNow,
        specialty: specialty,
      ));
    }
    return out;
  }

  String _extractSuburb(String? vicinity) {
    if (vicinity == null) return "Local Area";
    final parts = vicinity.split(',');
    return parts.length > 1 ? parts[parts.length - 2].trim() : "Local Area";
  }

  Future<String?> fetchRoutePolyline(double startLat, double startLon, double endLat, double endLon) async {
    try {
      final response = await _directions.directionsWithLocation(
        directions.Location(lat: startLat, lng: startLon),
        directions.Location(lat: endLat, lng: endLon),
        travelMode: directions.TravelMode.driving,
      );

      print("Directions API Status: ${response.status}");
      if (response.errorMessage != null) {
        print("Directions API Error: ${response.errorMessage}");
      }

      if (response.status == "OK" && response.routes.isNotEmpty) {
        return response.routes[0].overviewPolyline.points;
      }
    } catch (e) {
      print("Directions Exception: $e");
    }
    return null;
  }
}
