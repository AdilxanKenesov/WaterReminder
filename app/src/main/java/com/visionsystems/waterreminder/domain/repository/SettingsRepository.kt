package com.visionsystems.waterreminder.domain.repository

import com.visionsystems.waterreminder.domain.module.WaterUnit
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    fun isOnboardingDone(): Boolean
    fun setOnboardingDone(done: Boolean)
    fun getUnit(): StateFlow<WaterUnit>
    fun setUnit(unit: WaterUnit)
    fun wasGoalNotified(uid: String, dayKey: Int): Boolean
    fun markGoalNotified(uid: String, dayKey: Int)
    fun wasStreakNotified(uid: String, dayKey: Int): Boolean
    fun markStreakNotified(uid: String, dayKey: Int)
    fun getLanguage(): String?
    fun setLanguage(tag: String)
    fun isReviewRequested(): Boolean
    fun markReviewRequested()
}
