package com.visionsystems.waterreminder.data.repository_impl

import com.visionsystems.waterreminder.data.mapper.toUIData
import com.visionsystems.waterreminder.data.source.local.room.dao.DrinkDao
import com.visionsystems.waterreminder.data.source.local.room.entity.DrinkEntryEntity
import com.visionsystems.waterreminder.domain.module.DailyProgressUiData
import com.visionsystems.waterreminder.domain.repository.CurrentUserProvider
import com.visionsystems.waterreminder.domain.repository.DrinkRepository
import com.visionsystems.waterreminder.domain.repository.GoalRepository
import com.visionsystems.waterreminder.domain.util.TimeProvider
import com.visionsystems.waterreminder.domain.util.toDayKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class DrinkRepositoryImpl @Inject constructor(
    private val drinkDao: DrinkDao,
    private val goalRepository: GoalRepository,
    private val currentUser: CurrentUserProvider,
    private val time: TimeProvider
) : DrinkRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeToday(): Flow<DailyProgressUiData> =
        combine(currentUser.currentUid, time.observeDayKey()) { uid, dayKey -> uid to dayKey }
            .flatMapLatest { (uid, dayKey) ->
                combine(drinkDao.observeDay(uid, dayKey), goalRepository.observeTodayGoal()) { entries, goal ->
                    DailyProgressUiData(
                        dayKey = dayKey,
                        goalMl = goal,
                        consumedMl = entries.sumOf { it.amountMl },
                        entries = entries.map { it.toUIData() }
                    )
                }
            }

    override suspend fun getToday(): DailyProgressUiData {
        val uid = currentUser.currentUid.value
        val dayKey = time.today().toDayKey()
        val entries = drinkDao.getDay(uid, dayKey)
        return DailyProgressUiData(
            dayKey = dayKey,
            goalMl = goalRepository.getTodayGoal(),
            consumedMl = entries.sumOf { it.amountMl },
            entries = entries.map { it.toUIData() }
        )
    }

    override suspend fun addDrink(amountMl: Int): Long {
        val millis = time.currentMillis()
        return drinkDao.insert(
            DrinkEntryEntity(
                uid = currentUser.currentUid.value,
                amountMl = amountMl.coerceAtLeast(1),
                timestamp = millis,
                dayKey = time.dayKeyOf(millis)
            )
        )
    }

    override suspend fun deleteDrink(entryId: Long) {
        drinkDao.delete(currentUser.currentUid.value, entryId)
    }
}
