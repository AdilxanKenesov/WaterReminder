package com.visionsystems.waterreminder.di

import android.content.Context
import androidx.room.Room
import com.visionsystems.waterreminder.data.source.local.room.AppDatabase
import com.visionsystems.waterreminder.data.source.local.room.dao.AchievementDao
import com.visionsystems.waterreminder.data.source.local.room.dao.CupDao
import com.visionsystems.waterreminder.data.source.local.room.dao.DrinkDao
import com.visionsystems.waterreminder.data.source.local.room.dao.GoalDao
import com.visionsystems.waterreminder.data.source.local.room.dao.ProfileDao
import com.visionsystems.waterreminder.data.source.local.room.dao.ReminderDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.NAME).build()

    @Provides
    fun provideProfileDao(database: AppDatabase): ProfileDao = database.profileDao()

    @Provides
    fun provideDrinkDao(database: AppDatabase): DrinkDao = database.drinkDao()

    @Provides
    fun provideGoalDao(database: AppDatabase): GoalDao = database.goalDao()

    @Provides
    fun provideCupDao(database: AppDatabase): CupDao = database.cupDao()

    @Provides
    fun provideReminderDao(database: AppDatabase): ReminderDao = database.reminderDao()

    @Provides
    fun provideAchievementDao(database: AppDatabase): AchievementDao = database.achievementDao()
}
