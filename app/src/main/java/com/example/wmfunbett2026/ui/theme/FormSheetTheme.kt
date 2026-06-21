package com.example.wmfunbett2026.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FormSheetLightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = PrimaryBlue,
    onPrimaryContainer = Color.White,
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
        colorScheme = FormSheetLightColorScheme,
        typography = Typography,
        content = content
    )
}
