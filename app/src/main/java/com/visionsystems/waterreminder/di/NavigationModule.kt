package com.visionsystems.waterreminder.di

import com.visionsystems.waterreminder.navigation.AppNavigationDispatcher
import com.visionsystems.waterreminder.navigation.AppNavigationHandler
import com.visionsystems.waterreminder.navigation.AppNavigator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface NavigationModule {

    @Binds
    fun bindAppNavigator(impl: AppNavigationDispatcher): AppNavigator

    @Binds
    fun bindAppNavigationHandler(impl: AppNavigationDispatcher): AppNavigationHandler
}
