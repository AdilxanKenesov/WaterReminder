package com.visionsystems.waterreminder.domain.usecase.setgoal

import com.visionsystems.waterreminder.domain.module.SetGoalUiData

interface SetGoalUseCase {
    val goalRange: IntRange
    suspend fun load(): SetGoalUiData
    suspend fun save(goalMl: Int, cupId: Long?)
}
