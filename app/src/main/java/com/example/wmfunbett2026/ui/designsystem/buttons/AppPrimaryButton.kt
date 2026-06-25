package com.example.wmfunbett2026.ui.designsystem.buttons

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.ui.designsystem.animation.appPressGraphicsLayer
import com.example.wmfunbett2026.ui.designsystem.animation.rememberAppPressScale
import com.example.wmfunbett2026.ui.designsystem.layout.DefaultButtonElevation
import com.example.wmfunbett2026.ui.designsystem.layout.DefaultButtonHeight
import com.example.wmfunbett2026.ui.designsystem.layout.DefaultCornerRadius
import com.example.wmfunbett2026.ui.theme.PrimaryBlue
import com.example.wmfunbett2026.ui.theme.PrimaryBlueBright
import com.example.wmfunbett2026.ui.theme.TextPrimary

@Composable
fun AppPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    fillMaxWidth: Boolean = true,
    icon: ImageVector? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isInteractive = enabled && !loading
    val scale = rememberAppPressScale(isPressed = isPressed, enabled = isInteractive)
    val shape = RoundedCornerShape(DefaultCornerRadius)

    Box(
        modifier = modifier
            .then(if (fillMaxWidth) Modifier.fillMaxWidth() else Modifier)
            .appPressGraphicsLayer(scale)
            .height(DefaultButtonHeight)
            .shadow(
                elevation = if (isInteractive) DefaultButtonElevation else 0.dp,
                shape = shape,
                ambientColor = PrimaryBlue.copy(alpha = 0.35f),
                spotColor = PrimaryBlue.copy(alpha = 0.25f)
            )
            .clip(shape)
            .background(
                brush = Brush.horizontalGradient(
                    colors = if (isInteractive) {
                        listOf(PrimaryBlueBright.copy(alpha = 0.95f), PrimaryBlue)
                    } else {
                        listOf(PrimaryBlue.copy(alpha = 0.38f), PrimaryBlue.copy(alpha = 0.32f))
                    }
                )
            )
            .clickable(
                enabled = isInteractive,
                interactionSource = interactionSource,
                indication = ripple(color = TextPrimary.copy(alpha = 0.12f)),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = if (isInteractive) TextPrimary else TextPrimary.copy(alpha = 0.6f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
