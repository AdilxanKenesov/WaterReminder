package com.visionsystems.waterreminder.data.source.local.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.visionsystems.waterreminder.data.source.local.room.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {

    @Query("SELECT * FROM user_profile WHERE uid = :uid")
    fun observe(uid: String): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE uid = :uid")
    suspend fun get(uid: String): UserProfileEntity?

    @Upsert
    suspend fun upsert(profile: UserProfileEntity)

    @Query("DELETE FROM user_profile WHERE uid = :uid")
    suspend fun delete(uid: String)
}
