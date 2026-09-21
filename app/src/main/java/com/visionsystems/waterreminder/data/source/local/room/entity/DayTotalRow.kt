package com.visionsystems.waterreminder.data.source.local.room.entity

data class DayTotalRow(
    val dayKey: Int,
    val goalMl: Int,
    val consumedMl: Int,
    val drinkCount: Int
)
