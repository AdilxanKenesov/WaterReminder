package com.visionsystems.waterreminder.domain.util

import com.visionsystems.waterreminder.domain.module.AchievementType

object AchievementRules {
    private const val TEN_LITERS_ML = 10_000L
    private const val HUNDRED_LITERS_ML = 100_000L

    fun unlocked(totalMl: Long, entryCount: Int, completedDays: Int, bestStreak: Int): Set<AchievementType> =
        buildSet {
            if (entryCount >= 1) add(AchievementType.FIRST_GLASS)
            if (completedDays >= 1) add(AchievementType.FIRST_GOAL)
            if (bestStreak >= 3) add(AchievementType.STREAK_3)
            if (bestStreak >= 7) add(AchievementType.STREAK_7)
            if (bestStreak >= 30) add(AchievementType.STREAK_30)
            if (totalMl >= TEN_LITERS_ML) add(AchievementType.TOTAL_10_LITERS)
            if (totalMl >= HUNDRED_LITERS_ML) add(AchievementType.TOTAL_100_LITERS)
        }
}
