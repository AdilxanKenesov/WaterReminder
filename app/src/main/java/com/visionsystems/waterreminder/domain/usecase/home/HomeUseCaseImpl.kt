package com.visionsystems.waterreminder.domain.usecase.home

import com.visionsystems.waterreminder.domain.module.CupSizeUiData
import com.visionsystems.waterreminder.domain.module.DailyProgressUiData
import com.visionsystems.waterreminder.domain.module.DayTotalUiData
import com.visionsystems.waterreminder.domain.module.DrinkResultUiData
import com.visionsystems.waterreminder.domain.module.HomeUiData
import com.visionsystems.waterreminder.domain.module.WaterStatsUiData
import com.visionsystems.waterreminder.domain.module.WaterUnit
import com.visionsystems.waterreminder.domain.repository.AuthRepository
import com.visionsystems.waterreminder.domain.repository.CupRepository
import com.visionsystems.waterreminder.domain.repository.DrinkRepository
import com.visionsystems.waterreminder.domain.repository.ProfileRepository
import com.visionsystems.waterreminder.domain.repository.ReminderRepository
import com.visionsystems.waterreminder.domain.repository.SettingsRepository
import com.visionsystems.waterreminder.domain.repository.StatsRepository
import com.visionsystems.waterreminder.domain.usecase.drink.DrinkUseCase
import com.visionsystems.waterreminder.domain.util.ReminderPreview
import com.visionsystems.waterreminder.domain.util.TimeProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class HomeUseCaseImpl @Inject constructor(
    private val drinkRepository: DrinkRepository,
    private val statsRepository: StatsRepository,
    private val cupRepository: CupRepository,
    private val profileRepository: ProfileRepository,
    private val reminderRepository: ReminderRepository,
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository,
    private val drinkUseCase: DrinkUseCase,
    private val time: TimeProvider
) : HomeUseCase {

    private data class Snapshot(
        val progress: DailyProgressUiData,
        val stats: WaterStatsUiData,
        val week: List<DayTotalUiData>,
        val cups: List<CupSizeUiData>,
        val unit: WaterUnit
    )

    override fun observeHome(): Flow<HomeUiData> {
        val snapshot = combine(
            drinkRepository.observeToday(),
            statsRepository.observeStats(),
            statsRepository.observeLastDays(WEEK_DAYS),
            cupRepository.observeCups(),
            settingsRepository.getUnit()
        ) { progress, stats, week, cups, unit -> Snapshot(progress, stats, week, cups, unit) }
        return combine(
            snapshot,
            profileRepository.observeProfile(),
            reminderRepository.observeConfig(),
            authRepository.currentUser,
            ReminderPreview.minuteTicker()
        ) { data, profile, config, user, _ ->
            val interval = ReminderPreview.interval(profile, data.progress.goalMl, ReminderPreview.selectedCupMl(data.cups), config)
            HomeUiData(
                userName = profile?.fullName?.takeIf { it.isNotBlank() } ?: user?.displayName.orEmpty(),
                progress = data.progress,
                streak = data.stats.currentStreak,
                week = data.week,
                cups = data.cups,
                nextReminder = ReminderPreview.next(time.now(), profile, interval, config, data.progress.isCompleted),
                unit = data.unit,
                now = time.now()
            )
        }
    }

    override suspend fun addDrink(amountMl: Int): DrinkResultUiData = drinkUseCase.addDrink(amountMl)

    override suspend fun undoDrink(entryId: Long) = drinkUseCase.undoDrink(entryId)

    private companion object {
        const val WEEK_DAYS = 7
    }
}
