package com.visionsystems.waterreminder.data.source.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "achievements",
    primaryKeys = ["uid", "key"]
)
data class AchievementEntity(
    @ColumnInfo(name = "uid") val uid: String,
    @ColumnInfo(name = "key") val key: String,
    @ColumnInfo(name = "unlocked_at") val unlockedAt: Long
)
