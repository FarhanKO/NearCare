package com.example.data.repository

import com.example.data.db.DiagnosticDao
import com.example.data.model.DiagnosticCenter
import kotlinx.coroutines.flow.Flow

class DiagnosticRepository(private val diagnosticDao: DiagnosticDao) {
    val allCenters: Flow<List<DiagnosticCenter>> = diagnosticDao.getAllCenters()

    suspend fun insertAll(centers: List<DiagnosticCenter>) = diagnosticDao.insertAll(centers)

    suspend fun update(center: DiagnosticCenter) = diagnosticDao.update(center)

    suspend fun toggleFavorite(id: Int, isFav: Boolean) = diagnosticDao.toggleFavorite(id, isFav)

    suspend fun getCount(): Int = diagnosticDao.getCount()

    suspend fun clearAll() = diagnosticDao.clearAll()

    suspend fun clearCategory(category: String) = diagnosticDao.clearCategory(category)
}
