import 'dart:ui' as ui;
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:provider/provider.dart';
import '../../providers/diagnostic_provider.dart';

class MapViewWidget extends StatefulWidget {
  @override
  _MapViewWidgetState createState() => _MapViewWidgetState();
}

class _MapViewWidgetState extends State<MapViewWidget> {
  GoogleMapController? _controller;
  Map<int, BitmapDescriptor> _markerIcons = {};

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    _generateMarkers();
  }

  Future<void> _generateMarkers() async {
    final provider = Provider.of<DiagnosticProvider>(context, listen: false);
    final icons = <int, BitmapDescriptor>{};

    for (var enhanced in provider.centers) {
      final score = enhanced.matchScore;
      if (!_markerIcons.containsKey(score)) {
        icons[score] = await _createCustomMarkerBitmap(score);
      }
    }

    if (icons.isNotEmpty) {
      setState(() {
        _markerIcons.addAll(icons);
      });
    }
  }

  Future<BitmapDescriptor> _createCustomMarkerBitmap(int score) async {
    final ui.PictureRecorder pictureRecorder = ui.PictureRecorder();
    final Canvas canvas = Canvas(pictureRecorder);
    final double size = 100.0;

    final Color color = _getRankColor(score);

    // 1. Draw Circle
    final Paint paint = Paint()
      ..color = color
      ..style = PaintingStyle.fill;
    canvas.drawCircle(Offset(size / 2, size / 2), size / 2.2, paint);

    // 2. Draw White Border
    final Paint borderPaint = Paint()
      ..color = Colors.white
      ..style = PaintingStyle.stroke
      ..strokeWidth = 6.0;
    canvas.drawCircle(Offset(size / 2, size / 2), size / 2.2, borderPaint);

    // 3. Draw Percentage Text
    TextPainter textPainter = TextPainter(textDirection: TextDirection.ltr);
    textPainter.text = TextSpan(
      text: "$score%",
      style: TextStyle(
        fontSize: 32.0,
        fontWeight: FontWeight.bold,
        color: Colors.white,
      ),
    );
    textPainter.layout();
    textPainter.paint(
      canvas,
      Offset(size / 2 - textPainter.width / 2, size / 2 - textPainter.height / 2),
    );

    final ui.Image image = await pictureRecorder.endRecording().toImage(size.toInt(), size.toInt());
    final ByteData? byteData = await image.toByteData(format: ui.ImageByteFormat.png);
    return BitmapDescriptor.fromBytes(byteData!.buffer.asUint8List());
  }

  Color _getRankColor(int score) {
    if (score >= 85) return Color(0xFF4285F4); // Excellent - Blue
    if (score >= 70) return Color(0xFF34A853); // Good - Green
    if (score >= 50) return Color(0xFFFBBC05); // Average - Yellow
    return Color(0xFFEA4335); // Poor - Red
  }

  @override
  Widget build(BuildContext context) {
    final provider = Provider.of<DiagnosticProvider>(context);

    // Ensure map follows centers and fits them all after build
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (_controller != null && provider.centers.isNotEmpty) {
        _fitAllMarkers(provider);
      }
    });

    return GoogleMap(
      initialCameraPosition: CameraPosition(
        target: LatLng(provider.userLat, provider.userLon),
        zoom: 13,
      ),
      myLocationEnabled: true,
      myLocationButtonEnabled: false,
      zoomControlsEnabled: false,
      markers: _buildMarkers(provider),
      polylines: _buildPolylines(provider),
      onMapCreated: (controller) {
        _controller = controller;
      },
    );
  }

  Set<Polyline> _buildPolylines(DiagnosticProvider provider) {
    if (provider.currentRoutePoints.isEmpty) return {};

    return {
      Polyline(
        polylineId: PolylineId("route"),
        points: provider.currentRoutePoints,
        color: Color(0xFF4285F4), // Google Blue
        width: 8, // Increased thickness for visibility
        jointType: JointType.round,
        startCap: Cap.roundCap,
        endCap: Cap.roundCap,
      )
    };
  }

  void _fitAllMarkers(DiagnosticProvider provider) {
    if (provider.centers.isEmpty) return;

    List<LatLng> points = [LatLng(provider.userLat, provider.userLon)];
    for (var ec in provider.centers) {
      points.add(LatLng(ec.center.latitude, ec.center.longitude));
    }

    LatLngBounds bounds = _boundsFromLatLngList(points);
    _controller!.animateCamera(CameraUpdate.newLatLngBounds(bounds, 50.0));
  }

  LatLngBounds _boundsFromLatLngList(List<LatLng> list) {
    double? x0, x1, y0, y1;
    for (LatLng latLng in list) {
      if (x0 == null) {
        x0 = x1 = latLng.latitude;
        y0 = y1 = latLng.longitude;
      } else {
        if (latLng.latitude > x1!) x1 = latLng.latitude;
        if (latLng.latitude < x0) x0 = latLng.latitude;
        if (latLng.longitude > y1!) y1 = latLng.longitude;
        if (latLng.longitude < y0!) y0 = latLng.longitude;
      }
    }
    return LatLngBounds(northeast: LatLng(x1!, y1!), southwest: LatLng(x0!, y0!));
  }

  Set<Marker> _buildMarkers(DiagnosticProvider provider) {
    Set<Marker> markers = {};

    final isBD = provider.myLocationLabel.toLowerCase().contains("bangladesh") ||
                 provider.myLocationLabel.toLowerCase().contains("dhaka") ||
                 provider.myLocationLabel.toLowerCase().contains("bd") ||
                 (provider.userLat >= 20.0 && provider.userLat <= 27.0 && provider.userLon >= 88.0 && provider.userLon <= 93.0);
    final currencySymbol = isBD ? '৳' : '\$';

    // 1. User Marker (Standard Blue Dot handled by myLocationEnabled,
    // but adding a custom one if preferred)
    markers.add(Marker(
      markerId: MarkerId("user"),
      position: LatLng(provider.userLat, provider.userLon),
      icon: BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueAzure),
      infoWindow: InfoWindow(title: "You"),
    ));

    // 2. Center Markers
    for (var enhanced in provider.centers) {
      final c = enhanced.center;
      final score = enhanced.matchScore;
      markers.add(Marker(
        markerId: MarkerId(c.id),
        position: LatLng(c.latitude, c.longitude),
        icon: _markerIcons[score] ?? BitmapDescriptor.defaultMarker,
        infoWindow: InfoWindow(
          title: c.name,
          snippet:
              "Score: $score% | Price: ${enhanced.priceIsEstimated ? '~' : ''}$currencySymbol${enhanced.testPrice.toStringAsFixed(2)}${enhanced.priceIsEstimated ? ' est.' : ''}",
        ),
        onTap: () {
          provider.fetchRouteForCenter(enhanced);
        }
      ));
    }

    return markers;
  }
}
