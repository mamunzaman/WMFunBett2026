package com.example.wmfunbett2026.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val FunBettDarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = TextPrimary,
    primaryContainer = PrimaryBlueBright,
    onPrimaryContainer = TextPrimary,
    secondary = JackpotGold,
    onSecondary = DarkNavy,
    secondaryContainer = SurfaceVariant,
    onSecondaryContainer = TextPrimary,
    tertiary = AccentPurpleLight,
    onTertiary = TextPrimary,
    background = BackgroundDeep,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    error = DangerRed,
    onError = TextPrimary,
    outline = Divider,
    outlineVariant = GlassBorder
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
