package com.visionsystems.waterreminder.domain.util

import java.time.LocalDate
import java.time.LocalTime

fun LocalDate.toDayKey(): Int = year * 10_000 + monthValue * 100 + dayOfMonth

fun Int.toLocalDate(): LocalDate = LocalDate.of(this / 10_000, (this / 100) % 100, this % 100)

fun LocalTime.toMinuteOfDay(): Int = hour * 60 + minute

fun Int.toLocalTime(): LocalTime = LocalTime.of((this / 60) % 24, this % 60)
