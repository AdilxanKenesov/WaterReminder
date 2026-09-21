package com.visionsystems.waterreminder.presenter.screens.setgoal

import androidx.lifecycle.ViewModel
import com.visionsystems.waterreminder.domain.usecase.setgoal.SetGoalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.viewmodel.orbitContainer
import javax.inject.Inject

@HiltViewModel
class SetGoalViewModel @Inject constructor(
    private val direction: SetGoalContract.Direction,
    private val setGoalUseCase: SetGoalUseCase
) : ViewModel(), SetGoalContract.SetGoalViewModel {

    override val container = orbitContainer<SetGoalContract.SetGoalUiState, SetGoalContract.SideEffect>(
        SetGoalContract.SetGoalUiState(
            minGoalMl = setGoalUseCase.goalRange.first,
            maxGoalMl = setGoalUseCase.goalRange.last
        )
    )

    init {
        intent {
            val data = setGoalUseCase.load()
            reduce {
                state.copy(
                    isLoading = false,
                    goalMl = data.goalMl,
                    recommendedMl = data.recommendedMl,
                    cups = data.cups.sortedBy { it.amountMl },
                    selectedCupId = data.cups.firstOrNull { it.isSelected }?.id,
                    unit = data.unit
                )
            }
        }
    }

    override fun onEventDispatcher(event: SetGoalContract.SetGoalEvent) {
        when (event) {
            SetGoalContract.SetGoalEvent.IncreaseClicked -> changeGoal(SetGoalContract.GOAL_STEP_ML)

            SetGoalContract.SetGoalEvent.DecreaseClicked -> changeGoal(-SetGoalContract.GOAL_STEP_ML)

            SetGoalContract.SetGoalEvent.UseRecommendedClicked -> intent {
                reduce { state.copy(goalMl = state.recommendedMl) }
            }

            is SetGoalContract.SetGoalEvent.CupSelected -> intent {
                reduce { state.copy(selectedCupId = event.cupId) }
            }

            SetGoalContract.SetGoalEvent.SaveClicked -> intent {
                if (state.isSaving) return@intent
                reduce { state.copy(isSaving = true) }
                setGoalUseCase.save(state.goalMl, state.selectedCupId)
                direction.back()
            }

            SetGoalContract.SetGoalEvent.BackClicked -> direction.back()
        }
    }

    private fun changeGoal(delta: Int) = intent {
        reduce { state.copy(goalMl = (state.goalMl + delta).coerceIn(state.minGoalMl, state.maxGoalMl)) }
    }
}
