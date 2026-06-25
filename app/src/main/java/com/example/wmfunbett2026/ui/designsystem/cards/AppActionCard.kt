package com.example.wmfunbett2026.ui.designsystem.cards

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.wmfunbett2026.ui.designsystem.buttons.AppActionButton

@Composable
fun AppActionCard(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector? = null,
    showChevron: Boolean = true,
    enabled: Boolean = true
) {
    if (icon != null) {
        AppActionButton(
            title = title,
            subtitle = subtitle,
            onClick = onClick,
            modifier = modifier,
            icon = icon,
            showChevron = showChevron,
            enabled = enabled
        )
    } else {
        AppActionButton(
            title = title,
            subtitle = subtitle,
            onClick = onClick,
            modifier = modifier,
            showChevron = showChevron,
            enabled = enabled
        )
    }
}
