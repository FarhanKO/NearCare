import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/diagnostic_provider.dart';
import '../providers/pharmacy_provider.dart';
import '../providers/doctor_provider.dart';
import 'dashboard_page.dart';
import 'pharmacy_page.dart';
import 'doctor_page.dart';
import 'widgets/prescription_scanner_sheet.dart';
import 'dart:ui';
import 'package:http/http.dart' as http;
import 'dart:convert';

class LandingPage extends StatefulWidget {
  @override
  _LandingPageState createState() => _LandingPageState();
}

class _LandingPageState extends State<LandingPage> with TickerProviderStateMixin {
  final TextEditingController _areaController = TextEditingController();
  final TextEditingController _testController = TextEditingController();

  bool isAreaFocused = false;
  bool isSearchingLocation = false;
  bool isResolvingLocation = false;
  List<Map<String, dynamic>> locationSuggestions = [];

  // Animations
  late AnimationController _mainFadeController;
  bool triggerAnimate = false;

  @override
  void initState() {
    super.initState();
    _mainFadeController = AnimationController(vsync: this, duration: Duration(milliseconds: 1500));

    Future.delayed(Duration(milliseconds: 100), () {
      setState(() => triggerAnimate = true);
      _mainFadeController.forward();
    });
  }

  @override
  void dispose() {
    _mainFadeController.dispose();
    super.dispose();
  }

  Future<void> _searchPlaces(String query) async {
    if (query.length < 2) {
      setState(() => locationSuggestions = []);
      return;
    }
    setState(() => isSearchingLocation = true);
    try {
      final url = Uri.parse("https://photon.komoot.io/api/?q=${Uri.encodeComponent(query)}&limit=5");
      final response = await http.get(url);
      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        final List features = data['features'];
        setState(() {
          locationSuggestions = features.map((f) {
            final props = f['properties'];
            final coords = f['geometry']['coordinates'];
            String label = [props['name'], props['city'], props['country']]
                .where((e) => e != null)
                .join(", ");
            return {
              'label': label,
              'lat': coords[1],
              'lon': coords[0],
            };
          }).toList();
        });
      }
    } catch (e) {
      print("Search Error: $e");
    } finally {
      setState(() => isSearchingLocation = false);
    }
  }

  Future<Map<String, double>?> _resolveNominatim(String query) async {
    try {
      final url = Uri.parse("https://nominatim.openstreetmap.org/search?q=${Uri.encodeComponent(query)}&format=json&limit=1");
      final response = await http.get(url, headers: {"User-Agent": "NearCareApp/1.0"});
      if (response.statusCode == 200) {
        final List data = json.decode(response.body);
        if (data.isNotEmpty) {
          return {
            'lat': double.parse(data[0]['lat']),
            'lon': double.parse(data[0]['lon']),
          };
        }
      }
    } catch (e) {
      print("Nominatim Error: $e");
    }
    return null;
  }

  /// Resolves the entered area to coordinates (suggestion first, then geocode).
  Future<Map<String, dynamic>?> _resolveArea() async {
    if (_areaController.text.isEmpty) return null;

    Map<String, dynamic>? selected;
    try {
      selected = locationSuggestions.firstWhere((s) => s['label'] == _areaController.text);
    } catch (e) {
      selected = null;
    }

    if (selected != null) {
      return {
        'lat': (selected['lat'] as num).toDouble(),
        'lon': (selected['lon'] as num).toDouble(),
        'label': selected['label'],
      };
    }

    setState(() => isResolvingLocation = true);
    final coords = await _resolveNominatim(_areaController.text);
    setState(() => isResolvingLocation = false);
    if (coords != null) {
      return {'lat': coords['lat']!, 'lon': coords['lon']!, 'label': _areaController.text};
    }
    return null;
  }

  void _startSearch() async {
    if (_areaController.text.isEmpty) return;

    final provider = Provider.of<DiagnosticProvider>(context, listen: false);
    provider.selectedTest = _testController.text;

    final area = await _resolveArea();
    if (area != null) {
      await provider.updateLocation(area['lat'], area['lon'], area['label']);
      if (!mounted) return;
      Navigator.push(context, MaterialPageRoute(builder: (context) => DashboardPage()));
    } else {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("Location not found. Please select from suggestions or try another area.")),
      );
    }
  }

  /// Opens the pharmacy or doctor finder for the entered area.
  Future<void> _openFinder(String kind) async {
    if (_areaController.text.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Enter your area first.")),
      );
      return;
    }

    final area = await _resolveArea();
    if (!mounted) return;
    if (area == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Location not found. Try another area.")),
      );
      return;
    }

    if (kind == 'pharmacy') {
      final provider = Provider.of<PharmacyProvider>(context, listen: false);
      await provider.updateLocation(area['lat'], area['lon'], area['label']);
      if (!mounted) return;
      Navigator.push(context, MaterialPageRoute(builder: (_) => const PharmacyPage()));
    } else {
      final provider = Provider.of<DoctorProvider>(context, listen: false);
      provider.setLocation(area['lat'], area['lon'], area['label']);
      Navigator.push(context, MaterialPageRoute(builder: (_) => const DoctorPage()));
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        width: double.infinity,
        height: double.infinity,
        decoration: BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            colors: [Color(0xFF061B3B), Color(0xFF0D4EA3), Color(0xFF0A101C)],
          ),
        ),
        child: Stack(
          children: [
            Center(
              child: SingleChildScrollView(
                padding: EdgeInsets.symmetric(horizontal: 20, vertical: 40),
                child: Column(
                  children: [
                    // 1. BRANDING: STAGGERED REVEAL
                    _buildBranding(),

                    SizedBox(height: 12),

                    // 2. DISPLAY TITLE
                    _buildAnimatedText(
                      "Find the Best Diagnostic Center Near You",
                      delay: 300,
                      fontSize: 26,
                      fontWeight: FontWeight.w500,
                    ),

                    SizedBox(height: 32),

                    // 3. MAIN OPTIONS CARD
                    _buildMainCard(),

                    SizedBox(height: 32),

                    // 4. STATS BADGES
                    _buildStatsRow(),
                  ],
                ),
              ),
            ),
            if (isResolvingLocation)
               Container(
                 color: Colors.black45,
                 child: Center(child: CircularProgressIndicator(color: Colors.white)),
               )
          ],
        ),
      ),
    );
  }

  Widget _buildBranding() {
    String brand = "NearCare";
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: List.generate(brand.length, (index) {
        return _buildAnimatedLetter(brand[index], index);
      }),
    );
  }

  Widget _buildAnimatedLetter(String char, int index) {
    return TweenAnimationBuilder<double>(
      duration: Duration(milliseconds: 600),
      curve: Curves.fastOutSlowIn,
      tween: Tween(begin: 0.0, end: triggerAnimate ? 1.0 : 0.0),
      builder: (context, value, child) {
        return Opacity(
          opacity: value,
          child: Transform.translate(
            offset: Offset(0, 20 * (1 - value)),
            child: ImageFiltered(
              imageFilter: ImageFilter.blur(sigmaX: 10 * (1 - value), sigmaY: 10 * (1 - value)),
              child: Text(
                char,
                style: TextStyle(
                  fontSize: 40,
                  fontWeight: FontWeight.w900,
                  color: index < 4 ? Color(0xFF93C5FD) : Colors.white,
                  letterSpacing: -1,
                ),
              ),
            ),
          ),
        );
      },
    );
  }

  Widget _buildAnimatedText(String text, {int delay = 0, double fontSize = 18, FontWeight fontWeight = FontWeight.normal}) {
    return TweenAnimationBuilder<double>(
      duration: Duration(milliseconds: 800),
      curve: Curves.easeOut,
      tween: Tween(begin: 0.0, end: triggerAnimate ? 1.0 : 0.0),
      builder: (context, value, child) {
        return Opacity(
          opacity: value,
          child: Transform.translate(
            offset: Offset(0, 30 * (1 - value)),
            child: Text(
              text,
              textAlign: TextAlign.center,
              style: TextStyle(
                fontSize: fontSize,
                color: Colors.white,
                fontWeight: fontWeight,
              ),
            ),
          ),
        );
      },
    );
  }

  Widget _buildMainCard() {
    return AnimatedOpacity(
      duration: Duration(milliseconds: 1000),
      opacity: triggerAnimate ? 1.0 : 0.0,
      child: Card(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
        elevation: 8,
        color: Colors.white,
        child: Padding(
          padding: EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text("1. ENTER YOUR CLINICAL AREA", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 11, color: Color(0xFF0D4EA3), letterSpacing: 0.5)),
              SizedBox(height: 8),
              _buildAreaField(),

              SizedBox(height: 16),

              Text("2. SEARCH TEST OR SCAN REPORT", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 11, color: Color(0xFF0D4EA3), letterSpacing: 0.5)),
              SizedBox(height: 8),
              _buildTestField(),

              SizedBox(height: 24),

              ElevatedButton(
                onPressed: _startSearch,
                style: ElevatedButton.styleFrom(
                  backgroundColor: Color(0xFF0D4EA3),
                  minimumSize: Size(double.infinity, 56),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(14)),
                ),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Icon(Icons.search, color: Colors.white),
                    SizedBox(width: 8),
                    Text("Find Best Centers", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16, color: Colors.white)),
                  ],
                ),
              ),

              SizedBox(height: 12),

              // Secondary finders — pharmacies & doctors for the same area.
              Row(
                children: [
                  Expanded(
                    child: OutlinedButton.icon(
                      onPressed: () => _openFinder('pharmacy'),
                      icon: const Icon(Icons.local_pharmacy, color: Color(0xFF0D4EA3), size: 20),
                      label: const Text("Pharmacies", style: TextStyle(color: Color(0xFF0D4EA3), fontWeight: FontWeight.bold)),
                      style: OutlinedButton.styleFrom(
                        minimumSize: const Size(0, 48),
                        side: const BorderSide(color: Color(0xFF0D4EA3)),
                        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                      ),
                    ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: OutlinedButton.icon(
                      onPressed: () => _openFinder('doctor'),
                      icon: const Icon(Icons.medical_services, color: Color(0xFF0D4EA3), size: 20),
                      label: const Text("Doctors", style: TextStyle(color: Color(0xFF0D4EA3), fontWeight: FontWeight.bold)),
                      style: OutlinedButton.styleFrom(
                        minimumSize: const Size(0, 48),
                        side: const BorderSide(color: Color(0xFF0D4EA3)),
                        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                      ),
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildAreaField() {
    return Stack(
      children: [
        TextField(
          controller: _areaController,
          onChanged: _searchPlaces,
          onTap: () => setState(() => isAreaFocused = true),
          decoration: InputDecoration(
            hintText: "Enter clinical area...",
            prefixIcon: Icon(Icons.location_on, color: Color(0xFF0D4EA3)),
            suffixIcon: IconButton(
              icon: Icon(Icons.my_location, color: Color(0xFF0D4EA3)),
              onPressed: () {}, // Permission trigger would go here
            ),
            filled: true,
            fillColor: Color(0xFFF1F5F9),
            border: OutlineInputBorder(borderRadius: BorderRadius.circular(12), borderSide: BorderSide.none),
          ),
        ),
        if (isAreaFocused && locationSuggestions.isNotEmpty)
          Container(
            margin: EdgeInsets.only(top: 60),
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(12),
              boxShadow: [BoxShadow(color: Colors.black12, blurRadius: 10)],
            ),
            child: Column(
              children: locationSuggestions.map((s) {
                return ListTile(
                  leading: Icon(Icons.location_on, size: 16, color: Color(0xFF0D4EA3)),
                  title: Text(s['label'], style: TextStyle(fontSize: 13)),
                  onTap: () {
                    _areaController.text = s['label'];
                    setState(() {
                      locationSuggestions = [];
                      isAreaFocused = false;
                    });
                  },
                );
              }).toList(),
            ),
          ),
      ],
    );
  }

  Widget _buildTestField() {
    return TextField(
      controller: _testController,
      decoration: InputDecoration(
        hintText: "Search test...",
        prefixIcon: Icon(Icons.search, color: Color(0xFF0D4EA3)),
        suffixIcon: IconButton(
          icon: Icon(Icons.camera_alt, color: Colors.orange),
          onPressed: _showScannerDialog,
        ),
        filled: true,
        fillColor: Color(0xFFF1F5F9),
        border: OutlineInputBorder(borderRadius: BorderRadius.circular(12), borderSide: BorderSide.none),
      ),
    );
  }

  Widget _buildStatsRow() {
    return Row(
      children: [
        _buildStatsBadge("500+", "LAB CENTERS"),
        SizedBox(width: 8),
        _buildStatsBadge("35+", "CLINICAL TESTS"),
        SizedBox(width: 8),
        _buildStatsBadge("10K+", "PATIENTS SERVED"),
      ],
    );
  }

  Widget _buildStatsBadge(String big, String sub) {
    return Expanded(
      child: Container(
        padding: EdgeInsets.all(12),
        decoration: BoxDecoration(
          color: Colors.white.withOpacity(0.12),
          borderRadius: BorderRadius.circular(16),
          border: Border.all(color: Colors.white.withOpacity(0.22), width: 1.6),
        ),
        child: Column(
          children: [
            Text(big, style: TextStyle(color: Colors.white, fontWeight: FontWeight.w900, fontSize: 18)),
            SizedBox(height: 2),
            Text(sub, textAlign: TextAlign.center, style: TextStyle(color: Color(0xFFE2F0FF), fontWeight: FontWeight.bold, fontSize: 8)),
          ],
        ),
      ),
    );
  }

  /// Real prescription scanner: capture -> on-device OCR -> recognized items.
  void _showScannerDialog() {
    showDialog(
      context: context,
      builder: (context) => PrescriptionScannerSheet(
        onDone: (tests, medicines) {
          if (tests.isEmpty && medicines.isEmpty) return;
          setState(() {
            _testController.text =
                tests.isNotEmpty ? tests.join(", ") : medicines.join(", ");
          });
          // Only auto-search if we already have an area to search in.
          if (_areaController.text.isNotEmpty) {
            _startSearch();
          }
        },
      ),
    );
  }
}
