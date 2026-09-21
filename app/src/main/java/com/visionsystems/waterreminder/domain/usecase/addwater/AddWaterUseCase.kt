package com.visionsystems.waterreminder.domain.usecase.addwater

import com.visionsystems.waterreminder.domain.module.AddWaterUiData
import com.visionsystems.waterreminder.domain.module.DrinkResultUiData

interface AddWaterUseCase {
    suspend fun loadDefaults(): AddWaterUiData
    suspend fun addDrink(amountMl: Int): DrinkResultUiData
}
