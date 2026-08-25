package com.example.data.api

import android.content.Context
import android.util.Log
import com.example.data.db.AppDatabase
import com.example.data.model.PriceLookup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * Cheapest-first price cascade.
 *
 *   1. cache     - instant, free, and the reason the AI quota stops burning
 *   2. curated   - hand-entered published rate cards (see CuratedPrices)
 *   3. website   - the place's own site, parsed for prices
 *   4. AI        - grounded Gemini search, only when 1-3 miss
 *
 * Anything found in tiers 2-4 is cached, so a given place costs at most one
 * network lookup ever. Previously every tap fired a fresh grounded call, which
 * is what exhausted the free quota in a handful of tests.
 */
object PriceRepository {

    private const val TAG = "PriceRepository"
    private val TTL_MS = TimeUnit.DAYS.toMillis(30)

    private fun cacheKey(placeId: String, kind: String, items: List<String>): String =
        buildString {
            append(placeId).append('|').append(kind).append('|')
            append(items.map { it.trim().lowercase() }.sorted().joinToString(","))
        }

    suspend fun getPrices(
        context: Context,
        placeId: String,
        placeName: String,
        area: String,
        websiteUri: String,
        kind: String,
        items: List<String>,
        forceRefresh: Boolean = false
    ): PriceOutcome = withContext(Dispatchers.IO) {
        val dao = AppDatabase.getDatabase(context).priceDao()
        val key = cacheKey(placeId, kind, items)

        // 1 ── cache
        if (!forceRefresh) {
            runCatching { dao.get(key) }.getOrNull()?.let { hit ->
                if (System.currentTimeMillis() - hit.fetchedAt < TTL_MS) {
                    PriceJson.decode(hit.payload)?.let { cached ->
                        Log.d(TAG, "cache hit for $placeName")
                        return@withContext cached.copy(origin = PriceOrigin.CACHE)
                    }
                }
            }
        }

        // 2 ── curated rate card (free, instant, most reliable)
        CuratedPrices.lookup(placeName, items)?.let { curated ->
            store(dao, key, curated)
            return@withContext curated
        }

        // 3 ── the place's own website (free)
        runCatching { WebsitePriceReader.read(websiteUri, items) }.getOrNull()?.let { web ->
            store(dao, key, web)
            return@withContext web
        }

        // 4 ── grounded AI search (costs quota, so it runs last and is cached)
        val ai = PriceFinder.lookup(placeName, area, kind, items)
        if (ai is PriceOutcome.Found) store(dao, key, ai)
        ai
    }

    private suspend fun store(
        dao: com.example.data.db.PriceDao,
        key: String,
        outcome: PriceOutcome
    ) {
        if (outcome !is PriceOutcome.Found) return
        runCatching {
            dao.put(
                PriceLookup(
                    cacheKey = key,
                    payload = PriceJson.encode(outcome.items, outcome.note, outcome.origin),
                    fetchedAt = System.currentTimeMillis()
                )
            )
        }.onFailure { Log.e(TAG, "cache write failed", it) }
    }

    /** Used by the "refresh" affordance so a user can bust a stale entry. */
    suspend fun clearCache(context: Context) = withContext(Dispatchers.IO) {
        runCatching { AppDatabase.getDatabase(context).priceDao().clearAll() }
        Unit
    }
}
