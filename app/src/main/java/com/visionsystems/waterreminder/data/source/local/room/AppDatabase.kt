package com.visionsystems.waterreminder.data.source.local.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.visionsystems.waterreminder.data.source.local.room.dao.AchievementDao
import com.visionsystems.waterreminder.data.source.local.room.dao.CupDao
import com.visionsystems.waterreminder.data.source.local.room.dao.DrinkDao
import com.visionsystems.waterreminder.data.source.local.room.dao.GoalDao
import com.visionsystems.waterreminder.data.source.local.room.dao.ProfileDao
import com.visionsystems.waterreminder.data.source.local.room.dao.ReminderDao
import com.visionsystems.waterreminder.data.source.local.room.entity.AchievementEntity
import com.visionsystems.waterreminder.data.source.local.room.entity.CupSizeEntity
import com.visionsystems.waterreminder.data.source.local.room.entity.DailyGoalEntity
import com.visionsystems.waterreminder.data.source.local.room.entity.DrinkEntryEntity
import com.visionsystems.waterreminder.data.source.local.room.entity.ReminderConfigEntity
import com.visionsystems.waterreminder.data.source.local.room.entity.UserProfileEntity

@Database(
    entities = [
        UserProfileEntity::class,
        DrinkEntryEntity::class,
        DailyGoalEntity::class,
        CupSizeEntity::class,
        ReminderConfigEntity::class,
        AchievementEntity::class
    ],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun drinkDao(): DrinkDao
    abstract fun goalDao(): GoalDao
    abstract fun cupDao(): CupDao
    abstract fun reminderDao(): ReminderDao
    abstract fun achievementDao(): AchievementDao

    companion object {
        const val NAME = "hydro.db"
    }
}
