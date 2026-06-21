package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp

val ScreenContentHorizontalPadding = 20.dp
val ScreenContentTopPadding = 16.dp

fun screenContentPadding(): PaddingValues = PaddingValues(
    start = ScreenContentHorizontalPadding,
    end = ScreenContentHorizontalPadding,
    top = ScreenContentTopPadding,
    bottom = MatchCenterBottomNavReservedHeight
)
