package com.visionsystems.waterreminder.data.source.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.visionsystems.waterreminder.data.source.local.room.entity.AchievementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {

    @Query("SELECT * FROM achievements WHERE uid = :uid")
    fun observe(uid: String): Flow<List<AchievementEntity>>

    @Query("SELECT `key` FROM achievements WHERE uid = :uid")
    suspend fun getKeys(uid: String): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(achievements: List<AchievementEntity>)

    @Query("DELETE FROM achievements WHERE uid = :uid")
    suspend fun deleteAll(uid: String)
}
