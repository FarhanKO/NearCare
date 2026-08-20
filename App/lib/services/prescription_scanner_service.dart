import 'package:image_picker/image_picker.dart';
import 'package:google_mlkit_text_recognition/google_mlkit_text_recognition.dart';

/// Result of scanning a prescription: recognized diagnostic tests and medicines
/// plus the raw OCR text (kept so the UI can show what was actually read).
class PrescriptionScanResult {
  final List<String> tests;
  final List<String> medicines;
  final String rawText;

  const PrescriptionScanResult({
    required this.tests,
    required this.medicines,
    required this.rawText,
  });

  bool get isEmpty => tests.isEmpty && medicines.isEmpty;
  bool get hasText => rawText.trim().isNotEmpty;
}

/// Real prescription scanner: capture an image (camera or gallery), run
/// on-device OCR (Google ML Kit), then match recognized text against known
/// test/medicine dictionaries.
///
/// This is genuine recognition, not a mock. Accuracy depends on image quality
/// and how legible the prescription is; the dictionaries below are the source
/// of truth for what can be recognized and are meant to be extended.
class PrescriptionScannerService {
  final ImagePicker _picker = ImagePicker();
  final TextRecognizer _recognizer =
      TextRecognizer(script: TextRecognitionScript.latin);

  /// Diagnostic tests the parser can recognize. Each entry: the canonical
  /// label plus lowercase aliases/spellings that may appear on a prescription.
  static const Map<String, List<String>> _testAliases = {
    'CBC': ['cbc', 'complete blood count', 'full blood count', 'fbc'],
    'Glucose': ['glucose', 'fasting blood sugar', 'fbs', 'rbs', 'blood sugar'],
    'HbA1c': ['hba1c', 'a1c', 'glycated'],
    'Lipid': ['lipid', 'lipid profile', 'cholesterol'],
    'Liver Function': ['lft', 'liver function', 'sgpt', 'sgot'],
    'Kidney Function': ['kft', 'kidney function', 'creatinine', 'urea'],
    'TSH': ['tsh', 'thyroid'],
    'Vitamin D': ['vitamin d', 'vit d', '25-oh'],
    'Urine R/E': ['urine r/e', 'urine routine', 'urine test'],
    'X-Ray Chest': ['x-ray', 'x ray', 'xray', 'chest x'],
    'MRI Brain': ['mri', 'magnetic resonance'],
    'CT Scan': ['ct scan', 'ct ', 'cat scan'],
    'Ultrasound': ['ultrasound', 'usg', 'sonography'],
    'ECG Heart': ['ecg', 'ekg', 'electrocardiogram'],
  };

  /// Medicines the parser can recognize (generic + common brand names).
  static const Map<String, List<String>> _medicineAliases = {
    'Paracetamol': ['paracetamol', 'acetaminophen', 'napa', 'ace', 'panadol'],
    'Amoxicillin': ['amoxicillin', 'amoxil', 'moxacil'],
    'Azithromycin': ['azithromycin', 'azithro', 'zimax'],
    'Omeprazole': ['omeprazole', 'omep', 'losectil', 'seclo'],
    'Pantoprazole': ['pantoprazole', 'pantonix', 'pantoprol'],
    'Metformin': ['metformin', 'comet', 'glucophage'],
    'Losartan': ['losartan', 'losia', 'angilock'],
    'Atorvastatin': ['atorvastatin', 'atova', 'lipitor'],
    'Cetirizine': ['cetirizine', 'alatrol', 'zyrtec'],
    'Ibuprofen': ['ibuprofen', 'brufen', 'advil'],
    'Esomeprazole': ['esomeprazole', 'esonix', 'nexium'],
    'Ranitidine': ['ranitidine', 'ranison', 'neotack'],
    'Salbutamol': ['salbutamol', 'ventolin', 'sultolin'],
    'Insulin': ['insulin', 'humulin', 'novorapid'],
  };

  /// Opens the camera (or gallery) and returns the captured image, or null if
  /// the user cancels.
  Future<XFile?> capture({bool fromCamera = true}) {
    return _picker.pickImage(
      source: fromCamera ? ImageSource.camera : ImageSource.gallery,
      imageQuality: 90,
    );
  }

  /// Runs OCR on the image file and parses recognized items.
  Future<PrescriptionScanResult> scanFile(XFile file) async {
    final input = InputImage.fromFilePath(file.path);
    final RecognizedText recognized = await _recognizer.processImage(input);
    return parseText(recognized.text);
  }

  /// Exposed for testing: parse raw OCR text into tests + medicines.
  PrescriptionScanResult parseText(String rawText) {
    final hay = rawText.toLowerCase();

    final tests = <String>[];
    _testAliases.forEach((label, aliases) {
      if (aliases.any(hay.contains)) tests.add(label);
    });

    final medicines = <String>[];
    _medicineAliases.forEach((label, aliases) {
      if (aliases.any(hay.contains)) medicines.add(label);
    });

    return PrescriptionScanResult(
      tests: tests.toSet().toList(),
      medicines: medicines.toSet().toList(),
      rawText: rawText,
    );
  }

  void dispose() {
    _recognizer.close();
  }
}
