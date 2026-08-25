package com.example.ui

/**
 * How hard a medicine is to find in an ordinary retail pharmacy.
 *
 * This is the missing half of the pharmacy prediction: a paracetamol brand like
 * Napa is stocked literally everywhere, while an oncology drug like Lenva
 * (lenvatinib) is only realistically held by hospital or large chain pharmacies.
 * Availability therefore depends on BOTH the medicine and the pharmacy tier.
 */
enum class MedicineRarity(val label: String, val note: String) {
    COMMON("Common", "Stocked by almost every pharmacy"),
    MODERATE("Moderate", "Usually stocked by mid-sized pharmacies"),
    RARE("Rare", "Mostly larger or chain pharmacies"),
    SPECIALIST("Specialist", "Typically hospital or oncology pharmacies only")
}

object MedicineCatalog {

    /**
     * Known medicines (generic names + widely used South-Asian brand names)
     * mapped to how commonly they are stocked. Matching is substring-based and
     * case-insensitive, so "napa extend" still resolves to COMMON.
     *
     * Extend this list freely — anything unknown falls back to MODERATE and is
     * reported as an assumption rather than a fact.
     */
    private val catalog: List<Pair<String, MedicineRarity>> = listOf(
        // ---- COMMON: OTC / ubiquitous ----
        "napa" to MedicineRarity.COMMON,
        "paracetamol" to MedicineRarity.COMMON,
        "acetaminophen" to MedicineRarity.COMMON,
        "panadol" to MedicineRarity.COMMON,
        "ace" to MedicineRarity.COMMON,
        "ibuprofen" to MedicineRarity.COMMON,
        "brufen" to MedicineRarity.COMMON,
        "flexi" to MedicineRarity.COMMON,
        "cetirizine" to MedicineRarity.COMMON,
        "alatrol" to MedicineRarity.COMMON,
        "histacin" to MedicineRarity.COMMON,
        "fexofenadine" to MedicineRarity.COMMON,
        "fexo" to MedicineRarity.COMMON,
        "omeprazole" to MedicineRarity.COMMON,
        "losectil" to MedicineRarity.COMMON,
        "seclo" to MedicineRarity.COMMON,
        "esomeprazole" to MedicineRarity.COMMON,
        "sergel" to MedicineRarity.COMMON,
        "maxpro" to MedicineRarity.COMMON,
        "pantoprazole" to MedicineRarity.COMMON,
        "pantonix" to MedicineRarity.COMMON,
        "antacid" to MedicineRarity.COMMON,
        "amoxicillin" to MedicineRarity.COMMON,
        "moxacil" to MedicineRarity.COMMON,
        "azithromycin" to MedicineRarity.COMMON,
        "zimax" to MedicineRarity.COMMON,
        "metronidazole" to MedicineRarity.COMMON,
        "flagyl" to MedicineRarity.COMMON,
        "amodis" to MedicineRarity.COMMON,
        "salbutamol" to MedicineRarity.COMMON,
        "ventolin" to MedicineRarity.COMMON,
        "sultolin" to MedicineRarity.COMMON,
        "ors" to MedicineRarity.COMMON,
        "saline" to MedicineRarity.COMMON,
        "vitamin" to MedicineRarity.COMMON,
        "calcium" to MedicineRarity.COMMON,
        "zinc" to MedicineRarity.COMMON,
        "folic" to MedicineRarity.COMMON,
        "iron" to MedicineRarity.COMMON,

        // ---- MODERATE: routine prescription / chronic ----
        "metformin" to MedicineRarity.MODERATE,
        "comet" to MedicineRarity.MODERATE,
        "glucophage" to MedicineRarity.MODERATE,
        "gliclazide" to MedicineRarity.MODERATE,
        "sitagliptin" to MedicineRarity.MODERATE,
        "losartan" to MedicineRarity.MODERATE,
        "angilock" to MedicineRarity.MODERATE,
        "amlodipine" to MedicineRarity.MODERATE,
        "amdocal" to MedicineRarity.MODERATE,
        "bisoprolol" to MedicineRarity.MODERATE,
        "atenolol" to MedicineRarity.MODERATE,
        "atorvastatin" to MedicineRarity.MODERATE,
        "atova" to MedicineRarity.MODERATE,
        "rosuvastatin" to MedicineRarity.MODERATE,
        "clopidogrel" to MedicineRarity.MODERATE,
        "levothyroxine" to MedicineRarity.MODERATE,
        "thyrox" to MedicineRarity.MODERATE,
        "montelukast" to MedicineRarity.MODERATE,
        "monas" to MedicineRarity.MODERATE,
        "ciprofloxacin" to MedicineRarity.MODERATE,
        "levofloxacin" to MedicineRarity.MODERATE,
        "cefixime" to MedicineRarity.MODERATE,
        "ceftriaxone" to MedicineRarity.MODERATE,
        "prednisolone" to MedicineRarity.MODERATE,
        "insulin" to MedicineRarity.MODERATE,
        "humulin" to MedicineRarity.MODERATE,
        "novorapid" to MedicineRarity.MODERATE,
        "sertraline" to MedicineRarity.MODERATE,
        "fluoxetine" to MedicineRarity.MODERATE,
        "escitalopram" to MedicineRarity.MODERATE,
        "hydroxychloroquine" to MedicineRarity.MODERATE,

        // ---- RARE: limited distribution / specialist chronic ----
        "warfarin" to MedicineRarity.RARE,
        "rivaroxaban" to MedicineRarity.RARE,
        "xarelto" to MedicineRarity.RARE,
        "dabigatran" to MedicineRarity.RARE,
        "enoxaparin" to MedicineRarity.RARE,
        "clexane" to MedicineRarity.RARE,
        "tacrolimus" to MedicineRarity.RARE,
        "cyclosporine" to MedicineRarity.RARE,
        "mycophenolate" to MedicineRarity.RARE,
        "azathioprine" to MedicineRarity.RARE,
        "methotrexate" to MedicineRarity.RARE,
        "levetiracetam" to MedicineRarity.RARE,
        "keppra" to MedicineRarity.RARE,
        "lamotrigine" to MedicineRarity.RARE,
        "clozapine" to MedicineRarity.RARE,
        "lithium" to MedicineRarity.RARE,
        "erythropoietin" to MedicineRarity.RARE,
        "sofosbuvir" to MedicineRarity.RARE,

        // ---- SPECIALIST: oncology / biologics — hospital pharmacies ----
        "lenvatinib" to MedicineRarity.SPECIALIST,
        "lenva" to MedicineRarity.SPECIALIST,
        "lenvima" to MedicineRarity.SPECIALIST,
        "sorafenib" to MedicineRarity.SPECIALIST,
        "nexavar" to MedicineRarity.SPECIALIST,
        "imatinib" to MedicineRarity.SPECIALIST,
        "gleevec" to MedicineRarity.SPECIALIST,
        "veenat" to MedicineRarity.SPECIALIST,
        "gefitinib" to MedicineRarity.SPECIALIST,
        "erlotinib" to MedicineRarity.SPECIALIST,
        "osimertinib" to MedicineRarity.SPECIALIST,
        "tagrisso" to MedicineRarity.SPECIALIST,
        "trastuzumab" to MedicineRarity.SPECIALIST,
        "herceptin" to MedicineRarity.SPECIALIST,
        "bevacizumab" to MedicineRarity.SPECIALIST,
        "avastin" to MedicineRarity.SPECIALIST,
        "rituximab" to MedicineRarity.SPECIALIST,
        "pembrolizumab" to MedicineRarity.SPECIALIST,
        "keytruda" to MedicineRarity.SPECIALIST,
        "nivolumab" to MedicineRarity.SPECIALIST,
        "capecitabine" to MedicineRarity.SPECIALIST,
        "xeloda" to MedicineRarity.SPECIALIST,
        "cisplatin" to MedicineRarity.SPECIALIST,
        "carboplatin" to MedicineRarity.SPECIALIST,
        "paclitaxel" to MedicineRarity.SPECIALIST,
        "docetaxel" to MedicineRarity.SPECIALIST,
        "doxorubicin" to MedicineRarity.SPECIALIST,
        "cyclophosphamide" to MedicineRarity.SPECIALIST,
        "sunitinib" to MedicineRarity.SPECIALIST,
        "pazopanib" to MedicineRarity.SPECIALIST,
        "regorafenib" to MedicineRarity.SPECIALIST,
        "palbociclib" to MedicineRarity.SPECIALIST,
        "abiraterone" to MedicineRarity.SPECIALIST,
        "enzalutamide" to MedicineRarity.SPECIALIST,
        "filgrastim" to MedicineRarity.SPECIALIST,
        "immunoglobulin" to MedicineRarity.SPECIALIST,
        "adalimumab" to MedicineRarity.SPECIALIST,
        "etanercept" to MedicineRarity.SPECIALIST
    )

    /** True when the medicine isn't in the catalog and MODERATE was assumed. */
    fun isKnown(medicine: String): Boolean {
        val m = medicine.trim().lowercase()
        if (m.isEmpty()) return false
        return catalog.any { (key, _) -> m.contains(key) }
    }

    fun rarityOf(medicine: String): MedicineRarity {
        val m = medicine.trim().lowercase()
        if (m.isEmpty()) return MedicineRarity.MODERATE
        // Longest key first so "lenvatinib" isn't shadowed by a shorter entry.
        return catalog
            .filter { (key, _) -> m.contains(key) }
            .maxByOrNull { it.first.length }
            ?.second
            ?: MedicineRarity.MODERATE
    }

    /**
     * Chance (0..100) that a pharmacy of this tier stocks a medicine of this
     * rarity. Heuristic, not real inventory — the UI labels it as an estimate.
     */
    fun availability(rarity: MedicineRarity, tierLabel: String): Int {
        val tierIndex = when (tierLabel) {
            PharmacyTier.HOSPITAL -> 3
            PharmacyTier.BRAND -> 2
            PharmacyTier.MID -> 1
            else -> 0
        }
        val row = when (rarity) {
            //                     small mid brand hospital
            MedicineRarity.COMMON -> intArrayOf(95, 97, 98, 98)
            MedicineRarity.MODERATE -> intArrayOf(55, 75, 88, 92)
            MedicineRarity.RARE -> intArrayOf(12, 30, 55, 78)
            MedicineRarity.SPECIALIST -> intArrayOf(3, 8, 25, 70)
        }
        return row[tierIndex]
    }

    fun parseList(raw: String): List<String> =
        raw.split(",", "\n").map { it.trim() }.filter { it.isNotEmpty() }

    /**
     * Chance of getting EVERY requested medicine at one pharmacy — driven by the
     * hardest item to find, since that is what decides whether the trip succeeds.
     */
    fun availabilityForAll(medicines: List<String>, tierLabel: String): Int {
        if (medicines.isEmpty()) return PharmacyTier.projectedAvailability(tierLabel)
        return medicines.minOf { availability(rarityOf(it), tierLabel) }
    }

    /** Legend bucket for an availability percentage. */
    fun stockLabel(percent: Int): String = when {
        percent >= 80 -> "Very likely"
        percent >= 60 -> "Likely"
        percent >= 35 -> "Uncertain"
        else -> "Unlikely"
    }
}
