package com.example.wmfunbett2026.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppScreen(
    val label: String,
    val icon: ImageVector
) {
    Dashboard("Dashboard", Icons.Default.Dashboard),
    Rounds("Rounds", Icons.Default.SportsSoccer),
    Jackpot("Jackpot", Icons.Default.EmojiEvents),
    Settings("Settings", Icons.Default.Settings)
}
