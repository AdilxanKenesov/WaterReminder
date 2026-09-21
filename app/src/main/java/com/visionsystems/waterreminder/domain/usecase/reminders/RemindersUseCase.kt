package com.visionsystems.waterreminder.domain.usecase.reminders

import com.visionsystems.waterreminder.domain.module.ReminderConfigUiData
import com.visionsystems.waterreminder.domain.module.RemindersUiData
import kotlinx.coroutines.flow.Flow
import java.time.LocalTime

interface RemindersUseCase {
    fun observeReminders(): Flow<RemindersUiData>
    suspend fun updateConfig(config: ReminderConfigUiData)
    suspend fun updateTimes(wake: LocalTime, sleep: LocalTime)
}
