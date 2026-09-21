package com.visionsystems.waterreminder.domain.usecase.quickadd

import com.visionsystems.waterreminder.domain.module.AddWaterUiData

interface QuickAddUseCase {
    suspend fun isReady(): Boolean
    suspend fun loadDefaults(): AddWaterUiData
    suspend fun addDrink(amountMl: Int)
}
