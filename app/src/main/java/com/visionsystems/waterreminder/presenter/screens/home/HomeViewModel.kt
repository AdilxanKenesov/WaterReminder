package com.visionsystems.waterreminder.presenter.screens.home

import androidx.lifecycle.ViewModel
import com.visionsystems.waterreminder.R
import com.visionsystems.waterreminder.domain.module.CupSizeUiData
import com.visionsystems.waterreminder.domain.module.HomeUiData
import com.visionsystems.waterreminder.domain.usecase.home.HomeUseCase
import com.visionsystems.waterreminder.domain.util.toDayKey
import com.visionsystems.waterreminder.presenter.ui.util.UiText
import com.visionsystems.waterreminder.presenter.ui.util.titleRes
import com.visionsystems.waterreminder.presenter.ui.util.toVolumeText
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.viewmodel.orbitContainer
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val direction: HomeContract.Direction,
    private val homeUseCase: HomeUseCase
) : ViewModel(), HomeContract.HomeViewModel {

    override val container = orbitContainer<HomeContract.HomeUiState, HomeContract.SideEffect>(HomeContract.HomeUiState())

    init {
        intent {
            homeUseCase.observeHome().collect { data -> reduce { state.withData(data) } }
        }
    }

    override fun onEventDispatcher(event: HomeContract.HomeEvent) {
        when (event) {
            is HomeContract.HomeEvent.QuickAdd -> intent {
                if (state.isAdding) return@intent
                reduce { state.copy(isAdding = true) }
                val result = homeUseCase.addDrink(event.amountMl)
                reduce { state.copy(isAdding = false) }
                when {
                    result.goalJustReached -> direction.openGoalReached()
                    result.newAchievements.isNotEmpty() -> postSideEffect(
                        HomeContract.SideEffect.ShowMessage(
                            UiText.Res(R.string.new_badge, listOf(UiText.Res(result.newAchievements.first().titleRes)))
                        )
                    )
                    else -> postSideEffect(
                        HomeContract.SideEffect.ShowAdded(
                            message = UiText.Res(R.string.water_added, listOf(event.amountMl.toVolumeText(state.unit))),
                            entryId = result.entryId
                        )
                    )
                }
            }

            is HomeContract.HomeEvent.UndoClicked -> intent { homeUseCase.undoDrink(event.entryId) }

            is HomeContract.HomeEvent.DeleteRequested -> intent { reduce { state.copy(pendingDeleteId = event.entryId) } }

            HomeContract.HomeEvent.DeleteDismissed -> intent { reduce { state.copy(pendingDeleteId = null) } }

            HomeContract.HomeEvent.DeleteConfirmed -> intent {
                val entryId = state.pendingDeleteId ?: return@intent
                reduce { state.copy(pendingDeleteId = null) }
                homeUseCase.undoDrink(entryId)
            }
        }
    }

    private fun HomeContract.HomeUiState.withData(data: HomeUiData) = copy(
        isLoading = false,
        now = data.now,
        userName = data.userName,
        consumedMl = data.progress.consumedMl,
        goalMl = data.progress.goalMl,
        progress = data.progress.progress,
        remainingMl = data.progress.remainingMl,
        isCompleted = data.progress.isCompleted,
        streak = data.streak,
        week = data.week,
        todayKey = data.now.toLocalDate().toDayKey(),
        quickCups = quickCups(data.cups),
        entries = data.progress.entries.sortedByDescending { it.timestamp },
        nextReminder = data.nextReminder,
        unit = data.unit
    )

    private fun quickCups(cups: List<CupSizeUiData>): List<CupSizeUiData> {
        val sorted = cups.sortedBy { it.amountMl }
        if (sorted.size <= QUICK_CUPS) return sorted
        val selectedIndex = sorted.indexOfFirst { it.isSelected }.coerceAtLeast(0)
        val start = (selectedIndex - 1).coerceIn(0, sorted.size - QUICK_CUPS)
        return sorted.subList(start, start + QUICK_CUPS)
    }

    private companion object {
        const val QUICK_CUPS = 4
    }
}
