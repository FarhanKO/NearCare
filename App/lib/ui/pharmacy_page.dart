import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../models/pharmacy.dart';
import '../providers/pharmacy_provider.dart';

class PharmacyPage extends StatelessWidget {
  const PharmacyPage({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final provider = Provider.of<PharmacyProvider>(context);

    return Scaffold(
      backgroundColor: const Color(0xFFF1F5F9),
      appBar: AppBar(
        backgroundColor: const Color(0xFF0D4EA3),
        title: const Text("Nearby Pharmacies"),
      ),
      body: Column(
        children: [
          Container(
            color: Colors.white,
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                TextField(
                  onSubmitted: provider.updateMedicines,
                  decoration: InputDecoration(
                    hintText: "Medicines (comma separated)…",
                    prefixIcon: const Icon(Icons.medication, color: Color(0xFF0D4EA3)),
                    filled: true,
                    fillColor: const Color(0xFFF1F5F9),
                    border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(12), borderSide: BorderSide.none),
                  ),
                ),
                const SizedBox(height: 8),
                Row(
                  children: [
                    const Text("Sort:", style: TextStyle(fontSize: 12, color: Colors.grey)),
                    const SizedBox(width: 8),
                    DropdownButton<PharmacySort>(
                      value: provider.sort,
                      onChanged: (s) => s != null ? provider.setSort(s) : null,
                      items: const [
                        DropdownMenuItem(value: PharmacySort.bestMatch, child: Text("Best match")),
                        DropdownMenuItem(value: PharmacySort.distance, child: Text("Distance")),
                        DropdownMenuItem(value: PharmacySort.availability, child: Text("Availability")),
                        DropdownMenuItem(value: PharmacySort.rating, child: Text("Rating")),
                        DropdownMenuItem(value: PharmacySort.openNow, child: Text("Open now")),
                      ],
                    ),
                  ],
                ),
              ],
            ),
          ),
          if (provider.isLoading) const LinearProgressIndicator(),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
            child: Row(
              children: [
                Expanded(child: Text(provider.statusMessage, style: const TextStyle(fontSize: 12, color: Colors.grey))),
              ],
            ),
          ),
          Expanded(
            child: ListView.builder(
              padding: const EdgeInsets.all(16),
              itemCount: provider.results.length,
              itemBuilder: (context, i) => _card(provider.results[i]),
            ),
          ),
        ],
      ),
    );
  }

  Widget _card(EnhancedPharmacy e) {
    final p = e.pharmacy;
    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Expanded(child: Text(p.name, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15))),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                  decoration: BoxDecoration(color: Colors.blue.shade50, borderRadius: BorderRadius.circular(8)),
                  child: Text("${e.matchScore}% MATCH",
                      style: const TextStyle(color: Colors.blue, fontWeight: FontWeight.bold, fontSize: 10)),
                ),
              ],
            ),
            const SizedBox(height: 4),
            Text(p.address, style: const TextStyle(color: Colors.grey, fontSize: 12)),
            const SizedBox(height: 10),
            Wrap(
              spacing: 8,
              runSpacing: 6,
              crossAxisAlignment: WrapCrossAlignment.center,
              children: [
                _badge("${e.tier.label} · inferred", const Color(0xFF334155)),
                _iconText(Icons.star, Colors.orange, "${p.rating} (${p.reviewsCount})"),
                _iconText(Icons.place, Colors.blueGrey, "${e.distanceKm.toStringAsFixed(1)} km"),
                if (p.openNow != null)
                  _badge(p.openNow! ? "Open now" : "Closed",
                      p.openNow! ? const Color(0xFF15803D) : const Color(0xFF991B1B)),
              ],
            ),
            const SizedBox(height: 10),
            Row(
              children: [
                const Icon(Icons.inventory_2_outlined, size: 16, color: Color(0xFF0D4EA3)),
                const SizedBox(width: 6),
                Text("~${e.availabilityPercent}% stock likely",
                    style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w600, color: Color(0xFF0D4EA3))),
                const SizedBox(width: 6),
                const Text("est.", style: TextStyle(fontSize: 10, color: Colors.grey)),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _badge(String text, Color color) => Container(
        padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
        decoration: BoxDecoration(color: color, borderRadius: BorderRadius.circular(8)),
        child: Text(text, style: const TextStyle(color: Colors.white, fontSize: 10, fontWeight: FontWeight.w600)),
      );

  Widget _iconText(IconData icon, Color color, String text) => Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(icon, size: 14, color: color),
          const SizedBox(width: 2),
          Text(text, style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w600)),
        ],
      );
}
