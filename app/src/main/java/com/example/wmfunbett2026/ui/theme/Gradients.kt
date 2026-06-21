package com.example.wmfunbett2026.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush

val PremiumHeaderGradient: Brush
    @Composable
    get() = Brush.linearGradient(
        colors = listOf(PrimaryBlue, AccentPurple),
        start = Offset.Zero,
        end = Offset(900f, 400f)
    )

val MatchCardGradient: Brush
    @Composable
    get() = Brush.verticalGradient(
        colors = listOf(Surface, MatchCardMidTone, Surface)
    )

val JackpotCardGradient: Brush
    @Composable
    get() = Brush.verticalGradient(
        colors = listOf(Surface, JackpotCardMidTone)
    )

val FabGradient: Brush
    @Composable
    get() = Brush.linearGradient(
        colors = listOf(PrimaryBlueBright, AccentPurple),
        start = Offset(0f, 0f),
        end = Offset(400f, 400f)
    )
