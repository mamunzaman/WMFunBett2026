package com.example.wmfunbett2026.ui.designsystem.buttons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.example.wmfunbett2026.ui.designsystem.layout.IconButtonSize

@Composable
fun AppMenuButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    enabled: Boolean = true,
    filled: Boolean = true,
    iconTint: Color? = null,
    buttonSize: Dp = IconButtonSize
) {
    AppIconButton(
        icon = Icons.Default.MoreVert,
        contentDescription = contentDescription,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        filled = filled,
        iconTint = iconTint,
        buttonSize = buttonSize
    )
}
