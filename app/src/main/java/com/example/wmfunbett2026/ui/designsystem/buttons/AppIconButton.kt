package com.example.wmfunbett2026.ui.designsystem.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.ui.designsystem.animation.appPressGraphicsLayer
import com.example.wmfunbett2026.ui.designsystem.animation.rememberAppPressScale
import com.example.wmfunbett2026.ui.designsystem.layout.IconButtonIconSize
import com.example.wmfunbett2026.ui.designsystem.layout.IconButtonSize
import com.example.wmfunbett2026.ui.theme.GlassBorder
import com.example.wmfunbett2026.ui.theme.MatchCardCompactSurface
import com.example.wmfunbett2026.ui.theme.PrimaryBlueBright
import com.example.wmfunbett2026.ui.theme.SecondaryText
import com.example.wmfunbett2026.ui.theme.TextDisabled

@Composable
fun AppIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selected: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = rememberAppPressScale(isPressed = isPressed, enabled = enabled)
    val borderColor = if (selected) PrimaryBlueBright.copy(alpha = 0.55f) else GlassBorder

    Box(
        modifier = modifier
            .appPressGraphicsLayer(scale)
            .size(IconButtonSize)
            .clip(CircleShape)
            .background(MatchCardCompactSurface)
            .border(1.dp, borderColor, CircleShape)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = ripple(color = PrimaryBlueBright.copy(alpha = 0.12f)),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = when {
                !enabled -> TextDisabled
                selected -> PrimaryBlueBright
                else -> SecondaryText
            },
            modifier = Modifier.size(IconButtonIconSize)
        )
    }
}
