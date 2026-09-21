package com.visionsystems.waterreminder.domain.repository

import com.visionsystems.waterreminder.domain.module.AchievementType
import com.visionsystems.waterreminder.domain.module.AchievementUiData
import kotlinx.coroutines.flow.Flow

interface AchievementRepository {
    fun observeAchievements(): Flow<List<AchievementUiData>>
    suspend fun evaluate(): List<AchievementType>
}
