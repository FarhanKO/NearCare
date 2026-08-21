import 'dart:io';
import 'package:flutter/material.dart';
import '../../services/prescription_scanner_service.dart';

/// Real prescription scanner UI. Opens the camera/gallery, runs on-device OCR,
/// shows exactly what was recognized, and returns the confirmed tests/medicines.
class PrescriptionScannerSheet extends StatefulWidget {
  final void Function(List<String> tests, List<String> medicines) onDone;

  const PrescriptionScannerSheet({Key? key, required this.onDone}) : super(key: key);

  @override
  State<PrescriptionScannerSheet> createState() => _PrescriptionScannerSheetState();
}

class _PrescriptionScannerSheetState extends State<PrescriptionScannerSheet> {
  final _scanner = PrescriptionScannerService();

  String? _imagePath;
  bool _busy = false;
  String? _error;
  PrescriptionScanResult? _result;

  @override
  void dispose() {
    _scanner.dispose();
    super.dispose();
  }

  Future<void> _run({required bool fromCamera}) async {
    setState(() {
      _busy = true;
      _error = null;
    });
    try {
      final file = await _scanner.capture(fromCamera: fromCamera);
      if (file == null) {
        setState(() => _busy = false); // user cancelled
        return;
      }
      final result = await _scanner.scanFile(file);
      setState(() {
        _imagePath = file.path;
        _result = result;
        _busy = false;
        if (result.isEmpty) {
          _error = result.hasText
              ? "Couldn't match any known tests or medicines. Try a clearer photo."
              : "No text detected. Try a clearer, well-lit photo.";
        }
      });
    } catch (e) {
      setState(() {
        _busy = false;
        _error = "Scan failed: $e";
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final result = _result;
    return Dialog(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
      backgroundColor: const Color(0xFF0F172A),
      child: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Text("Prescription Scanner",
                    style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 16)),
                IconButton(
                  icon: const Icon(Icons.close, color: Colors.white),
                  onPressed: () => Navigator.pop(context),
                ),
              ],
            ),
            const Text("Capture a prescription — tests & medicines are read on-device.",
                style: TextStyle(color: Colors.grey, fontSize: 11)),
            const SizedBox(height: 16),

            // Preview / placeholder
            Container(
              height: 180,
              width: double.infinity,
              decoration: BoxDecoration(
                color: const Color(0xFF1E293B),
                borderRadius: BorderRadius.circular(16),
                border: Border.all(color: const Color(0xFF334155)),
              ),
              clipBehavior: Clip.antiAlias,
              child: _busy
                  ? const Center(child: CircularProgressIndicator())
                  : (_imagePath != null
                      ? Image.file(File(_imagePath!), fit: BoxFit.cover)
                      : const Center(
                          child: Icon(Icons.document_scanner, color: Colors.white24, size: 56))),
            ),
            const SizedBox(height: 12),

            Row(
              children: [
                Expanded(
                  child: OutlinedButton.icon(
                    onPressed: _busy ? null : () => _run(fromCamera: true),
                    icon: const Icon(Icons.photo_camera, color: Colors.white),
                    label: const Text("Camera", style: TextStyle(color: Colors.white)),
                    style: OutlinedButton.styleFrom(side: const BorderSide(color: Color(0xFF38BDF8))),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: OutlinedButton.icon(
                    onPressed: _busy ? null : () => _run(fromCamera: false),
                    icon: const Icon(Icons.photo_library, color: Colors.white),
                    label: const Text("Gallery", style: TextStyle(color: Colors.white)),
                    style: OutlinedButton.styleFrom(side: const BorderSide(color: Color(0xFF38BDF8))),
                  ),
                ),
              ],
            ),

            if (_error != null) ...[
              const SizedBox(height: 12),
              Text(_error!, style: const TextStyle(color: Color(0xFFF87171), fontSize: 12)),
            ],

            if (result != null && !result.isEmpty) ...[
              const SizedBox(height: 16),
              if (result.tests.isNotEmpty) ...[
                const Text("Tests", style: TextStyle(color: Colors.white70, fontSize: 11, fontWeight: FontWeight.bold)),
                const SizedBox(height: 6),
                _chips(result.tests, const Color(0xFF0D4EA3)),
              ],
              if (result.medicines.isNotEmpty) ...[
                const SizedBox(height: 12),
                const Text("Medicines", style: TextStyle(color: Colors.white70, fontSize: 11, fontWeight: FontWeight.bold)),
                const SizedBox(height: 6),
                _chips(result.medicines, const Color(0xFF15803D)),
              ],
              const SizedBox(height: 16),
              SizedBox(
                width: double.infinity,
                child: ElevatedButton(
                  onPressed: () {
                    widget.onDone(result.tests, result.medicines);
                    Navigator.pop(context);
                  },
                  style: ElevatedButton.styleFrom(
                    backgroundColor: const Color(0xFF22C55E),
                    minimumSize: const Size(0, 48),
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                  ),
                  child: const Text("Use these", style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }

  Widget _chips(List<String> items, Color color) {
    return Wrap(
      spacing: 6,
      runSpacing: 6,
      children: items
          .map((t) => Chip(
                label: Text(t, style: const TextStyle(color: Colors.white, fontSize: 12)),
                backgroundColor: color,
                materialTapTargetSize: MaterialTapTargetSize.shrinkWrap,
              ))
          .toList(),
    );
  }
}
