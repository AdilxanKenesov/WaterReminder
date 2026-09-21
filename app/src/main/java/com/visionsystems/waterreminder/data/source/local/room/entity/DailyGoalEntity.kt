package com.visionsystems.waterreminder.data.source.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "daily_goals",
    primaryKeys = ["uid", "day_key"]
)
data class DailyGoalEntity(
    @ColumnInfo(name = "uid") val uid: String,
    @ColumnInfo(name = "day_key") val dayKey: Int,
    @ColumnInfo(name = "goal_ml") val goalMl: Int
)
