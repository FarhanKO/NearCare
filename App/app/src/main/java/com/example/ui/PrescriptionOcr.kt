package com.example.ui

import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Real prescription OCR.
 *
 * Runs Google ML Kit on-device text recognition over the captured photos and
 * matches the recognised words against known diagnostic tests and medicines.
 *
 * IMPORTANT: nothing here is hardcoded. If the photo contains no readable text,
 * or no known item, this returns an EMPTY result — the UI must say so rather
 * than inventing findings.
 */
object PrescriptionOcr {

    private const val TAG = "PrescriptionOcr"

    data class Result(
        val tests: List<String>,
        val medicines: List<String>,
        val rawText: String
    ) {
        val isEmpty: Boolean get() = tests.isEmpty() && medicines.isEmpty()
        val hasText: Boolean get() = rawText.isNotBlank()
        val all: List<String> get() = tests + medicines
    }

    /**
     * Canonical diagnostic test -> spellings/abbreviations that may appear on a
     * prescription. Matching is lowercase substring over the recognised text.
     */
    private val testAliases: Map<String, List<String>> = mapOf(
        "CBC" to listOf("cbc", "complete blood count", "full blood count", "fbc"),
        "Glucose" to listOf("glucose", "fasting blood sugar", "fbs", "rbs", "blood sugar", "ogtt"),
        "HbA1c" to listOf("hba1c", "hb a1c", "a1c", "glycated"),
        "Lipid" to listOf("lipid", "lipid profile", "cholesterol", "triglyceride"),
        "Liver Function" to listOf("lft", "liver function", "sgpt", "sgot", "alt", "ast", "bilirubin"),
        "Kidney Function" to listOf("kft", "rft", "kidney function", "creatinine", "urea", "egfr"),
        "TSH" to listOf("tsh", "thyroid", "ft3", "ft4", "t3", "t4"),
        "Vitamin D" to listOf("vitamin d", "vit d", "25-oh", "25 oh"),
        "Urine R/E" to listOf("urine r/e", "urine re", "urine routine", "urine c/s", "urine test"),
        "Electrolytes" to listOf("electrolyte", "serum electrolyte", "sodium", "potassium", "s. electrolyte"),
        "CRP" to listOf("crp", "c-reactive", "c reactive"),
        "ESR" to listOf("esr", "sedimentation"),
        "X-Ray Chest" to listOf("x-ray", "x ray", "xray", "chest x", "cxr"),
        "MRI Brain" to listOf("mri", "magnetic resonance"),
        "CT Scan" to listOf("ct scan", "ct-scan", "cect", "hrct", "cat scan"),
        "Ultrasound" to listOf("ultrasound", "usg", "sonography", "ultrasonogram"),
        "ECG Heart" to listOf("ecg", "ekg", "electrocardiogram"),
        "Echocardiogram" to listOf("echo", "echocardiogram"),
        "Biopsy" to listOf("biopsy", "fnac", "histopath")
    )

    /**
     * Medicines are resolved through [MedicineCatalog] so the pharmacy rarity
     * model and the scanner always agree on naming.
     */
    private val medicineAliases: List<String> = listOf(
        "napa", "paracetamol", "ace", "panadol", "ibuprofen", "brufen",
        "cetirizine", "alatrol", "fexofenadine", "histacin",
        "omeprazole", "seclo", "losectil", "esomeprazole", "sergel", "maxpro",
        "pantoprazole", "pantonix", "amoxicillin", "moxacil", "azithromycin",
        "zimax", "metronidazole", "flagyl", "amodis", "ciprofloxacin",
        "levofloxacin", "cefixime", "ceftriaxone", "salbutamol", "ventolin",
        "sultolin", "montelukast", "monas", "metformin", "comet", "glucophage",
        "gliclazide", "sitagliptin", "losartan", "angilock", "amlodipine",
        "amdocal", "bisoprolol", "atenolol", "atorvastatin", "atova",
        "rosuvastatin", "clopidogrel", "levothyroxine", "thyrox", "insulin",
        "humulin", "novorapid", "prednisolone", "sertraline", "fluoxetine",
        "escitalopram", "warfarin", "rivaroxaban", "xarelto", "enoxaparin",
        "clexane", "tacrolimus", "methotrexate", "levetiracetam", "keppra",
        "lenvatinib", "lenva", "lenvima", "imatinib", "veenat", "gefitinib",
        "erlotinib", "osimertinib", "trastuzumab", "herceptin", "bevacizumab",
        "capecitabine", "xeloda", "cisplatin", "carboplatin", "paclitaxel",
        "docetaxel", "doxorubicin", "cyclophosphamide"
    )

    /** Runs OCR over every captured page and merges the findings. */
    suspend fun extract(bitmaps: List<Bitmap>): Result {
        if (bitmaps.isEmpty()) return Result(emptyList(), emptyList(), "")

        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val combined = StringBuilder()

        try {
            for (bitmap in bitmaps) {
                val text = recognizeOne(recognizer, bitmap)
                if (text.isNotBlank()) combined.append(text).append('\n')
            }
        } finally {
            try { recognizer.close() } catch (e: Exception) { Log.e(TAG, "close failed", e) }
        }

        return parse(combined.toString())
    }

    private suspend fun recognizeOne(
        recognizer: com.google.mlkit.vision.text.TextRecognizer,
        bitmap: Bitmap
    ): String = suspendCancellableCoroutine { cont ->
        try {
            val image = InputImage.fromBitmap(bitmap, 0)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    if (cont.isActive) cont.resume(visionText.text)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "OCR failed for a page", e)
                    if (cont.isActive) cont.resume("")
                }
        } catch (e: Exception) {
            Log.e(TAG, "OCR threw", e)
            if (cont.isActive) cont.resume("")
        }
    }

    /** Exposed for testing: turn raw OCR text into recognised tests + medicines. */
    fun parse(rawText: String): Result {
        val hay = rawText.lowercase()

        val tests = testAliases
            .filter { (_, aliases) -> aliases.any { hay.contains(it) } }
            .keys
            .toList()

        val medicines = medicineAliases
            .filter { hay.contains(it) }
            // Prefer the longest match so "lenvatinib" doesn't also report "lenva".
            .sortedByDescending { it.length }
            .fold(mutableListOf<String>()) { acc, name ->
                if (acc.none { it.contains(name, ignoreCase = true) }) {
                    acc.add(name.replaceFirstChar { c -> c.uppercase() })
                }
                acc
            }

        return Result(tests, medicines, rawText)
    }
}
