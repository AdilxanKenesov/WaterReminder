package com.visionsystems.waterreminder.domain.usecase.splash

import com.visionsystems.waterreminder.domain.module.AppStart

interface SplashUseCase {
    suspend fun resolveStart(): AppStart
}
