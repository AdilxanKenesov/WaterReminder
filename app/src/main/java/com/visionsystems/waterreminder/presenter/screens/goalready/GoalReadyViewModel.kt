package com.visionsystems.waterreminder.presenter.screens.goalready

import androidx.lifecycle.ViewModel
import com.visionsystems.waterreminder.domain.usecase.goalready.GoalReadyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.viewmodel.orbitContainer
import javax.inject.Inject

@HiltViewModel
class GoalReadyViewModel @Inject constructor(
    private val direction: GoalReadyContract.Direction,
    private val goalReadyUseCase: GoalReadyUseCase
) : ViewModel(), GoalReadyContract.GoalReadyViewModel {

    override val container = orbitContainer<GoalReadyContract.GoalReadyUiState, GoalReadyContract.SideEffect>(GoalReadyContract.GoalReadyUiState())

    init {
        intent {
            val summary = goalReadyUseCase.prepareGoal()
            reduce {
                state.copy(
                    isLoading = false,
                    goalMl = summary.goalMl,
                    weightKg = summary.weightKg,
                    activityLevel = summary.activityLevel,
                    cupMl = summary.cupMl
                )
            }
        }
    }

    override fun onEventDispatcher(event: GoalReadyContract.GoalReadyEvent) {
        when (event) {
            GoalReadyContract.GoalReadyEvent.StartClicked -> direction.openMain()
            GoalReadyContract.GoalReadyEvent.AdjustClicked -> direction.openSetGoal()
        }
    }
}
