package com.visionsystems.waterreminder.domain.module

data class GoalSummaryUiData(
    val goalMl: Int,
    val weightKg: Int,
    val activityLevel: ActivityLevel,
    val cupMl: Int
)

data class SetGoalUiData(
    val goalMl: Int,
    val recommendedMl: Int,
    val cups: List<CupSizeUiData>,
    val unit: WaterUnit
)

data class GoalReachedUiData(
    val consumedMl: Int,
    val streak: Int,
    val unit: WaterUnit
)
