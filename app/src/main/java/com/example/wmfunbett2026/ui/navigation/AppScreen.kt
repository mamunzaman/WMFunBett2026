package com.example.wmfunbett2026.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payments
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.wmfunbett2026.R

enum class AppScreen(
    @StringRes val labelRes: Int,
    val icon: ImageVector
) {
    Home(R.string.nav_home, Icons.Default.Home),
    WM2026(R.string.nav_tipps, Icons.Default.EmojiEvents),
    Kasse(R.string.nav_kasse, Icons.Default.Payments)
}
