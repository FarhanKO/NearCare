package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DiagnosticCenter
import kotlinx.coroutines.flow.Flow

@Dao
interface DiagnosticDao {
    @Query("SELECT * FROM diagnostic_centers")
    fun getAllCenters(): Flow<List<DiagnosticCenter>>

    @Query("SELECT * FROM diagnostic_centers WHERE id = :id")
    suspend fun getCenterById(id: Int): DiagnosticCenter?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(centers: List<DiagnosticCenter>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(center: DiagnosticCenter)

    @Update
    suspend fun update(center: DiagnosticCenter)

    @Query("SELECT COUNT(*) FROM diagnostic_centers")
    suspend fun getCount(): Int

    @Query("DELETE FROM diagnostic_centers")
    suspend fun clearAll()

    /**
     * Clears one mode's cached results before a fresh search so stale rows never
     * mix into the list. Favorites are preserved.
     */
    @Query("DELETE FROM diagnostic_centers WHERE category = :category AND isFavorite = 0")
    suspend fun clearCategory(category: String)

    @Query("UPDATE diagnostic_centers SET isFavorite = :isFav WHERE id = :id")
    suspend fun toggleFavorite(id: Int, isFav: Boolean)
}
