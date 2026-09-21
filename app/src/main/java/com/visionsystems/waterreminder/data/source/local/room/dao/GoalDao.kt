package com.visionsystems.waterreminder.data.source.local.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.visionsystems.waterreminder.data.source.local.room.entity.DailyGoalEntity
import com.visionsystems.waterreminder.data.source.local.room.entity.DayTotalRow
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Query("SELECT goal_ml FROM daily_goals WHERE uid = :uid AND day_key = :dayKey")
    fun observeGoal(uid: String, dayKey: Int): Flow<Int?>

    @Query("SELECT goal_ml FROM daily_goals WHERE uid = :uid AND day_key = :dayKey")
    suspend fun getGoal(uid: String, dayKey: Int): Int?

    @Query("SELECT goal_ml FROM daily_goals WHERE uid = :uid AND day_key <= :dayKey ORDER BY day_key DESC LIMIT 1")
    suspend fun latestGoalOnOrBefore(uid: String, dayKey: Int): Int?

    @Upsert
    suspend fun upsert(goal: DailyGoalEntity)

    @Query(
        """
        SELECT g.day_key AS dayKey, g.goal_ml AS goalMl,
               COALESCE(SUM(d.amount_ml), 0) AS consumedMl, COUNT(d.id) AS drinkCount
        FROM daily_goals g
        LEFT JOIN drink_entries d ON d.uid = g.uid AND d.day_key = g.day_key
        WHERE g.uid = :uid AND g.day_key BETWEEN :fromDayKey AND :toDayKey
        GROUP BY g.day_key
        ORDER BY g.day_key
        """
    )
    fun observeTotals(uid: String, fromDayKey: Int, toDayKey: Int): Flow<List<DayTotalRow>>

    @Query(
        """
        SELECT g.day_key AS dayKey, g.goal_ml AS goalMl,
               COALESCE(SUM(d.amount_ml), 0) AS consumedMl, COUNT(d.id) AS drinkCount
        FROM daily_goals g
        LEFT JOIN drink_entries d ON d.uid = g.uid AND d.day_key = g.day_key
        WHERE g.uid = :uid
        GROUP BY g.day_key
        ORDER BY g.day_key
        """
    )
    fun observeAllTotals(uid: String): Flow<List<DayTotalRow>>

    @Query(
        """
        SELECT g.day_key AS dayKey, g.goal_ml AS goalMl,
               COALESCE(SUM(d.amount_ml), 0) AS consumedMl, COUNT(d.id) AS drinkCount
        FROM daily_goals g
        LEFT JOIN drink_entries d ON d.uid = g.uid AND d.day_key = g.day_key
        WHERE g.uid = :uid
        GROUP BY g.day_key
        ORDER BY g.day_key
        """
    )
    suspend fun getAllTotals(uid: String): List<DayTotalRow>

    @Query("DELETE FROM daily_goals WHERE uid = :uid")
    suspend fun deleteAll(uid: String)
}
