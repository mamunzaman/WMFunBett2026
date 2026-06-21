package com.example.wmfunbett2026.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payments
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppScreen(
    val label: String,
    val icon: ImageVector
) {
    Home("Home", Icons.Default.Home),
    WM2026("Tipps", Icons.Default.EmojiEvents),
    Kasse("Kasse", Icons.Default.Payments)
}
