package com.visionsystems.waterreminder.domain.module

data class DrinkEntryUiData(
    val id: Long,
    val amountMl: Int,
    val timestamp: Long,
    val dayKey: Int
)
