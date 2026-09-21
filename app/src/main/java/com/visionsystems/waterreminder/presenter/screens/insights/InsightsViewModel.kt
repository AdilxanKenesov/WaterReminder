package com.visionsystems.waterreminder.presenter.screens.insights

import androidx.lifecycle.ViewModel
import com.visionsystems.waterreminder.domain.usecase.insights.InsightsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import org.orbitmvi.orbit.viewmodel.orbitContainer
import javax.inject.Inject

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val direction: InsightsContract.Direction,
    private val insightsUseCase: InsightsUseCase
) : ViewModel(), InsightsContract.InsightsViewModel {

    override val container = orbitContainer<InsightsContract.InsightsUiState, InsightsContract.SideEffect>(InsightsContract.InsightsUiState())

    private var observeJob: Job? = null

    init {
        observe(InsightsContract.Period.WEEK)
    }

    override fun onEventDispatcher(event: InsightsContract.InsightsEvent) {
        when (event) {
            is InsightsContract.InsightsEvent.PeriodSelected -> intent {
                if (event.period == state.period) return@intent
                reduce { state.copy(period = event.period) }
                observe(event.period)
            }
        }
    }

    private fun observe(period: InsightsContract.Period) {
        observeJob?.cancel()
        observeJob = intent {
            insightsUseCase.observeInsights(period.days).collect { data ->
                reduce {
                    state.copy(
                        isLoading = false,
                        days = data.days,
                        averageMl = if (data.days.isEmpty()) 0 else data.days.sumOf { it.consumedMl } / data.days.size,
                        goalMl = data.days.lastOrNull { it.goalMl > 0 }?.goalMl ?: 0,
                        goalReachedDays = data.days.count { it.isCompleted },
                        bestStreak = data.stats.bestStreak,
                        drinksPerDay = if (data.days.isEmpty()) 0f else data.days.sumOf { it.drinkCount }.toFloat() / data.days.size,
                        achievements = data.achievements,
                        unit = data.unit
                    )
                }
            }
        }
    }
}
