package com.example.data.api

import org.json.JSONArray
import org.json.JSONObject

/**
 * One priced line item.
 *
 * [basis] is the honesty contract for the whole feature:
 *  - "published" -> found on a page that names this exact place. [source] MUST be set.
 *  - "estimated" -> a market/typical figure. [source] is empty, [basisNote] says what
 *    it is based on. The UI must tag these visibly.
 */
data class PriceItem(
    val item: String,
    val price: String,
    val basis: String,
    val source: String = "",
    val basisNote: String = ""
) {
    val isPublished: Boolean get() = basis.equals("published", ignoreCase = true)

    fun toJson(): JSONObject = JSONObject().apply {
        put("item", item)
        put("price", price)
        put("basis", basis)
        put("source", source)
        put("basisNote", basisNote)
    }

    companion object {
        fun fromJson(o: JSONObject) = PriceItem(
            item = o.optString("item"),
            price = o.optString("price"),
            basis = o.optString("basis", "estimated"),
            source = o.optString("source", ""),
            basisNote = o.optString("basisNote", "")
        )
    }
}

/** Where a result came from — shown to the user so nothing is mysterious. */
enum class PriceOrigin(val label: String) {
    CACHE("saved"),
    CURATED("published rate card"),
    WEBSITE("this place's website"),
    AI("web search")
}

sealed interface PriceOutcome {
    data class Found(
        val items: List<PriceItem>,
        val note: String,
        val origin: PriceOrigin
    ) : PriceOutcome

    data class NotFound(val reason: String) : PriceOutcome
    data class Failed(val message: String) : PriceOutcome
}

/** Serialisation helpers so results can be cached in Room as a single string. */
object PriceJson {
    fun encode(items: List<PriceItem>, note: String, origin: PriceOrigin): String =
        JSONObject().apply {
            put("items", JSONArray().also { arr -> items.forEach { arr.put(it.toJson()) } })
            put("note", note)
            put("origin", origin.name)
        }.toString()

    fun decode(raw: String): PriceOutcome.Found? = try {
        val o = JSONObject(raw)
        val arr = o.optJSONArray("items") ?: JSONArray()
        val items = (0 until arr.length()).mapNotNull { i ->
            arr.optJSONObject(i)?.let { PriceItem.fromJson(it) }
        }
        if (items.isEmpty()) null
        else PriceOutcome.Found(
            items = items,
            note = o.optString("note"),
            origin = runCatching { PriceOrigin.valueOf(o.optString("origin")) }
                .getOrDefault(PriceOrigin.CACHE)
        )
    } catch (e: Exception) {
        null
    }
}
