/// A pharmacy tier inferred from name, review volume and hospital proximity.
///
/// IMPORTANT: no map API returns a pharmacy's physical "size" or its medicine
/// inventory. Tier is a HEURISTIC (see PharmacyProvider._inferTier) and is
/// surfaced in the UI as "inferred". It becomes real only when a real data
/// source is wired in.
enum PharmacyTier { small, medium, chainBrand, hospitalPharmacy }

extension PharmacyTierInfo on PharmacyTier {
  /// Rough breadth-of-stock signal (0..1): the user's premise is that hospital
  /// and big-brand pharmacies carry medicines small shops do not.
  double get breadth {
    switch (this) {
      case PharmacyTier.hospitalPharmacy:
        return 1.0;
      case PharmacyTier.chainBrand:
        return 0.85;
      case PharmacyTier.medium:
        return 0.6;
      case PharmacyTier.small:
        return 0.35;
    }
  }

  String get label {
    switch (this) {
      case PharmacyTier.hospitalPharmacy:
        return "Hospital pharmacy";
      case PharmacyTier.chainBrand:
        return "Brand / chain";
      case PharmacyTier.medium:
        return "Mid-sized";
      case PharmacyTier.small:
        return "Small";
    }
  }
}

/// Raw pharmacy facts that ARE real from Google Places.
class Pharmacy {
  final String id;
  final String name;
  final String address;
  final double latitude;
  final double longitude;
  final double rating;
  final int reviewsCount;
  final bool? openNow; // null when Places doesn't report it

  Pharmacy({
    required this.id,
    required this.name,
    required this.address,
    required this.latitude,
    required this.longitude,
    required this.rating,
    required this.reviewsCount,
    this.openNow,
  });
}

/// A scored pharmacy result. Tier and availability are inferred/estimated and
/// flagged as such so the UI can label them honestly.
class EnhancedPharmacy {
  final Pharmacy pharmacy;
  final double distanceKm;
  final PharmacyTier tier;
  final bool tierIsEstimated;
  final double? distanceToNearestHospitalKm;

  /// Projected 0..1 availability of the searched medicines. Derived from tier
  /// breadth while estimated; replaced by real stock data if a source exists.
  final double projectedAvailability;
  final bool availabilityIsEstimated;

  final int matchScore;
  final List<String> searchedMedicines;

  EnhancedPharmacy({
    required this.pharmacy,
    required this.distanceKm,
    required this.tier,
    required this.matchScore,
    required this.projectedAvailability,
    this.tierIsEstimated = true,
    this.availabilityIsEstimated = true,
    this.distanceToNearestHospitalKm,
    this.searchedMedicines = const [],
  });

  int get availabilityPercent => (projectedAvailability * 100).round();
}

/// Lightweight hospital location used to compute "near a hospital" proximity.
class HospitalRef {
  final String name;
  final double latitude;
  final double longitude;
  const HospitalRef({
    required this.name,
    required this.latitude,
    required this.longitude,
  });
}
