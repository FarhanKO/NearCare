class DiagnosticCenter {
  final String id;
  final String name;
  final String address;
  final double latitude;
  final String suburb;
  final double longitude;
  final double rating;
  final int reviewsCount;
  final String crowdLevel;
  final int estimatedWaitMinutes;
  final String phone;
  final String timing;
  final bool certified;
  final Map<String, double> testPrices;
  bool isFavorite;

  // Google Places only returns name, rating, review count and location.
  // Everything price/wait related is a heuristic estimate, not real data.
  // These flags let the UI label it honestly and the ranker ignore it until
  // a real price/wait source is wired in.
  final bool priceIsEstimated;
  final bool waitIsEstimated;

  DiagnosticCenter({
    required this.id,
    required this.name,
    required this.address,
    required this.suburb,
    required this.latitude,
    required this.longitude,
    required this.rating,
    required this.reviewsCount,
    required this.crowdLevel,
    required this.estimatedWaitMinutes,
    required this.phone,
    required this.timing,
    required this.certified,
    required this.testPrices,
    this.isFavorite = false,
    this.priceIsEstimated = true,
    this.waitIsEstimated = true,
  });
}

class EnhancedCenter {
  final DiagnosticCenter center;
  final double distanceKm;
  final double testPrice;
  final int matchScore;
  final bool priceIsEstimated;

  EnhancedCenter({
    required this.center,
    required this.distanceKm,
    required this.testPrice,
    required this.matchScore,
    this.priceIsEstimated = true,
  });
}
