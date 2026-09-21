package com.visionsystems.waterreminder.domain.repository

import com.visionsystems.waterreminder.domain.module.DayTotalUiData
import com.visionsystems.waterreminder.domain.module.WaterStatsUiData
import kotlinx.coroutines.flow.Flow

interface StatsRepository {
    fun observeLastDays(days: Int): Flow<List<DayTotalUiData>>
    fun observeStats(): Flow<WaterStatsUiData>
    suspend fun getCurrentStreak(): Int
}
