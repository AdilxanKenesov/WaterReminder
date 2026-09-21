package com.visionsystems.waterreminder.presenter.ui.util

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.visionsystems.waterreminder.R
import com.visionsystems.waterreminder.domain.module.AchievementType
import com.visionsystems.waterreminder.domain.module.ActivityLevel
import com.visionsystems.waterreminder.domain.module.Gender
import com.visionsystems.waterreminder.domain.module.WaterUnit
import com.visionsystems.waterreminder.domain.util.toLocalDate
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private const val ML_PER_OZ = 29.5735

private val HourMinute = DateTimeFormatter.ofPattern("HH:mm", Locale.ROOT)

fun Int.withThousands(): String = NumberFormat.getIntegerInstance().format(this)

fun Int.toVolume(unit: WaterUnit): String = when (unit) {
    WaterUnit.ML -> withThousands()
    WaterUnit.OZ -> String.format(Locale.getDefault(), "%.1f", this / ML_PER_OZ)
}

fun Int.toVolumeText(unit: WaterUnit): String = "${toVolume(unit)} ${unit.label}"

val WaterUnit.label: String
    get() = when (this) {
        WaterUnit.ML -> "ml"
        WaterUnit.OZ -> "oz"
    }

fun LocalTime.toHm(): String = format(HourMinute)

fun Long.toClock(): String = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalTime().toHm()

@Composable
@ReadOnlyComposable
fun currentLocale(): Locale = LocalConfiguration.current.locales[0]

@Composable
@ReadOnlyComposable
fun LocalDate.toDayHeader(): String {
    val locale = currentLocale()
    return format(DateTimeFormatter.ofPattern("EEEE, d MMMM", locale)).replaceFirstChar { it.titlecase(locale) }
}

@Composable
@ReadOnlyComposable
fun Int.dayKeyLetter(): String {
    val locale = currentLocale()
    return toLocalDate().dayOfWeek.getDisplayName(TextStyle.NARROW, locale).uppercase(locale)
}

@Composable
@ReadOnlyComposable
fun rangeLabel(fromDayKey: Int, toDayKey: Int): String {
    val short = DateTimeFormatter.ofPattern("d MMM", currentLocale())
    return "${fromDayKey.toLocalDate().format(short)} – ${toDayKey.toLocalDate().format(short)}"
}

@Composable
@ReadOnlyComposable
fun intervalText(minutes: Int): String {
    val hours = minutes / 60
    val rest = minutes % 60
    return when {
        hours == 0 -> stringResource(R.string.interval_minutes, rest)
        rest == 0 -> stringResource(R.string.interval_hours, hours)
        else -> stringResource(R.string.interval_hours_minutes, hours, rest)
    }
}

@StringRes
fun greetingRes(time: LocalTime): Int = when (time.hour) {
    in 5..11 -> R.string.greeting_morning
    in 12..17 -> R.string.greeting_afternoon
    else -> R.string.greeting_evening
}

fun String.firstName(): String = trim().substringBefore(' ')

fun String.initial(): String = trim().firstOrNull()?.uppercase() ?: "H"

val Gender.labelRes: Int
    @StringRes get() = when (this) {
        Gender.MALE -> R.string.gender_male
        Gender.FEMALE -> R.string.gender_female
        Gender.OTHER -> R.string.gender_other
    }

val ActivityLevel.labelRes: Int
    @StringRes get() = when (this) {
        ActivityLevel.LOW -> R.string.activity_low
        ActivityLevel.MODERATE -> R.string.activity_moderate
        ActivityLevel.HIGH -> R.string.activity_high
    }

val AchievementType.titleRes: Int
    @StringRes get() = when (this) {
        AchievementType.FIRST_GLASS -> R.string.achievement_first_glass
        AchievementType.FIRST_GOAL -> R.string.achievement_first_goal
        AchievementType.STREAK_3 -> R.string.achievement_streak_3
        AchievementType.STREAK_7 -> R.string.achievement_streak_7
        AchievementType.STREAK_30 -> R.string.achievement_streak_30
        AchievementType.TOTAL_10_LITERS -> R.string.achievement_total_10_liters
        AchievementType.TOTAL_100_LITERS -> R.string.achievement_total_100_liters
    }
