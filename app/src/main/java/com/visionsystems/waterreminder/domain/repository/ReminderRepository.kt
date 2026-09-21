package com.visionsystems.waterreminder.domain.repository

import com.visionsystems.waterreminder.domain.module.ReminderConfigUiData
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    fun observeConfig(): Flow<ReminderConfigUiData>
    suspend fun getConfig(): ReminderConfigUiData
    suspend fun updateConfig(config: ReminderConfigUiData)
}
