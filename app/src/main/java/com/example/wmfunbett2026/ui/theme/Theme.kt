package com.example.wmfunbett2026.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val FunBettDarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = PrimaryText,
    primaryContainer = PrimaryBlue,
    onPrimaryContainer = PrimaryText,
    secondary = JackpotGold,
    onSecondary = DarkNavy,
    secondaryContainer = SurfaceDark,
    onSecondaryContainer = PrimaryText,
    tertiary = WinnerGreen,
    onTertiary = PrimaryText,
    background = DarkNavy,
    onBackground = PrimaryText,
    surface = SurfaceDark,
    onSurface = PrimaryText,
    surfaceVariant = SurfaceDark,
    onSurfaceVariant = SecondaryText,
    error = DangerRed,
    onError = PrimaryText,
    outline = SecondaryText,
    outlineVariant = SurfaceDark
)

@Composable
fun WMFunBett2026Theme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = FunBettDarkColorScheme,
        typography = Typography,
        content = content
    )
}
