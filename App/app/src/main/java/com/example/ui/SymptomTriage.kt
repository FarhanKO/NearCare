package com.example.ui

/**
 * Lightweight symptom -> specialty routing, plus the Places search keyword and
 * name hints for each specialty.
 *
 * This is a triage HEURISTIC, not a diagnosis — the UI must show a
 * "guidance only" disclaimer. Unrecognised input always falls back to GENERAL.
 *
 * Note: Google Places cannot return individual named doctors, their specialties
 * or schedules. Doctor mode therefore locates CLINICS / SPECIALIST PRACTICES /
 * HOSPITALS matching a specialty — it never claims to list named physicians.
 */
object SymptomTriage {

    /** Places query used to find places for this specialty. */
    fun searchKeyword(specialty: DoctorSpecialty): String = when (specialty) {
        DoctorSpecialty.GENERAL -> "general physician clinic doctor"
        DoctorSpecialty.CARDIOLOGIST -> "cardiologist heart specialist clinic"
        DoctorSpecialty.DERMATOLOGIST -> "dermatologist skin specialist clinic"
        DoctorSpecialty.ORTHOPEDIC -> "orthopedic bone joint specialist clinic"
        DoctorSpecialty.ENT -> "ENT ear nose throat specialist clinic"
        DoctorSpecialty.GYNECOLOGIST -> "gynecologist obstetrician women clinic"
        DoctorSpecialty.PEDIATRICIAN -> "pediatrician child specialist clinic"
        DoctorSpecialty.NEUROLOGIST -> "neurologist brain nerve specialist clinic"
        DoctorSpecialty.PSYCHIATRIST -> "psychiatrist mental health clinic"
        DoctorSpecialty.OPHTHALMOLOGIST -> "ophthalmologist eye specialist clinic"
        DoctorSpecialty.DENTIST -> "dentist dental clinic"
        DoctorSpecialty.UROLOGIST -> "urologist kidney urinary specialist clinic"
    }

    /** Words in a place name that signal this specialty (used to boost ranking). */
    fun nameHints(specialty: DoctorSpecialty): List<String> = when (specialty) {
        DoctorSpecialty.GENERAL -> listOf("general", "family", "physician", "medicine")
        DoctorSpecialty.CARDIOLOGIST -> listOf("cardio", "heart")
        DoctorSpecialty.DERMATOLOGIST -> listOf("derma", "skin")
        DoctorSpecialty.ORTHOPEDIC -> listOf("ortho", "bone", "joint")
        DoctorSpecialty.ENT -> listOf("ent", "ear", "nose", "throat")
        DoctorSpecialty.GYNECOLOGIST -> listOf("gyne", "obstet", "women", "maternity")
        DoctorSpecialty.PEDIATRICIAN -> listOf("pediat", "child", "kids", "shishu")
        DoctorSpecialty.NEUROLOGIST -> listOf("neuro", "brain", "nerve")
        DoctorSpecialty.PSYCHIATRIST -> listOf("psychi", "mental")
        DoctorSpecialty.OPHTHALMOLOGIST -> listOf("ophthal", "eye", "vision")
        DoctorSpecialty.DENTIST -> listOf("dental", "dentist", "tooth")
        DoctorSpecialty.UROLOGIST -> listOf("uro", "kidney")
    }

    /** Symptom keyword -> specialty. Extend freely. */
    private val symptomMap: Map<String, DoctorSpecialty> = mapOf(
        "chest pain" to DoctorSpecialty.CARDIOLOGIST,
        "palpitation" to DoctorSpecialty.CARDIOLOGIST,
        "heart" to DoctorSpecialty.CARDIOLOGIST,
        "blood pressure" to DoctorSpecialty.CARDIOLOGIST,

        "headache" to DoctorSpecialty.NEUROLOGIST,
        "migraine" to DoctorSpecialty.NEUROLOGIST,
        "dizziness" to DoctorSpecialty.NEUROLOGIST,
        "seizure" to DoctorSpecialty.NEUROLOGIST,
        "numbness" to DoctorSpecialty.NEUROLOGIST,
        "stroke" to DoctorSpecialty.NEUROLOGIST,

        "rash" to DoctorSpecialty.DERMATOLOGIST,
        "acne" to DoctorSpecialty.DERMATOLOGIST,
        "itch" to DoctorSpecialty.DERMATOLOGIST,
        "skin" to DoctorSpecialty.DERMATOLOGIST,
        "eczema" to DoctorSpecialty.DERMATOLOGIST,

        "bone" to DoctorSpecialty.ORTHOPEDIC,
        "joint" to DoctorSpecialty.ORTHOPEDIC,
        "fracture" to DoctorSpecialty.ORTHOPEDIC,
        "back pain" to DoctorSpecialty.ORTHOPEDIC,
        "knee" to DoctorSpecialty.ORTHOPEDIC,
        "shoulder" to DoctorSpecialty.ORTHOPEDIC,

        "ear" to DoctorSpecialty.ENT,
        "nose" to DoctorSpecialty.ENT,
        "throat" to DoctorSpecialty.ENT,
        "sinus" to DoctorSpecialty.ENT,
        "tonsil" to DoctorSpecialty.ENT,

        "eye" to DoctorSpecialty.OPHTHALMOLOGIST,
        "vision" to DoctorSpecialty.OPHTHALMOLOGIST,
        "blurry" to DoctorSpecialty.OPHTHALMOLOGIST,

        "pregnan" to DoctorSpecialty.GYNECOLOGIST,
        "menstru" to DoctorSpecialty.GYNECOLOGIST,
        "period pain" to DoctorSpecialty.GYNECOLOGIST,

        "child" to DoctorSpecialty.PEDIATRICIAN,
        "infant" to DoctorSpecialty.PEDIATRICIAN,
        "baby" to DoctorSpecialty.PEDIATRICIAN,

        "urine" to DoctorSpecialty.UROLOGIST,
        "urinary" to DoctorSpecialty.UROLOGIST,
        "kidney" to DoctorSpecialty.UROLOGIST,

        "anxiety" to DoctorSpecialty.PSYCHIATRIST,
        "depress" to DoctorSpecialty.PSYCHIATRIST,
        "stress" to DoctorSpecialty.PSYCHIATRIST,
        "insomnia" to DoctorSpecialty.PSYCHIATRIST,

        "tooth" to DoctorSpecialty.DENTIST,
        "teeth" to DoctorSpecialty.DENTIST,
        "gum" to DoctorSpecialty.DENTIST,
        "dental" to DoctorSpecialty.DENTIST,

        "fever" to DoctorSpecialty.GENERAL,
        "cold" to DoctorSpecialty.GENERAL,
        "flu" to DoctorSpecialty.GENERAL,
        "cough" to DoctorSpecialty.GENERAL,
        "body ache" to DoctorSpecialty.GENERAL,
        "weakness" to DoctorSpecialty.GENERAL,
        "vomit" to DoctorSpecialty.GENERAL,
        "diarrhea" to DoctorSpecialty.GENERAL,
        "stomach" to DoctorSpecialty.GENERAL
    )

    /**
     * Returns specialties matching the free-text symptoms, most-cued first.
     * Empty list when nothing is recognised (caller decides the fallback).
     */
    fun analyze(text: String): List<DoctorSpecialty> {
        if (text.isBlank()) return emptyList()
        val t = text.lowercase()
        val tally = mutableMapOf<DoctorSpecialty, Int>()
        symptomMap.forEach { (symptom, specialty) ->
            if (t.contains(symptom)) tally[specialty] = (tally[specialty] ?: 0) + 1
        }
        return tally.entries.sortedByDescending { it.value }.map { it.key }
    }
}
