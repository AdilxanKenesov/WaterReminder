package com.visionsystems.waterreminder.domain.usecase.insights

import com.visionsystems.waterreminder.domain.module.InsightsUiData
import com.visionsystems.waterreminder.domain.repository.AchievementRepository
import com.visionsystems.waterreminder.domain.repository.SettingsRepository
import com.visionsystems.waterreminder.domain.repository.StatsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class InsightsUseCaseImpl @Inject constructor(
    private val statsRepository: StatsRepository,
    private val achievementRepository: AchievementRepository,
    private val settingsRepository: SettingsRepository
) : InsightsUseCase {

    override fun observeInsights(days: Int): Flow<InsightsUiData> =
        combine(
            statsRepository.observeLastDays(days),
            statsRepository.observeStats(),
            achievementRepository.observeAchievements(),
            settingsRepository.getUnit()
        ) { totals, stats, achievements, unit ->
            InsightsUiData(days = totals, stats = stats, achievements = achievements, unit = unit)
        }
}
