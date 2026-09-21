package com.visionsystems.waterreminder.data.source.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.visionsystems.waterreminder.data.source.local.room.entity.CupSizeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CupDao {

    @Query("SELECT * FROM cup_sizes WHERE uid = :uid ORDER BY amount_ml")
    fun observe(uid: String): Flow<List<CupSizeEntity>>

    @Query("SELECT COUNT(*) FROM cup_sizes WHERE uid = :uid")
    suspend fun count(uid: String): Int

    @Query("SELECT * FROM cup_sizes WHERE uid = :uid AND is_selected = 1 LIMIT 1")
    suspend fun getSelected(uid: String): CupSizeEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(cups: List<CupSizeEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(cup: CupSizeEntity): Long

    @Query("UPDATE cup_sizes SET is_selected = (id = :cupId) WHERE uid = :uid")
    suspend fun select(uid: String, cupId: Long)

    @Query("DELETE FROM cup_sizes WHERE uid = :uid AND id = :cupId AND is_selected = 0")
    suspend fun delete(uid: String, cupId: Long)

    @Query("DELETE FROM cup_sizes WHERE uid = :uid")
    suspend fun deleteAll(uid: String)
}
