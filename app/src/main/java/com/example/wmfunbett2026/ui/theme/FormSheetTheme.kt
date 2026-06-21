package com.example.wmfunbett2026.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FormSheetDarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = PrimaryBlue.copy(alpha = 0.35f),
    onPrimaryContainer = PrimaryText,
    surface = SheetSurface,
    onSurface = SheetOnSurface,
    onSurfaceVariant = SheetOnSurfaceVariant,
    surfaceVariant = SheetChipUnselected,
    onBackground = SheetOnSurface,
    background = SheetSurface,
    error = DangerRed,
    onError = Color.White,
    outline = SheetBorderUnfocused,
    outlineVariant = SheetBorderUnfocused
)

@Composable
fun FormSheetTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FormSheetDarkColorScheme,
        typography = Typography,
        content = content
    )
}
