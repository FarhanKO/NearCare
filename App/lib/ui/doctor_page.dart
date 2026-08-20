import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../models/doctor.dart';
import '../providers/doctor_provider.dart';

class DoctorPage extends StatefulWidget {
  const DoctorPage({Key? key}) : super(key: key);

  @override
  State<DoctorPage> createState() => _DoctorPageState();
}

class _DoctorPageState extends State<DoctorPage> {
  bool _bySymptoms = true;
  final _symptomController = TextEditingController();
  Specialty? _selectedSpecialty;

  @override
  void dispose() {
    _symptomController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final provider = Provider.of<DoctorProvider>(context);

    return Scaffold(
      backgroundColor: const Color(0xFFF1F5F9),
      appBar: AppBar(
        backgroundColor: const Color(0xFF0D4EA3),
        title: const Text("Find a Doctor"),
      ),
      body: Column(
        children: [
          Container(
            color: Colors.white,
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                // Mode toggle
                Row(
                  children: [
                    _modeChip("By symptoms", _bySymptoms, () => setState(() => _bySymptoms = true)),
                    const SizedBox(width: 8),
                    _modeChip("By specialty", !_bySymptoms, () => setState(() => _bySymptoms = false)),
                  ],
                ),
                const SizedBox(height: 12),
                if (_bySymptoms)
                  TextField(
                    controller: _symptomController,
                    onSubmitted: (v) => provider.searchBySymptoms(v),
                    decoration: InputDecoration(
                      hintText: "Describe symptoms (e.g. chest pain, cough)…",
                      prefixIcon: const Icon(Icons.healing, color: Color(0xFF0D4EA3)),
                      filled: true,
                      fillColor: const Color(0xFFF1F5F9),
                      border: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(12), borderSide: BorderSide.none),
                    ),
                  )
                else
                  DropdownButtonFormField<Specialty>(
                    value: _selectedSpecialty,
                    isExpanded: true,
                    hint: const Text("Choose a specialty"),
                    decoration: InputDecoration(
                      filled: true,
                      fillColor: const Color(0xFFF1F5F9),
                      border: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(12), borderSide: BorderSide.none),
                    ),
                    items: provider.allSpecialties
                        .map((s) => DropdownMenuItem(value: s, child: Text(s.label)))
                        .toList(),
                    onChanged: (s) {
                      setState(() => _selectedSpecialty = s);
                      if (s != null) provider.searchBySpecialty(s);
                    },
                  ),
                const SizedBox(height: 8),
                // Disclaimer — this is triage, not diagnosis.
                Container(
                  padding: const EdgeInsets.all(8),
                  decoration: BoxDecoration(
                    color: const Color(0xFFFFF7ED),
                    borderRadius: BorderRadius.circular(8),
                    border: Border.all(color: const Color(0xFFFED7AA)),
                  ),
                  child: const Row(
                    children: [
                      Icon(Icons.info_outline, size: 16, color: Color(0xFF9A3412)),
                      SizedBox(width: 6),
                      Expanded(
                        child: Text(
                          "Guidance only — not a medical diagnosis. For emergencies call your local emergency number.",
                          style: TextStyle(fontSize: 11, color: Color(0xFF9A3412)),
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
          if (provider.isLoading) const LinearProgressIndicator(),
          if (provider.matchedSpecialties.isNotEmpty)
            Padding(
              padding: const EdgeInsets.fromLTRB(16, 8, 16, 0),
              child: Wrap(
                spacing: 6,
                runSpacing: 6,
                children: [
                  const Text("Suggested: ", style: TextStyle(fontSize: 12, color: Colors.grey)),
                  ...provider.matchedSpecialties.map((s) => Chip(
                        label: Text(s.label, style: const TextStyle(fontSize: 11)),
                        materialTapTargetSize: MaterialTapTargetSize.shrinkWrap,
                      )),
                ],
              ),
            ),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
            child: Text(provider.statusMessage, style: const TextStyle(fontSize: 12, color: Colors.grey)),
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

  Widget _modeChip(String label, bool selected, VoidCallback onTap) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
        decoration: BoxDecoration(
          color: selected ? const Color(0xFF0D4EA3) : const Color(0xFFF1F5F9),
          borderRadius: BorderRadius.circular(20),
        ),
        child: Text(label,
            style: TextStyle(
                color: selected ? Colors.white : const Color(0xFF475569),
                fontWeight: FontWeight.bold,
                fontSize: 12)),
      ),
    );
  }

  Widget _card(EnhancedDoctorPlace e) {
    final p = e.place;
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
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                  decoration: BoxDecoration(color: const Color(0xFF334155), borderRadius: BorderRadius.circular(8)),
                  child: Text(p.specialty.label,
                      style: const TextStyle(color: Colors.white, fontSize: 10, fontWeight: FontWeight.w600)),
                ),
                Row(mainAxisSize: MainAxisSize.min, children: [
                  const Icon(Icons.star, size: 14, color: Colors.orange),
                  const SizedBox(width: 2),
                  Text("${p.rating} (${p.reviewsCount})", style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w600)),
                ]),
                Row(mainAxisSize: MainAxisSize.min, children: [
                  const Icon(Icons.place, size: 14, color: Colors.blueGrey),
                  const SizedBox(width: 2),
                  Text("${e.distanceKm.toStringAsFixed(1)} km", style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w600)),
                ]),
                if (p.openNow != null)
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                    decoration: BoxDecoration(
                        color: p.openNow! ? const Color(0xFF15803D) : const Color(0xFF991B1B),
                        borderRadius: BorderRadius.circular(8)),
                    child: Text(p.openNow! ? "Open now" : "Closed",
                        style: const TextStyle(color: Colors.white, fontSize: 10, fontWeight: FontWeight.w600)),
                  ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
