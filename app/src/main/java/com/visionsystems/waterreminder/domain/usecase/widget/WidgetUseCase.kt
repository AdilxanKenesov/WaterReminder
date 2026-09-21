package com.visionsystems.waterreminder.domain.usecase.widget

import com.visionsystems.waterreminder.domain.module.WidgetUiData

interface WidgetUseCase {
    suspend fun load(): WidgetUiData
    suspend fun addDrink(amountMl: Int)
}
