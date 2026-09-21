package com.visionsystems.waterreminder.data.source.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.visionsystems.waterreminder.data.source.local.room.entity.DrinkEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DrinkDao {

    @Insert
    suspend fun insert(entry: DrinkEntryEntity): Long

    @Query("DELETE FROM drink_entries WHERE uid = :uid AND id = :entryId")
    suspend fun delete(uid: String, entryId: Long)

    @Query("SELECT * FROM drink_entries WHERE uid = :uid AND day_key = :dayKey ORDER BY timestamp DESC")
    fun observeDay(uid: String, dayKey: Int): Flow<List<DrinkEntryEntity>>

    @Query("SELECT * FROM drink_entries WHERE uid = :uid AND day_key = :dayKey ORDER BY timestamp DESC")
    suspend fun getDay(uid: String, dayKey: Int): List<DrinkEntryEntity>

    @Query("SELECT COALESCE(SUM(amount_ml), 0) FROM drink_entries WHERE uid = :uid")
    suspend fun totalMl(uid: String): Long

    @Query("SELECT COUNT(*) FROM drink_entries WHERE uid = :uid")
    suspend fun count(uid: String): Int

    @Query("DELETE FROM drink_entries WHERE uid = :uid")
    suspend fun deleteAll(uid: String)
}
