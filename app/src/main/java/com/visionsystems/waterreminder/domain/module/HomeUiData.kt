package com.visionsystems.waterreminder.domain.module

import java.time.LocalDateTime
import java.time.LocalTime

data class HomeUiData(
    val userName: String,
    val progress: DailyProgressUiData,
    val streak: Int,
    val week: List<DayTotalUiData>,
    val cups: List<CupSizeUiData>,
    val nextReminder: LocalTime?,
    val unit: WaterUnit,
    val now: LocalDateTime
)
