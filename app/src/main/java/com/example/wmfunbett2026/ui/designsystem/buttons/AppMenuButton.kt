package com.example.wmfunbett2026.ui.designsystem.buttons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AppMenuButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    enabled: Boolean = true
) {
    AppIconButton(
        icon = Icons.Default.MoreVert,
        contentDescription = contentDescription,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    )
}
