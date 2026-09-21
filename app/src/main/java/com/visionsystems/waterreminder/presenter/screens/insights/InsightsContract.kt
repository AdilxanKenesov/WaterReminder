package com.visionsystems.waterreminder.presenter.screens.insights

import androidx.annotation.StringRes
import com.visionsystems.waterreminder.R
import com.visionsystems.waterreminder.domain.module.AchievementUiData
import com.visionsystems.waterreminder.domain.module.DayTotalUiData
import com.visionsystems.waterreminder.domain.module.WaterUnit
import org.orbitmvi.orbit.OrbitContainerHost

interface InsightsContract {

    interface InsightsViewModel : OrbitContainerHost<InsightsUiState, InsightsUiState, SideEffect> {
        fun onEventDispatcher(event: InsightsEvent)
    }

    enum class Period(val days: Int, @param:StringRes val title: Int) {
        WEEK(7, R.string.period_week),
        MONTH(30, R.string.period_month)
    }

    sealed interface InsightsEvent {
        data class PeriodSelected(val period: Period) : InsightsEvent
    }

    data class InsightsUiState(
        val isLoading: Boolean = true,
        val period: Period = Period.WEEK,
        val days: List<DayTotalUiData> = emptyList(),
        val averageMl: Int = 0,
        val goalMl: Int = 0,
        val goalReachedDays: Int = 0,
        val bestStreak: Int = 0,
        val drinksPerDay: Float = 0f,
        val achievements: List<AchievementUiData> = emptyList(),
        val unit: WaterUnit = WaterUnit.ML
    ) {
        val unlockedCount: Int get() = achievements.count { it.isUnlocked }
    }

    sealed interface SideEffect

    interface Direction
}
