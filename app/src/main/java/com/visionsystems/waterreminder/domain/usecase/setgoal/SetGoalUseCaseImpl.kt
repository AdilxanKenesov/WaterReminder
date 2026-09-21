package com.visionsystems.waterreminder.domain.usecase.setgoal

import com.visionsystems.waterreminder.domain.module.SetGoalUiData
import com.visionsystems.waterreminder.domain.repository.CupRepository
import com.visionsystems.waterreminder.domain.repository.GoalRepository
import com.visionsystems.waterreminder.domain.repository.ReminderScheduler
import com.visionsystems.waterreminder.domain.repository.SettingsRepository
import com.visionsystems.waterreminder.domain.repository.WidgetRepository
import com.visionsystems.waterreminder.domain.util.GoalCalculator
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SetGoalUseCaseImpl @Inject constructor(
    private val goalRepository: GoalRepository,
    private val cupRepository: CupRepository,
    private val settingsRepository: SettingsRepository,
    private val reminderScheduler: ReminderScheduler,
    private val widgetRepository: WidgetRepository
) : SetGoalUseCase {

    override val goalRange: IntRange = GoalCalculator.MIN_GOAL_ML..GoalCalculator.MAX_GOAL_ML

    override suspend fun load(): SetGoalUiData = SetGoalUiData(
        goalMl = goalRepository.getTodayGoal(),
        recommendedMl = goalRepository.recommendedGoal(),
        cups = cupRepository.observeCups().first(),
        unit = settingsRepository.getUnit().value
    )

    override suspend fun save(goalMl: Int, cupId: Long?) {
        if (cupId != null) cupRepository.selectCup(cupId)
        goalRepository.setTodayGoal(goalMl)
        reminderScheduler.rescheduleAll()
        widgetRepository.refresh()
    }
}
