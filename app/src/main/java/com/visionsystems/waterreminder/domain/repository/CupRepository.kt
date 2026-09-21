package com.visionsystems.waterreminder.domain.repository

import com.visionsystems.waterreminder.domain.module.CupSizeUiData
import kotlinx.coroutines.flow.Flow

interface CupRepository {
    fun observeCups(): Flow<List<CupSizeUiData>>
    suspend fun getSelectedCupMl(): Int
    suspend fun selectCup(cupId: Long)
    suspend fun addCup(amountMl: Int)
    suspend fun deleteCup(cupId: Long)
}
