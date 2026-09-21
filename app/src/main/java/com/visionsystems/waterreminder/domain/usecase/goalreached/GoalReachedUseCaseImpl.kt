package com.visionsystems.waterreminder.domain.usecase.goalreached

import com.visionsystems.waterreminder.domain.module.GoalReachedUiData
import com.visionsystems.waterreminder.domain.repository.DrinkRepository
import com.visionsystems.waterreminder.domain.repository.SettingsRepository
import com.visionsystems.waterreminder.domain.repository.StatsRepository
import javax.inject.Inject

class GoalReachedUseCaseImpl @Inject constructor(
    private val drinkRepository: DrinkRepository,
    private val statsRepository: StatsRepository,
    private val settingsRepository: SettingsRepository
) : GoalReachedUseCase {

    override suspend fun load(): GoalReachedUiData = GoalReachedUiData(
        consumedMl = drinkRepository.getToday().consumedMl,
        streak = statsRepository.getCurrentStreak(),
        unit = settingsRepository.getUnit().value
    )
}
