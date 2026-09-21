package com.visionsystems.waterreminder.domain.usecase.quickadd

import com.visionsystems.waterreminder.domain.module.AddWaterUiData
import com.visionsystems.waterreminder.domain.module.AppStart
import com.visionsystems.waterreminder.domain.usecase.addwater.AddWaterUseCase
import com.visionsystems.waterreminder.domain.usecase.common.StartResolver
import javax.inject.Inject

class QuickAddUseCaseImpl @Inject constructor(
    private val startResolver: StartResolver,
    private val addWaterUseCase: AddWaterUseCase
) : QuickAddUseCase {

    override suspend fun isReady(): Boolean = startResolver.resolve() == AppStart.MAIN

    override suspend fun loadDefaults(): AddWaterUiData = addWaterUseCase.loadDefaults()

    override suspend fun addDrink(amountMl: Int) {
        addWaterUseCase.addDrink(amountMl)
    }
}
