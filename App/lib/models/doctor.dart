/// A medical specialty and how to find matching places on the map.
///
/// NOTE: Google Places does not expose individual physicians, their specialties
/// or schedules. The doctor finder therefore locates CLINICS / SPECIALIST
/// PRACTICES / HOSPITALS matching a specialty keyword — it does not claim to
/// list named doctors it cannot actually retrieve.
class Specialty {
  final String key; // 'cardiology'
  final String label; // 'Cardiologist'
  final String searchKeyword; // Places query
  final List<String> nameHints; // words that signal this specialty in a name

  const Specialty({
    required this.key,
    required this.label,
    required this.searchKeyword,
    required this.nameHints,
  });
}

/// Raw place facts that ARE real from Google Places.
class DoctorPlace {
  final String id;
  final String name;
  final String address;
  final double latitude;
  final double longitude;
  final double rating;
  final int reviewsCount;
  final bool? openNow;
  final Specialty specialty;

  DoctorPlace({
    required this.id,
    required this.name,
    required this.address,
    required this.latitude,
    required this.longitude,
    required this.rating,
    required this.reviewsCount,
    required this.specialty,
    this.openNow,
  });
}

class EnhancedDoctorPlace {
  final DoctorPlace place;
  final double distanceKm;
  final int matchScore;

  EnhancedDoctorPlace({
    required this.place,
    required this.distanceKm,
    required this.matchScore,
  });
}
