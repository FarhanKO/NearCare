package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A cached price lookup.
 *
 * This is the single biggest cost saver: without it every tap on a place fired a
 * fresh grounded AI call. Keyed by place + what was asked for, so each centre is
 * looked up once and reused by every later visit.
 */
@Entity(tableName = "price_lookups")
data class PriceLookup(
    @PrimaryKey val cacheKey: String,
    val payload: String,
    val fetchedAt: Long
)
