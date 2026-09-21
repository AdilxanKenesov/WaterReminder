package com.visionsystems.waterreminder.data.repository_impl

import com.visionsystems.waterreminder.data.mapper.toUIData
import com.visionsystems.waterreminder.data.source.local.room.dao.AchievementDao
import com.visionsystems.waterreminder.data.source.local.room.dao.DrinkDao
import com.visionsystems.waterreminder.data.source.local.room.dao.GoalDao
import com.visionsystems.waterreminder.data.source.local.room.entity.AchievementEntity
import com.visionsystems.waterreminder.domain.module.AchievementType
import com.visionsystems.waterreminder.domain.module.AchievementUiData
import com.visionsystems.waterreminder.domain.repository.AchievementRepository
import com.visionsystems.waterreminder.domain.repository.CurrentUserProvider
import com.visionsystems.waterreminder.domain.util.AchievementRules
import com.visionsystems.waterreminder.domain.util.StreakCalculator
import com.visionsystems.waterreminder.domain.util.TimeProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AchievementRepositoryImpl @Inject constructor(
    private val achievementDao: AchievementDao,
    private val drinkDao: DrinkDao,
    private val goalDao: GoalDao,
    private val currentUser: CurrentUserProvider,
    private val time: TimeProvider
) : AchievementRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeAchievements(): Flow<List<AchievementUiData>> =
        currentUser.currentUid.flatMapLatest { uid -> achievementDao.observe(uid) }.map { it.toUIData() }

    override suspend fun evaluate(): List<AchievementType> {
        val uid = currentUser.currentUid.value
        val days = goalDao.getAllTotals(uid).map { it.toUIData() }
        val unlocked = AchievementRules.unlocked(
            totalMl = drinkDao.totalMl(uid),
            entryCount = drinkDao.count(uid),
            completedDays = days.count { it.isCompleted },
            bestStreak = StreakCalculator.best(days)
        )
        val existing = achievementDao.getKeys(uid).toSet()
        val fresh = unlocked.filter { it.key !in existing }
        if (fresh.isNotEmpty()) {
            val now = time.currentMillis()
            achievementDao.insertAll(fresh.map { AchievementEntity(uid = uid, key = it.key, unlockedAt = now) })
        }
        return fresh
    }
}
