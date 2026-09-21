package com.visionsystems.waterreminder.domain.repository

import com.visionsystems.waterreminder.domain.module.AchievementType

interface NotificationRepository {
    fun canNotify(): Boolean
    fun showReminder(consumedMl: Int, goalMl: Int, cupMl: Int, silent: Boolean)
    fun showGoalReached(goalMl: Int)
    fun showStreakAtRisk(streak: Int, remainingMl: Int, cupMl: Int)
    fun showAchievement(type: AchievementType)
    fun cancelReminder()
}
