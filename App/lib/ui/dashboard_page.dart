import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/diagnostic_provider.dart';
import 'widgets/map_view.dart';

class DashboardPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final provider = Provider.of<DiagnosticProvider>(context);
    final isBD = provider.myLocationLabel.toLowerCase().contains("bangladesh") ||
                 provider.myLocationLabel.toLowerCase().contains("dhaka") ||
                 provider.myLocationLabel.toLowerCase().contains("bd") ||
                 (provider.userLat >= 20.0 && provider.userLat <= 27.0 && provider.userLon >= 88.0 && provider.userLon <= 93.0);
    final currencySymbol = isBD ? '৳' : '\$';

    return Scaffold(
      backgroundColor: Color(0xFFF1F5F9),
      body: SafeArea(
        child: Column(
          children: [
            // 1. MAP SECTION
            Container(
              height: 300,
              child: Stack(
                children: [
                  MapViewWidget(),
                  Positioned(
                    top: 16,
                    left: 16,
                    child: _buildLegend(),
                  ),
                  if (provider.isLoading)
                    Center(child: CircularProgressIndicator()),
                ],
              ),
            ),

            // 2. RESULTS LIST
            Expanded(
              child: ListView.builder(
                padding: EdgeInsets.all(16),
                itemCount: provider.centers.length,
                itemBuilder: (context, index) {
                  final enhanced = provider.centers[index];
                  return _buildCenterCard(enhanced);
                },
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildLegend() {
    return Container(
      padding: EdgeInsets.all(6),
      decoration: BoxDecoration(
        color: Colors.white.withOpacity(0.85),
        borderRadius: BorderRadius.circular(8),
        border: Border.all(color: Colors.black12),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _legendItem("🔵 Excellent"),
          _legendItem("🟢 Good"),
          _legendItem("🟡 Average"),
          _legendItem("🔴 Poor"),
        ],
      ),
    );
  }

  Widget _legendItem(String text) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 1.0),
      child: Text(
        text,
        style: TextStyle(fontSize: 8, fontWeight: FontWeight.bold, color: Color(0xFF1E293B))
      ),
    );
  }

  Widget _buildCenterCard(EnhancedCenter enhanced) {
    final c = enhanced.center;
    return Card(
      margin: EdgeInsets.only(bottom: 12),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: Padding(
        padding: EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(c.name, style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                Container(
                  padding: EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                  decoration: BoxDecoration(
                    color: Colors.blue.shade50,
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Text("${enhanced.matchScore}% MATCH", style: TextStyle(color: Colors.blue, fontWeight: FontWeight.bold, fontSize: 10)),
                ),
              ],
            ),
            SizedBox(height: 4),
            Text(c.address, style: TextStyle(color: Colors.grey, fontSize: 12)),
            SizedBox(height: 12),
            Row(
              children: [
                Icon(Icons.star, color: Colors.orange, size: 16),
                Text(" ${c.rating} (${c.reviewsCount} reviews)", style: TextStyle(fontSize: 12, fontWeight: FontWeight.w600)),
                Spacer(),
                Column(
                  crossAxisAlignment: CrossAxisAlignment.end,
                  children: [
                    Text(
                      "${enhanced.priceIsEstimated ? '~' : ''}$currencySymbol${enhanced.testPrice.toStringAsFixed(2)}",
                      style: TextStyle(fontWeight: FontWeight.w900, fontSize: 18, color: Colors.blue),
                    ),
                    if (enhanced.priceIsEstimated)
                      Text("est. price", style: TextStyle(fontSize: 9, color: Colors.grey)),
                  ],
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
