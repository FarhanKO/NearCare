package com.example.data.api

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

/**
 * Free tier: read the place's OWN website (the URL Google Places gave us) and
 * look for prices next to the requested item names.
 *
 * Costs nothing, has no quota, and anything found here is genuinely "published"
 * because it came from the provider's own page — so the source URL is real.
 *
 * Deliberately conservative: it only reports a number that sits close to the
 * item name AND carries a currency marker. When a site renders prices with
 * JavaScript or publishes a PDF rate card (common in Bangladesh) this finds
 * nothing, and that is the correct outcome — the caller falls through to the
 * next tier rather than guessing.
 */
object WebsitePriceReader {

    private const val TAG = "WebsitePriceReader"

    private val client = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()

    /** "BDT 400", "Tk. 1,200", "৳450", "400 Taka" */
    private val currencyNearby = Regex(
        """(?:(?:bdt|tk\.?|taka|৳)\s*([0-9][0-9,]{1,6}))|(?:([0-9][0-9,]{1,6})\s*(?:bdt|tk\.?|taka|৳))""",
        RegexOption.IGNORE_CASE
    )

    /** Common paths where rate lists live, tried after the landing page. */
    private val candidatePaths = listOf(
        "", "/price", "/prices", "/price-list", "/pricelist",
        "/test-list", "/tests", "/rate", "/rates", "/our-services", "/services"
    )

    suspend fun read(
        websiteUri: String,
        items: List<String>
    ): PriceOutcome? = withContext(Dispatchers.IO) {
        if (websiteUri.isBlank() || items.isEmpty()) return@withContext null

        val base = websiteUri.trimEnd('/')
        val found = linkedMapOf<String, PriceItem>()

        for (path in candidatePaths) {
            if (found.size >= items.size) break
            val url = if (path.isEmpty()) base else "$base$path"
            val text = fetchText(url) ?: continue

            for (item in items) {
                if (found.containsKey(item.lowercase())) continue
                val price = priceNear(text, item) ?: continue
                found[item.lowercase()] = PriceItem(
                    item = item,
                    price = price,
                    basis = "published",
                    source = url,
                    basisNote = ""
                )
            }
        }

        if (found.isEmpty()) return@withContext null
        PriceOutcome.Found(
            items = found.values.toList(),
            note = "Read from this provider's own website. Confirm before travelling.",
            origin = PriceOrigin.WEBSITE
        )
    }

    private fun fetchText(url: String): String? = try {
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "Mozilla/5.0 (Android) NearCare/1.0")
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) null
            else response.body?.string()?.let { stripHtml(it) }
        }
    } catch (e: Exception) {
        Log.d(TAG, "fetch failed for $url: ${e.message}")
        null
    }

    private fun stripHtml(html: String): String =
        html.replace(Regex("(?is)<(script|style)[^>]*>.*?</\\1>"), " ")
            .replace(Regex("<[^>]+>"), " ")
            .replace(Regex("&nbsp;?", RegexOption.IGNORE_CASE), " ")
            .replace(Regex("\\s+"), " ")

    /**
     * Finds a currency figure within a short window of the item name. The window
     * is tight on purpose — a price far from the label is probably a different
     * row of the table.
     */
    private fun priceNear(text: String, item: String, window: Int = 160): String? {
        val hay = text.lowercase()
        val needle = item.lowercase().trim()
        if (needle.length < 2) return null

        var from = 0
        while (true) {
            val at = hay.indexOf(needle, from)
            if (at < 0) return null
            val end = minOf(text.length, at + needle.length + window)
            val slice = text.substring(at + needle.length, end)
            val match = currencyNearby.find(slice)
            if (match != null) {
                val digits = (match.groupValues[1].ifBlank { match.groupValues[2] })
                    .replace(",", "")
                if (digits.isNotBlank()) return "BDT $digits"
            }
            from = at + needle.length
        }
    }
}
