package com.visionsystems.waterreminder.domain.util

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

interface TimeProvider {
    fun now(): LocalDateTime
    fun today(): LocalDate
    fun currentMillis(): Long
    fun dayKeyOf(millis: Long): Int
    fun millisOf(dateTime: LocalDateTime): Long
    fun observeDayKey(): Flow<Int>
}
