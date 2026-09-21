package com.visionsystems.waterreminder.domain.module

data class WaterStatsUiData(
    val weeklyAverageMl: Int,
    val monthlyAverageMl: Int,
    val averageCompletionPercent: Int,
    val drinksPerDay: Float,
    val currentStreak: Int,
    val bestStreak: Int,
    val totalMl: Long
)
