package com.visionsystems.waterreminder.domain.module

data class DayTotalUiData(
    val dayKey: Int,
    val goalMl: Int,
    val consumedMl: Int,
    val drinkCount: Int
) {
    val isCompleted: Boolean get() = goalMl > 0 && consumedMl >= goalMl
    val completion: Float get() = if (goalMl <= 0) 0f else (consumedMl.toFloat() / goalMl).coerceIn(0f, 1f)
}
