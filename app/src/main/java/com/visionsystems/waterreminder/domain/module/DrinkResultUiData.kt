package com.visionsystems.waterreminder.domain.module

data class DrinkResultUiData(
    val entryId: Long,
    val progress: DailyProgressUiData,
    val goalJustReached: Boolean,
    val newAchievements: List<AchievementType>
)
