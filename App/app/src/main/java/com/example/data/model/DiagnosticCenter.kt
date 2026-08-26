package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diagnostic_centers")
data class DiagnosticCenter(
    @PrimaryKey val id: Int,
    val name: String,
    val address: String,
    val suburb: String,
    val latitude: Double,
    val longitude: Double,
    val rating: Float,
    val reviewsCount: Int,
    val crowdLevel: String, // "Low", "Moderate", "High"
    val crowdPercentage: Int, // 10% - 100%
    val estimatedWaitMinutes: Int,
    val phone: String,
    val timing: String,
    val certified: Boolean,
    val testsJson: String, // e.g. {"CBC": 25, "MRI Brain": 210, ...}
    val isFavorite: Boolean = false,
    // Which mode produced this row: "DIAGNOSTIC" | "PHARMACY" | "DOCTOR".
    // Results are scoped by this so pharmacy/doctor searches never mix with labs.
    val category: String = "DIAGNOSTIC",
    // Pharmacy only: inferred size tier ("Small", "Mid-sized", "Brand / chain",
    // "Hospital pharmacy"). Inferred from name/reviews/hospital proximity — never
    // real inventory data, so the UI labels it "inferred".
    val tierLabel: String = "",
    // Doctor only: the specialty this result was matched against.
    val specialtyLabel: String = "",
    // The place's own website, straight from Google Places. This is the only real
    // route to a centre's published price list — no API exposes test prices.
    val websiteUri: String = ""
) {
    // Helper to get tests as a map
    fun getTestPrices(): Map<String, Double> {
        val map = mutableMapOf<String, Double>()
        try {
            val json = org.json.JSONObject(testsJson)
            val keys = json.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                map[key] = json.optDouble(key)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return map
    }
}
