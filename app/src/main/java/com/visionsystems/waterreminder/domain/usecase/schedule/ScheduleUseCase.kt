package com.visionsystems.waterreminder.domain.usecase.schedule

import com.visionsystems.waterreminder.domain.module.SchedulePlanUiData
import com.visionsystems.waterreminder.domain.module.UserProfileUiData
import java.time.LocalTime

interface ScheduleUseCase {
    suspend fun loadProfile(): UserProfileUiData?
    suspend fun previewPlan(wake: LocalTime, sleep: LocalTime): SchedulePlanUiData
    suspend fun saveTimes(wake: LocalTime, sleep: LocalTime)
}
