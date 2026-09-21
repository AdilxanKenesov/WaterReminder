package com.visionsystems.waterreminder.presenter.screens.home

import com.visionsystems.waterreminder.domain.module.CupSizeUiData
import com.visionsystems.waterreminder.domain.module.DayTotalUiData
import com.visionsystems.waterreminder.domain.module.DrinkEntryUiData
import com.visionsystems.waterreminder.domain.module.WaterUnit
import com.visionsystems.waterreminder.presenter.ui.util.UiText
import org.orbitmvi.orbit.OrbitContainerHost
import java.time.LocalDateTime
import java.time.LocalTime

interface HomeContract {

    interface HomeViewModel : OrbitContainerHost<HomeUiState, HomeUiState, SideEffect> {
        fun onEventDispatcher(event: HomeEvent)
    }

    sealed interface HomeEvent {
        data class QuickAdd(val amountMl: Int) : HomeEvent
        data class UndoClicked(val entryId: Long) : HomeEvent
        data class DeleteRequested(val entryId: Long) : HomeEvent
        data object DeleteConfirmed : HomeEvent
        data object DeleteDismissed : HomeEvent
    }

    data class HomeUiState(
        val isLoading: Boolean = true,
        val now: LocalDateTime? = null,
        val userName: String = "",
        val consumedMl: Int = 0,
        val goalMl: Int = 0,
        val progress: Float = 0f,
        val remainingMl: Int = 0,
        val isCompleted: Boolean = false,
        val streak: Int = 0,
        val week: List<DayTotalUiData> = emptyList(),
        val todayKey: Int = 0,
        val quickCups: List<CupSizeUiData> = emptyList(),
        val entries: List<DrinkEntryUiData> = emptyList(),
        val nextReminder: LocalTime? = null,
        val unit: WaterUnit = WaterUnit.ML,
        val isAdding: Boolean = false,
        val pendingDeleteId: Long? = null
    ) {
        val percent: Int get() = (progress * 100).toInt()
        val pendingDelete: DrinkEntryUiData? get() = entries.firstOrNull { it.id == pendingDeleteId }
    }

    sealed interface SideEffect {
        data class ShowAdded(val message: UiText, val entryId: Long) : SideEffect
        data class ShowMessage(val message: UiText) : SideEffect
    }

    interface Direction {
        fun openGoalReached()
    }
}
