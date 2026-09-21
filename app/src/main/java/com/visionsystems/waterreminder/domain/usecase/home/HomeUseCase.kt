package com.visionsystems.waterreminder.domain.usecase.home

import com.visionsystems.waterreminder.domain.module.DrinkResultUiData
import com.visionsystems.waterreminder.domain.module.HomeUiData
import kotlinx.coroutines.flow.Flow

interface HomeUseCase {
    fun observeHome(): Flow<HomeUiData>
    suspend fun addDrink(amountMl: Int): DrinkResultUiData
    suspend fun undoDrink(entryId: Long)
}
