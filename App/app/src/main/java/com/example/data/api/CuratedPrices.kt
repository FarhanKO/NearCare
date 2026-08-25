package com.example.data.api

/**
 * Hand-entered rate cards for places that publish them.
 *
 * This is the cheapest and most reliable tier: instant, free, no quota, no
 * parsing, no model. When a chain publishes a rate list, transcribing it here
 * beats every automated route.
 *
 * ─────────────────────────────────────────────────────────────────────────────
 * DELIBERATELY SHIPPED EMPTY.
 *
 * Filling this in from memory would mean attaching invented figures to named
 * real businesses and labelling them "published" — the exact failure this whole
 * feature was built to avoid, and worse than the old hash because it looks
 * credible.
 *
 * To populate it, open the centre's real rate card and transcribe it:
 *
 *   "popular diagnostic" to RateCard(
 *       sourceUrl = "https://populardiagnostic.com/test-list",
 *       updated   = "2026-08",
 *       prices    = mapOf("CBC" to "BDT 400", "Glucose" to "BDT 150")
 *   )
 *
 * Match keys are lowercase substrings tested against the place name, so
 * "popular diagnostic" matches "Popular Diagnostic Centre Ltd, Dhanmondi".
 * Add branch-specific keys ("popular diagnostic dhanmondi") when branches differ —
 * the longest matching key wins.
 * ─────────────────────────────────────────────────────────────────────────────
 */
object CuratedPrices {

    data class RateCard(
        val sourceUrl: String,
        val updated: String,
        val prices: Map<String, String>
    )

    /** placeNameFragment (lowercase) -> published rate card. */
    private val cards: Map<String, RateCard> = emptyMap()

    /** Longest matching key wins, so a branch entry beats a chain-wide one. */
    fun lookup(placeName: String, items: List<String>): PriceOutcome? {
        if (cards.isEmpty() || placeName.isBlank()) return null
        val name = placeName.lowercase()

        val card = cards.entries
            .filter { name.contains(it.key) }
            .maxByOrNull { it.key.length }
            ?.value ?: return null

        val matched = items.mapNotNull { requested ->
            val hit = card.prices.entries.firstOrNull { (test, _) ->
                test.equals(requested, ignoreCase = true) ||
                    requested.contains(test, ignoreCase = true) ||
                    test.contains(requested, ignoreCase = true)
            } ?: return@mapNotNull null

            PriceItem(
                item = requested,
                price = hit.value,
                basis = "published",
                source = card.sourceUrl,
                basisNote = ""
            )
        }

        if (matched.isEmpty()) return null
        return PriceOutcome.Found(
            items = matched,
            note = "From this provider's published rate card (${card.updated}).",
            origin = PriceOrigin.CURATED
        )
    }
}
