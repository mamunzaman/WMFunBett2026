package com.example.wmfunbett2026.ui.designsystem.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.ui.designsystem.animation.appPressGraphicsLayer
import com.example.wmfunbett2026.ui.designsystem.animation.rememberAppPressScale
import com.example.wmfunbett2026.ui.designsystem.layout.DefaultButtonHeight
import com.example.wmfunbett2026.ui.designsystem.layout.DefaultCornerRadius
import com.example.wmfunbett2026.ui.theme.GlassBorder
import com.example.wmfunbett2026.ui.theme.MatchCardCompactSurface
import com.example.wmfunbett2026.ui.theme.PrimaryBlueBright
import com.example.wmfunbett2026.ui.theme.SecondaryText
import com.example.wmfunbett2026.ui.theme.TextDisabled

@Composable
fun AppSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fillMaxWidth: Boolean = true,
    icon: ImageVector? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = rememberAppPressScale(isPressed = isPressed, enabled = enabled)
    val shape = RoundedCornerShape(DefaultCornerRadius)

    Box(
        modifier = modifier
            .then(if (fillMaxWidth) Modifier.fillMaxWidth() else Modifier)
            .appPressGraphicsLayer(scale)
            .height(DefaultButtonHeight)
            .clip(shape)
            .background(MatchCardCompactSurface)
            .border(
                width = 1.dp,
                color = if (enabled) GlassBorder else GlassBorder.copy(alpha = 0.45f),
                shape = shape
            )
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = ripple(color = PrimaryBlueBright.copy(alpha = 0.1f)),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = if (enabled) SecondaryText else TextDisabled,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
