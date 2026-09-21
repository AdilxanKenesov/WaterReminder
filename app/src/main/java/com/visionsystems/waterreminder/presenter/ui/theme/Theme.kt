package com.visionsystems.waterreminder.presenter.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val HydroColorScheme = lightColorScheme(
    primary = HydroPrimary,
    onPrimary = HydroOnPrimary,
    primaryContainer = HydroPrimaryContainer,
    onPrimaryContainer = HydroPrimaryDark,
    secondary = HydroCoral,
    onSecondary = HydroOnPrimary,
    background = HydroBackground,
    onBackground = HydroInk,
    surface = HydroSurface,
    onSurface = HydroInk,
    surfaceVariant = HydroSegment,
    onSurfaceVariant = HydroInkMuted,
    outline = HydroOutline,
    outlineVariant = HydroLine,
    error = HydroDanger,
    scrim = HydroScrim
)

@Composable
fun HydroTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = HydroColorScheme,
        typography = HydroTypography,
        content = content
    )
}
