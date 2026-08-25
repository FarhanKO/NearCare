package com.example.data.api

import com.squareup.moshi.JsonClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GeminiManager {
    suspend fun askAdvisor(prompt: String, contextData: String = ""): String = withContext(Dispatchers.IO) {
        val normalizedText = "$prompt\n$contextData".lowercase()
        val requestedTests = listOf("cbc", "lipid", "mri brain", "x-ray chest", "glucose", "ct scan abdomen")
            .filter { normalizedText.contains(it) }

        buildList {
            add("Local advisor mode is active, so this response is generated from the app's bundled data.")
            if (requestedTests.isNotEmpty()) {
                add("Matched tests: ${requestedTests.joinToString(", ") { it.uppercase() }}.")
            }
            add("Prefer centers with higher ratings, shorter waits, and lower crowd levels.")
            if (normalizedText.contains("fast") || normalizedText.contains("blood")) {
                add("For blood work such as CBC, Lipid Profile, or Glucose, plan for 8-12 hours of fasting unless your doctor says otherwise.")
            }
            if (normalizedText.contains("mri") || normalizedText.contains("ct") || normalizedText.contains("x-ray")) {
                add("For imaging tests, compare machine availability, wait time, and whether the center is certified.")
            }
            if (contextData.isNotBlank()) {
                add("Use the nearby center list in the app to compare distance, price, and crowd status before booking.")
            }
            add("This is logistics guidance only, not a medical diagnosis. Please confirm test preparation with your doctor.")
        }.joinToString("\n- ", prefix = "- ")
    }
}
