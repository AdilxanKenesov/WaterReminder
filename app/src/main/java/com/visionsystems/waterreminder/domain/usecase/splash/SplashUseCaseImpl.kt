package com.visionsystems.waterreminder.domain.usecase.splash

import com.visionsystems.waterreminder.domain.module.AppStart
import com.visionsystems.waterreminder.domain.usecase.common.StartResolver
import javax.inject.Inject

class SplashUseCaseImpl @Inject constructor(
    private val startResolver: StartResolver
) : SplashUseCase {

    override suspend fun resolveStart(): AppStart = startResolver.resolve()
}
