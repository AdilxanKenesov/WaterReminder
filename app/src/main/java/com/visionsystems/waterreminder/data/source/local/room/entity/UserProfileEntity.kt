package com.visionsystems.waterreminder.data.source.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    @ColumnInfo(name = "uid") val uid: String,
    @ColumnInfo(name = "full_name") val fullName: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "gender") val gender: String,
    @ColumnInfo(name = "age") val age: Int,
    @ColumnInfo(name = "weight_kg") val weightKg: Int,
    @ColumnInfo(name = "height_cm") val heightCm: Int,
    @ColumnInfo(name = "wake_minutes") val wakeMinutes: Int,
    @ColumnInfo(name = "sleep_minutes") val sleepMinutes: Int,
    @ColumnInfo(name = "activity_level") val activityLevel: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
    @ColumnInfo(name = "photo_path") val photoPath: String? = null
)
