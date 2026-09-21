package com.visionsystems.waterreminder.domain.repository

import com.visionsystems.waterreminder.domain.module.DailyProgressUiData
import kotlinx.coroutines.flow.Flow

interface DrinkRepository {
    fun observeToday(): Flow<DailyProgressUiData>
    suspend fun getToday(): DailyProgressUiData
    suspend fun addDrink(amountMl: Int): Long
    suspend fun deleteDrink(entryId: Long)
}
