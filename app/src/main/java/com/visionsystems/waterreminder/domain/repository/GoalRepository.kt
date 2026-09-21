package com.visionsystems.waterreminder.domain.repository

import com.visionsystems.waterreminder.domain.module.GoalTemplate
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    fun observeTodayGoal(): Flow<Int>
    suspend fun getTodayGoal(): Int
    suspend fun setTodayGoal(goalMl: Int)
    suspend fun applyTemplate(template: GoalTemplate)
    suspend fun recommendedGoal(): Int
    suspend fun ensureGoalForToday(): Int
}
