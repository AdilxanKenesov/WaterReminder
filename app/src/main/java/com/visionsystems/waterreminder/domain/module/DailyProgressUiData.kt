package com.visionsystems.waterreminder.domain.module

data class DailyProgressUiData(
    val dayKey: Int,
    val goalMl: Int,
    val consumedMl: Int,
    val entries: List<DrinkEntryUiData> = emptyList()
) {
    val isCompleted: Boolean get() = goalMl > 0 && consumedMl >= goalMl
    val remainingMl: Int get() = (goalMl - consumedMl).coerceAtLeast(0)
    val progress: Float get() = if (goalMl <= 0) 0f else (consumedMl.toFloat() / goalMl).coerceIn(0f, 1f)
}
