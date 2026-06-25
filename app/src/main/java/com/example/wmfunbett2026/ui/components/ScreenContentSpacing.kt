package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.ui.designsystem.layout.DefaultContentPadding
import com.example.wmfunbett2026.ui.designsystem.layout.DefaultContentTopPadding

val ScreenContentHorizontalPadding = DefaultContentPadding
val ScreenContentTopPadding = DefaultContentTopPadding

fun screenContentPadding(): PaddingValues = PaddingValues(
    start = ScreenContentHorizontalPadding,
    end = ScreenContentHorizontalPadding,
    top = ScreenContentTopPadding,
    bottom = MatchCenterBottomNavReservedHeight
)
