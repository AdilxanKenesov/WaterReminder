package com.visionsystems.waterreminder.data.repository_impl

import com.visionsystems.waterreminder.data.mapper.toUIData
import com.visionsystems.waterreminder.data.source.local.room.dao.CupDao
import com.visionsystems.waterreminder.data.source.local.room.dao.GoalDao
import com.visionsystems.waterreminder.data.source.local.room.dao.ProfileDao
import com.visionsystems.waterreminder.data.source.local.room.entity.DailyGoalEntity
import com.visionsystems.waterreminder.domain.module.GoalTemplate
import com.visionsystems.waterreminder.domain.repository.CurrentUserProvider
import com.visionsystems.waterreminder.domain.repository.GoalRepository
import com.visionsystems.waterreminder.domain.util.GoalCalculator
import com.visionsystems.waterreminder.domain.util.TimeProvider
import com.visionsystems.waterreminder.domain.util.toDayKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GoalRepositoryImpl @Inject constructor(
    private val goalDao: GoalDao,
    private val profileDao: ProfileDao,
    private val cupDao: CupDao,
    private val currentUser: CurrentUserProvider,
    private val time: TimeProvider
) : GoalRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeTodayGoal(): Flow<Int> =
        combine(currentUser.currentUid, time.observeDayKey()) { uid, dayKey -> uid to dayKey }
            .flatMapLatest { (uid, dayKey) ->
                goalDao.observeGoal(uid, dayKey).map { it ?: fallbackGoal(uid, dayKey) }
            }

    override suspend fun getTodayGoal(): Int {
        val uid = currentUser.currentUid.value
        val dayKey = time.today().toDayKey()
        return goalDao.getGoal(uid, dayKey) ?: fallbackGoal(uid, dayKey)
    }

    override suspend fun setTodayGoal(goalMl: Int) {
        goalDao.upsert(
            DailyGoalEntity(
                uid = currentUser.currentUid.value,
                dayKey = time.today().toDayKey(),
                goalMl = goalMl.coerceIn(GoalCalculator.MIN_GOAL_ML, GoalCalculator.MAX_GOAL_ML)
            )
        )
    }

    override suspend fun applyTemplate(template: GoalTemplate) {
        val cupMl = cupDao.getSelected(currentUser.currentUid.value)?.amountMl ?: CupRepositoryImpl.DEFAULT_CUP_ML
        setTodayGoal(GoalCalculator.fromTemplate(template, cupMl))
    }

    override suspend fun recommendedGoal(): Int {
        val profile = profileDao.get(currentUser.currentUid.value)?.toUIData() ?: return GoalCalculator.DEFAULT_GOAL_ML
        return GoalCalculator.recommended(profile.weightKg, profile.gender, profile.activityLevel, profile.age)
    }

    override suspend fun ensureGoalForToday(): Int {
        val uid = currentUser.currentUid.value
        val dayKey = time.today().toDayKey()
        goalDao.getGoal(uid, dayKey)?.let { return it }
        val goal = fallbackGoal(uid, dayKey)
        goalDao.upsert(DailyGoalEntity(uid = uid, dayKey = dayKey, goalMl = goal))
        return goal
    }

    private suspend fun fallbackGoal(uid: String, dayKey: Int): Int =
        goalDao.latestGoalOnOrBefore(uid, dayKey) ?: recommendedGoal()
}
