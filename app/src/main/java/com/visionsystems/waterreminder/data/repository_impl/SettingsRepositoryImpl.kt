package com.visionsystems.waterreminder.data.repository_impl

import com.visionsystems.waterreminder.data.source.local.pref.SharedManager
import com.visionsystems.waterreminder.domain.module.WaterUnit
import com.visionsystems.waterreminder.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val sharedManager: SharedManager
) : SettingsRepository {

    private val unitState = MutableStateFlow(
        WaterUnit.entries.firstOrNull { it.name == sharedManager.unit } ?: WaterUnit.ML
    )

    override fun isOnboardingDone(): Boolean = sharedManager.onboardingDone

    override fun setOnboardingDone(done: Boolean) {
        sharedManager.onboardingDone = done
    }

    override fun getUnit(): StateFlow<WaterUnit> = unitState.asStateFlow()

    override fun setUnit(unit: WaterUnit) {
        sharedManager.unit = unit.name
        unitState.value = unit
    }

    override fun wasGoalNotified(uid: String, dayKey: Int): Boolean =
        sharedManager.getNotifiedDay(SharedManager.NOTIFIED_GOAL, uid) == dayKey

    override fun markGoalNotified(uid: String, dayKey: Int) {
        sharedManager.setNotifiedDay(SharedManager.NOTIFIED_GOAL, uid, dayKey)
    }

    override fun wasStreakNotified(uid: String, dayKey: Int): Boolean =
        sharedManager.getNotifiedDay(SharedManager.NOTIFIED_STREAK, uid) == dayKey

    override fun markStreakNotified(uid: String, dayKey: Int) {
        sharedManager.setNotifiedDay(SharedManager.NOTIFIED_STREAK, uid, dayKey)
    }

    override fun getLanguage(): String? = sharedManager.language

    override fun setLanguage(tag: String) {
        sharedManager.language = tag
    }

    override fun isReviewRequested(): Boolean = sharedManager.reviewRequested

    override fun markReviewRequested() {
        sharedManager.reviewRequested = true
    }
}
