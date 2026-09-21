package com.visionsystems.waterreminder.data.source.local.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.visionsystems.waterreminder.data.source.local.room.entity.ReminderConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminder_config WHERE uid = :uid")
    fun observe(uid: String): Flow<ReminderConfigEntity?>

    @Query("SELECT * FROM reminder_config WHERE uid = :uid")
    suspend fun get(uid: String): ReminderConfigEntity?

    @Upsert
    suspend fun upsert(config: ReminderConfigEntity)

    @Query("DELETE FROM reminder_config WHERE uid = :uid")
    suspend fun deleteAll(uid: String)
}
