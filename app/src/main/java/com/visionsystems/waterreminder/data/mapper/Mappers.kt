package com.visionsystems.waterreminder.data.mapper

import com.visionsystems.waterreminder.data.source.local.room.entity.AchievementEntity
import com.visionsystems.waterreminder.data.source.local.room.entity.CupSizeEntity
import com.visionsystems.waterreminder.data.source.local.room.entity.DayTotalRow
import com.visionsystems.waterreminder.data.source.local.room.entity.DrinkEntryEntity
import com.visionsystems.waterreminder.data.source.local.room.entity.ReminderConfigEntity
import com.visionsystems.waterreminder.data.source.local.room.entity.UserProfileEntity
import com.visionsystems.waterreminder.domain.module.AchievementType
import com.visionsystems.waterreminder.domain.module.AchievementUiData
import com.visionsystems.waterreminder.domain.module.ActivityLevel
import com.visionsystems.waterreminder.domain.module.CupSizeUiData
import com.visionsystems.waterreminder.domain.module.DayTotalUiData
import com.visionsystems.waterreminder.domain.module.DrinkEntryUiData
import com.visionsystems.waterreminder.domain.module.Gender
import com.visionsystems.waterreminder.domain.module.ReminderConfigUiData
import com.visionsystems.waterreminder.domain.module.UserProfileUiData
import com.visionsystems.waterreminder.domain.util.toLocalTime
import com.visionsystems.waterreminder.domain.util.toMinuteOfDay

fun UserProfileEntity.toUIData(): UserProfileUiData =
    UserProfileUiData(
        uid = uid,
        fullName = fullName,
        email = email,
        gender = Gender.entries.firstOrNull { it.name == gender } ?: Gender.OTHER,
        age = age,
        weightKg = weightKg,
        heightCm = heightCm,
        wakeTime = wakeMinutes.toLocalTime(),
        sleepTime = sleepMinutes.toLocalTime(),
        activityLevel = ActivityLevel.entries.firstOrNull { it.name == activityLevel } ?: ActivityLevel.MODERATE,
        photoPath = photoPath
    )

fun UserProfileUiData.toEntity(uid: String, createdAt: Long, updatedAt: Long): UserProfileEntity =
    UserProfileEntity(
        uid = uid,
        fullName = fullName,
        email = email,
        gender = gender.name,
        age = age,
        weightKg = weightKg,
        heightCm = heightCm,
        wakeMinutes = wakeTime.toMinuteOfDay(),
        sleepMinutes = sleepTime.toMinuteOfDay(),
        activityLevel = activityLevel.name,
        createdAt = createdAt,
        updatedAt = updatedAt,
        photoPath = photoPath
    )

fun DrinkEntryEntity.toUIData(): DrinkEntryUiData =
    DrinkEntryUiData(id = id, amountMl = amountMl, timestamp = timestamp, dayKey = dayKey)

fun DayTotalRow.toUIData(): DayTotalUiData =
    DayTotalUiData(dayKey = dayKey, goalMl = goalMl, consumedMl = consumedMl, drinkCount = drinkCount)

fun CupSizeEntity.toUIData(): CupSizeUiData =
    CupSizeUiData(id = id, amountMl = amountMl, isSelected = isSelected)

fun ReminderConfigEntity.toUIData(): ReminderConfigUiData =
    ReminderConfigUiData(
        enabled = enabled,
        intervalMinutes = intervalMinutes,
        soundEnabled = soundEnabled,
        goalNotify = goalNotify,
        streakNotify = streakNotify,
        achievementNotify = achievementNotify
    )

fun ReminderConfigUiData.toEntity(uid: String): ReminderConfigEntity =
    ReminderConfigEntity(
        uid = uid,
        enabled = enabled,
        intervalMinutes = intervalMinutes,
        soundEnabled = soundEnabled,
        goalNotify = goalNotify,
        streakNotify = streakNotify,
        achievementNotify = achievementNotify
    )

fun List<AchievementEntity>.toUIData(): List<AchievementUiData> {
    val unlocked = associate { it.key to it.unlockedAt }
    return AchievementType.entries.map { type -> AchievementUiData(type, unlocked[type.key]) }
}
