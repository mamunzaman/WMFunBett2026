package com.example.wmfunbett2026.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.wmfunbett2026.R

enum class AppScreen(
    @StringRes val labelRes: Int,
    val icon: ImageVector
) {
    Matches(R.string.nav_matches, Icons.Outlined.SportsSoccer),
    Leagues(R.string.nav_leagues, Icons.Outlined.EmojiEvents),
    Friends(R.string.nav_friends, Icons.Outlined.Group),
    Settings(R.string.nav_settings, Icons.Default.Settings)
}
