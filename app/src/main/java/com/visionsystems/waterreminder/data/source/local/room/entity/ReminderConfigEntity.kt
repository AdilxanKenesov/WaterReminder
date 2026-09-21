package com.visionsystems.waterreminder.data.source.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminder_config")
data class ReminderConfigEntity(
    @PrimaryKey
    @ColumnInfo(name = "uid") val uid: String,
    @ColumnInfo(name = "enabled") val enabled: Boolean,
    @ColumnInfo(name = "interval_minutes") val intervalMinutes: Int?,
    @ColumnInfo(name = "sound_enabled") val soundEnabled: Boolean,
    @ColumnInfo(name = "goal_notify") val goalNotify: Boolean,
    @ColumnInfo(name = "streak_notify") val streakNotify: Boolean,
    @ColumnInfo(name = "achievement_notify") val achievementNotify: Boolean
)
