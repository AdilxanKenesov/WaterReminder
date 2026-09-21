package com.visionsystems.waterreminder.domain.usecase.goalready

import com.visionsystems.waterreminder.domain.module.GoalSummaryUiData

interface GoalReadyUseCase {
    suspend fun prepareGoal(): GoalSummaryUiData
}
