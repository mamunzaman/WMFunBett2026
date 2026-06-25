package com.example.wmfunbett2026.ui.designsystem.layout

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val DefaultContentPadding = 20.dp
val DefaultContentTopPadding = 16.dp
val DefaultCardPadding = 14.dp
val ActionCardHorizontalPadding = 14.dp
val ActionCardVerticalPadding = 12.dp
val ActionCardContentSpacing = 12.dp
val ChipHorizontalPadding = 10.dp
val ChipVerticalPadding = 5.dp
val StatCardValueSpacing = 6.dp

fun defaultScreenContentPadding(bottom: Dp = 148.dp): PaddingValues = PaddingValues(
    start = DefaultContentPadding,
    end = DefaultContentPadding,
    top = DefaultContentTopPadding,
    bottom = bottom
)
