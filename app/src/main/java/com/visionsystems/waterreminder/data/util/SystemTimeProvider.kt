package com.visionsystems.waterreminder.data.util

import com.visionsystems.waterreminder.domain.util.TimeProvider
import com.visionsystems.waterreminder.domain.util.toDayKey
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

class SystemTimeProvider @Inject constructor() : TimeProvider {

    override fun now(): LocalDateTime = LocalDateTime.now(ZoneId.systemDefault())

    override fun today(): LocalDate = LocalDate.now(ZoneId.systemDefault())

    override fun currentMillis(): Long = System.currentTimeMillis()

    override fun dayKeyOf(millis: Long): Int =
        Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate().toDayKey()

    override fun millisOf(dateTime: LocalDateTime): Long =
        dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    override fun observeDayKey(): Flow<Int> = flow {
        while (true) {
            emit(today().toDayKey())
            val nextMidnight = today().plusDays(1).atStartOfDay()
            delay((millisOf(nextMidnight) - currentMillis()).coerceAtLeast(MIN_DELAY_MILLIS) + MIDNIGHT_MARGIN_MILLIS)
        }
    }.distinctUntilChanged()

    private companion object {
        const val MIN_DELAY_MILLIS = 1_000L
        const val MIDNIGHT_MARGIN_MILLIS = 500L
    }
}
