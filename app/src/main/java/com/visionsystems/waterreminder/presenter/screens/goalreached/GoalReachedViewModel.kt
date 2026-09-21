package com.visionsystems.waterreminder.presenter.screens.goalreached

import androidx.lifecycle.ViewModel
import com.visionsystems.waterreminder.domain.usecase.goalreached.GoalReachedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.viewmodel.orbitContainer
import javax.inject.Inject

@HiltViewModel
class GoalReachedViewModel @Inject constructor(
    private val direction: GoalReachedContract.Direction,
    private val goalReachedUseCase: GoalReachedUseCase
) : ViewModel(), GoalReachedContract.GoalReachedViewModel {

    override val container = orbitContainer<GoalReachedContract.GoalReachedUiState, GoalReachedContract.SideEffect>(
        GoalReachedContract.GoalReachedUiState()
    )

    init {
        intent {
            val data = goalReachedUseCase.load()
            reduce {
                state.copy(
                    isLoading = false,
                    consumedMl = data.consumedMl,
                    streak = data.streak,
                    unit = data.unit
                )
            }
        }
    }

    override fun onEventDispatcher(event: GoalReachedContract.GoalReachedEvent) {
        when (event) {
            GoalReachedContract.GoalReachedEvent.BackHomeClicked -> direction.back()
        }
    }
}
