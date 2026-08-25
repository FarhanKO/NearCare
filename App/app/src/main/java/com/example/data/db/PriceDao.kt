package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.PriceLookup

@Dao
interface PriceDao {

    @Query("SELECT * FROM price_lookups WHERE cacheKey = :key LIMIT 1")
    suspend fun get(key: String): PriceLookup?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(entry: PriceLookup)

    @Query("DELETE FROM price_lookups WHERE fetchedAt < :cutoff")
    suspend fun evictOlderThan(cutoff: Long)

    @Query("DELETE FROM price_lookups")
    suspend fun clearAll()
}
