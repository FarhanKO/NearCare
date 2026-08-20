import '../models/doctor.dart';

/// Deterministic symptom -> specialty routing.
///
/// This is a lightweight triage HEURISTIC, not a diagnosis. The UI must show a
/// clear "not medical advice" disclaimer. It maps free-text symptoms to one or
/// more specialties and always falls back to a General Physician.
class SymptomTriage {
  const SymptomTriage();

  /// Canonical specialty registry (single source of truth).
  static const List<Specialty> specialties = [
    Specialty(
      key: 'general',
      label: 'General Physician',
      searchKeyword: 'general physician clinic doctor',
      nameHints: ['general', 'family', 'physician', 'medicine'],
    ),
    Specialty(
      key: 'cardiology',
      label: 'Cardiologist',
      searchKeyword: 'cardiologist heart specialist clinic',
      nameHints: ['cardio', 'heart'],
    ),
    Specialty(
      key: 'neurology',
      label: 'Neurologist',
      searchKeyword: 'neurologist brain nerve specialist clinic',
      nameHints: ['neuro', 'brain', 'nerve'],
    ),
    Specialty(
      key: 'dermatology',
      label: 'Dermatologist',
      searchKeyword: 'dermatologist skin specialist clinic',
      nameHints: ['derma', 'skin'],
    ),
    Specialty(
      key: 'gastroenterology',
      label: 'Gastroenterologist',
      searchKeyword: 'gastroenterologist stomach digestive specialist clinic',
      nameHints: ['gastro', 'digest'],
    ),
    Specialty(
      key: 'orthopedics',
      label: 'Orthopedic Surgeon',
      searchKeyword: 'orthopedic bone joint specialist clinic',
      nameHints: ['ortho', 'bone', 'joint'],
    ),
    Specialty(
      key: 'ent',
      label: 'ENT Specialist',
      searchKeyword: 'ENT ear nose throat specialist clinic',
      nameHints: ['ent', 'ear', 'nose', 'throat'],
    ),
    Specialty(
      key: 'ophthalmology',
      label: 'Ophthalmologist',
      searchKeyword: 'ophthalmologist eye specialist clinic',
      nameHints: ['ophthal', 'eye', 'vision'],
    ),
    Specialty(
      key: 'pediatrics',
      label: 'Pediatrician',
      searchKeyword: 'pediatrician child specialist clinic',
      nameHints: ['pediat', 'child', 'kids'],
    ),
    Specialty(
      key: 'gynecology',
      label: 'Gynecologist',
      searchKeyword: 'gynecologist obstetrician women clinic',
      nameHints: ['gyne', 'obstet', 'women', 'maternity'],
    ),
    Specialty(
      key: 'urology',
      label: 'Urologist',
      searchKeyword: 'urologist kidney urinary specialist clinic',
      nameHints: ['uro', 'kidney'],
    ),
    Specialty(
      key: 'psychiatry',
      label: 'Psychiatrist',
      searchKeyword: 'psychiatrist mental health specialist clinic',
      nameHints: ['psychi', 'mental'],
    ),
    Specialty(
      key: 'endocrinology',
      label: 'Endocrinologist',
      searchKeyword: 'endocrinologist diabetes thyroid hormone specialist clinic',
      nameHints: ['endocrin', 'diabet', 'thyroid', 'hormone'],
    ),
    Specialty(
      key: 'pulmonology',
      label: 'Pulmonologist',
      searchKeyword: 'pulmonologist lung chest specialist clinic',
      nameHints: ['pulmo', 'lung', 'chest', 'respir'],
    ),
    Specialty(
      key: 'dentistry',
      label: 'Dentist',
      searchKeyword: 'dentist dental clinic',
      nameHints: ['dental', 'dentist', 'tooth'],
    ),
  ];

  static Specialty get general =>
      specialties.firstWhere((s) => s.key == 'general');

  static Specialty? byKey(String key) {
    for (final s in specialties) {
      if (s.key == key) return s;
    }
    return null;
  }

  /// Symptom keyword -> specialty key. Kept simple and readable on purpose;
  /// extend freely.
  static const Map<String, String> _symptomToSpecialty = {
    'chest pain': 'cardiology',
    'palpitation': 'cardiology',
    'heart': 'cardiology',
    'breathless': 'pulmonology',
    'shortness of breath': 'pulmonology',
    'cough': 'pulmonology',
    'wheeze': 'pulmonology',
    'headache': 'neurology',
    'migraine': 'neurology',
    'dizziness': 'neurology',
    'seizure': 'neurology',
    'numbness': 'neurology',
    'rash': 'dermatology',
    'acne': 'dermatology',
    'itch': 'dermatology',
    'skin': 'dermatology',
    'stomach': 'gastroenterology',
    'abdominal': 'gastroenterology',
    'nausea': 'gastroenterology',
    'diarrhea': 'gastroenterology',
    'vomit': 'gastroenterology',
    'acidity': 'gastroenterology',
    'bone': 'orthopedics',
    'joint': 'orthopedics',
    'fracture': 'orthopedics',
    'back pain': 'orthopedics',
    'knee': 'orthopedics',
    'ear': 'ent',
    'nose': 'ent',
    'throat': 'ent',
    'sinus': 'ent',
    'eye': 'ophthalmology',
    'vision': 'ophthalmology',
    'blurry': 'ophthalmology',
    'child': 'pediatrics',
    'infant': 'pediatrics',
    'baby': 'pediatrics',
    'pregnan': 'gynecology',
    'menstru': 'gynecology',
    'period': 'gynecology',
    'urine': 'urology',
    'urinary': 'urology',
    'kidney': 'urology',
    'anxiety': 'psychiatry',
    'depression': 'psychiatry',
    'stress': 'psychiatry',
    'mental': 'psychiatry',
    'diabetes': 'endocrinology',
    'sugar': 'endocrinology',
    'thyroid': 'endocrinology',
    'tooth': 'dentistry',
    'gum': 'dentistry',
    'dental': 'dentistry',
    'fever': 'general',
    'cold': 'general',
    'flu': 'general',
    'body ache': 'general',
    'weakness': 'general',
  };

  /// Returns matching specialties ordered by how many symptom cues hit them.
  /// Always non-empty (falls back to General Physician).
  List<Specialty> analyzeSymptoms(String text) {
    final t = text.toLowerCase();
    final tally = <String, int>{};
    _symptomToSpecialty.forEach((symptom, key) {
      if (t.contains(symptom)) {
        tally[key] = (tally[key] ?? 0) + 1;
      }
    });

    if (tally.isEmpty) return [general];

    final keys = tally.keys.toList()
      ..sort((a, b) => tally[b]!.compareTo(tally[a]!));
    return keys.map((k) => byKey(k)!).toList();
  }
}
