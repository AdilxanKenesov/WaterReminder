package com.visionsystems.waterreminder.data.repository_impl

import com.visionsystems.waterreminder.data.mapper.toUIData
import com.visionsystems.waterreminder.data.source.local.room.dao.GoalDao
import com.visionsystems.waterreminder.domain.module.DayTotalUiData
import com.visionsystems.waterreminder.domain.module.WaterStatsUiData
import com.visionsystems.waterreminder.domain.repository.CurrentUserProvider
import com.visionsystems.waterreminder.domain.repository.StatsRepository
import com.visionsystems.waterreminder.domain.util.StreakCalculator
import com.visionsystems.waterreminder.domain.util.TimeProvider
import com.visionsystems.waterreminder.domain.util.toDayKey
import com.visionsystems.waterreminder.domain.util.toLocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.roundToInt

class StatsRepositoryImpl @Inject constructor(
    private val goalDao: GoalDao,
    private val currentUser: CurrentUserProvider,
    private val time: TimeProvider
) : StatsRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeLastDays(days: Int): Flow<List<DayTotalUiData>> =
        combine(currentUser.currentUid, time.observeDayKey()) { uid, dayKey -> uid to dayKey }
            .flatMapLatest { (uid, dayKey) ->
                val today = dayKey.toLocalDate()
                val from = today.minusDays((days - 1).toLong())
                goalDao.observeTotals(uid, from.toDayKey(), dayKey).map { rows ->
                    fillDays(rows.map { it.toUIData() }, from, today)
                }
            }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeStats(): Flow<WaterStatsUiData> =
        combine(currentUser.currentUid, time.observeDayKey()) { uid, dayKey -> uid to dayKey }
            .flatMapLatest { (uid, dayKey) ->
                goalDao.observeAllTotals(uid).map { rows -> computeStats(rows.map { it.toUIData() }, dayKey.toLocalDate()) }
            }

    override suspend fun getCurrentStreak(): Int =
        StreakCalculator.current(
            goalDao.getAllTotals(currentUser.currentUid.value).map { it.toUIData() },
            time.today()
        )

    private fun computeStats(days: List<DayTotalUiData>, today: LocalDate): WaterStatsUiData {
        val week = fillDays(days, today.minusDays(WEEK_DAYS - 1L), today)
        val month = fillDays(days, today.minusDays(MONTH_DAYS - 1L), today)
        val trackedMonth = month.filter { it.goalMl > 0 }
        return WaterStatsUiData(
            weeklyAverageMl = week.sumOf { it.consumedMl } / WEEK_DAYS,
            monthlyAverageMl = month.sumOf { it.consumedMl } / MONTH_DAYS,
            averageCompletionPercent = if (trackedMonth.isEmpty()) 0 else
                (trackedMonth.map { it.completion }.average() * PERCENT).roundToInt(),
            drinksPerDay = week.sumOf { it.drinkCount }.toFloat() / WEEK_DAYS,
            currentStreak = StreakCalculator.current(days, today),
            bestStreak = StreakCalculator.best(days),
            totalMl = days.sumOf { it.consumedMl.toLong() }
        )
    }

    private fun fillDays(days: List<DayTotalUiData>, from: LocalDate, to: LocalDate): List<DayTotalUiData> {
        val byKey = days.associateBy { it.dayKey }
        return generateSequence(from) { it.plusDays(1) }
            .takeWhile { !it.isAfter(to) }
            .map { date ->
                val key = date.toDayKey()
                byKey[key] ?: DayTotalUiData(dayKey = key, goalMl = 0, consumedMl = 0, drinkCount = 0)
            }
            .toList()
    }

    private companion object {
        const val WEEK_DAYS = 7
        const val MONTH_DAYS = 30
        const val PERCENT = 100
    }
}
