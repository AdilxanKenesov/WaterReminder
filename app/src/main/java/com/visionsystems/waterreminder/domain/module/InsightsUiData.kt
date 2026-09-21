package com.visionsystems.waterreminder.domain.module

data class InsightsUiData(
    val days: List<DayTotalUiData>,
    val stats: WaterStatsUiData,
    val achievements: List<AchievementUiData>,
    val unit: WaterUnit
)
