package com.visionsystems.waterreminder.domain.usecase.goalreached

import com.visionsystems.waterreminder.domain.module.GoalReachedUiData

interface GoalReachedUseCase {
    suspend fun load(): GoalReachedUiData
}
