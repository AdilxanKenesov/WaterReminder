package com.visionsystems.waterreminder.presenter.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.visionsystems.waterreminder.R

val Outfit = FontFamily(
    Font(R.font.outfit_medium, FontWeight.Medium),
    Font(R.font.outfit_semibold, FontWeight.SemiBold),
    Font(R.font.outfit_bold, FontWeight.Bold)
)

val Manrope = FontFamily(
    Font(R.font.manrope_regular, FontWeight.Normal),
    Font(R.font.manrope_medium, FontWeight.Medium),
    Font(R.font.manrope_semibold, FontWeight.SemiBold),
    Font(R.font.manrope_bold, FontWeight.Bold)
)

private fun outfit(size: Int, weight: FontWeight = FontWeight.SemiBold, lineHeight: Int = size + 6) = TextStyle(
    fontFamily = Outfit,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
    letterSpacing = (-0.2).sp
)

private fun manrope(size: Int, weight: FontWeight, lineHeight: Int = size + 6) = TextStyle(
    fontFamily = Manrope,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = lineHeight.sp
)

val HydroTypography = Typography(
    displayLarge = outfit(52, lineHeight = 56),
    displayMedium = outfit(44, lineHeight = 48),
    displaySmall = outfit(34, lineHeight = 40),
    headlineLarge = outfit(30, lineHeight = 36),
    headlineMedium = outfit(28, lineHeight = 34),
    headlineSmall = outfit(24, lineHeight = 30),
    titleLarge = outfit(22, lineHeight = 28),
    titleMedium = outfit(18, lineHeight = 24),
    titleSmall = manrope(16, FontWeight.Bold, 22),
    bodyLarge = manrope(16, FontWeight.Normal, 24),
    bodyMedium = manrope(15, FontWeight.Normal, 22),
    bodySmall = manrope(13, FontWeight.Medium, 18),
    labelLarge = manrope(15, FontWeight.Bold, 20),
    labelMedium = manrope(13, FontWeight.Bold, 18),
    labelSmall = manrope(11, FontWeight.SemiBold, 14)
)
