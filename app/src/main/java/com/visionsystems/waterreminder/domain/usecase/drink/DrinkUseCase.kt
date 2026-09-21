package com.visionsystems.waterreminder.domain.usecase.drink

import com.visionsystems.waterreminder.domain.module.DrinkResultUiData

interface DrinkUseCase {
    suspend fun addDrink(amountMl: Int): DrinkResultUiData
    suspend fun addSelectedCup(): DrinkResultUiData
    suspend fun undoDrink(entryId: Long)
}
