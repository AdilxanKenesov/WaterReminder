package com.visionsystems.waterreminder.di

import com.visionsystems.waterreminder.data.repository_impl.AchievementRepositoryImpl
import com.visionsystems.waterreminder.data.repository_impl.AuthRepositoryImpl
import com.visionsystems.waterreminder.data.repository_impl.CupRepositoryImpl
import com.visionsystems.waterreminder.data.repository_impl.CurrentUserProviderImpl
import com.visionsystems.waterreminder.data.repository_impl.DrinkRepositoryImpl
import com.visionsystems.waterreminder.data.repository_impl.GoalRepositoryImpl
import com.visionsystems.waterreminder.data.repository_impl.NetworkRepositoryImpl
import com.visionsystems.waterreminder.data.repository_impl.NotificationRepositoryImpl
import com.visionsystems.waterreminder.data.repository_impl.ProfileRepositoryImpl
import com.visionsystems.waterreminder.data.repository_impl.ReminderRepositoryImpl
import com.visionsystems.waterreminder.data.repository_impl.SettingsRepositoryImpl
import com.visionsystems.waterreminder.data.repository_impl.StatsRepositoryImpl
import com.visionsystems.waterreminder.data.repository_impl.WidgetRepositoryImpl
import com.visionsystems.waterreminder.data.util.SystemTimeProvider
import com.visionsystems.waterreminder.data.worker.ReminderSchedulerImpl
import com.visionsystems.waterreminder.domain.repository.AchievementRepository
import com.visionsystems.waterreminder.domain.repository.AuthRepository
import com.visionsystems.waterreminder.domain.repository.CupRepository
import com.visionsystems.waterreminder.domain.repository.CurrentUserProvider
import com.visionsystems.waterreminder.domain.repository.DrinkRepository
import com.visionsystems.waterreminder.domain.repository.GoalRepository
import com.visionsystems.waterreminder.domain.repository.NetworkRepository
import com.visionsystems.waterreminder.domain.repository.NotificationRepository
import com.visionsystems.waterreminder.domain.repository.ProfileRepository
import com.visionsystems.waterreminder.domain.repository.ReminderRepository
import com.visionsystems.waterreminder.domain.repository.ReminderScheduler
import com.visionsystems.waterreminder.domain.repository.SettingsRepository
import com.visionsystems.waterreminder.domain.repository.StatsRepository
import com.visionsystems.waterreminder.domain.repository.WidgetRepository
import com.visionsystems.waterreminder.domain.util.TimeProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    fun bindCurrentUserProvider(impl: CurrentUserProviderImpl): CurrentUserProvider

    @Binds
    @Singleton
    fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

    @Binds
    @Singleton
    fun bindDrinkRepository(impl: DrinkRepositoryImpl): DrinkRepository

    @Binds
    @Singleton
    fun bindGoalRepository(impl: GoalRepositoryImpl): GoalRepository

    @Binds
    @Singleton
    fun bindStatsRepository(impl: StatsRepositoryImpl): StatsRepository

    @Binds
    @Singleton
    fun bindAchievementRepository(impl: AchievementRepositoryImpl): AchievementRepository

    @Binds
    @Singleton
    fun bindCupRepository(impl: CupRepositoryImpl): CupRepository

    @Binds
    @Singleton
    fun bindReminderRepository(impl: ReminderRepositoryImpl): ReminderRepository

    @Binds
    @Singleton
    fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    fun bindNotificationRepository(impl: NotificationRepositoryImpl): NotificationRepository

    @Binds
    @Singleton
    fun bindReminderScheduler(impl: ReminderSchedulerImpl): ReminderScheduler

    @Binds
    @Singleton
    fun bindNetworkRepository(impl: NetworkRepositoryImpl): NetworkRepository

    @Binds
    @Singleton
    fun bindWidgetRepository(impl: WidgetRepositoryImpl): WidgetRepository

    @Binds
    @Singleton
    fun bindTimeProvider(impl: SystemTimeProvider): TimeProvider
}
