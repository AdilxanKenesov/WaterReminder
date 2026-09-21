package com.visionsystems.waterreminder.domain.module

data class WidgetUiData(
    val isReady: Boolean,
    val consumedMl: Int,
    val goalMl: Int,
    val cupMl: Int,
    val unit: WaterUnit
) {
    val progress: Float get() = if (goalMl <= 0) 0f else (consumedMl.toFloat() / goalMl).coerceIn(0f, 1f)
}
