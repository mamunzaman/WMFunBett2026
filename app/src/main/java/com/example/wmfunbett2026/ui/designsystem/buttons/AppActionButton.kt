package com.example.wmfunbett2026.ui.designsystem.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
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
import com.example.wmfunbett2026.ui.designsystem.layout.ActionCardContentSpacing
import com.example.wmfunbett2026.ui.designsystem.layout.ActionCardCornerRadius
import com.example.wmfunbett2026.ui.designsystem.layout.ActionCardHorizontalPadding
import com.example.wmfunbett2026.ui.designsystem.layout.ActionCardVerticalPadding
import com.example.wmfunbett2026.ui.designsystem.layout.ActionIconSize
import com.example.wmfunbett2026.ui.designsystem.layout.IconButtonSize
import com.example.wmfunbett2026.ui.theme.GlassBorder
import com.example.wmfunbett2026.ui.theme.MatchCardCompactSurface
import com.example.wmfunbett2026.ui.theme.PrimaryBlue
import com.example.wmfunbett2026.ui.theme.PrimaryBlueBright
import com.example.wmfunbett2026.ui.theme.PrimaryText
import com.example.wmfunbett2026.ui.theme.SecondaryText

@Composable
fun AppActionButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Add,
    subtitle: String? = null,
    showChevron: Boolean = true,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = rememberAppPressScale(isPressed = isPressed, enabled = enabled)
    val shape = RoundedCornerShape(ActionCardCornerRadius)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .appPressGraphicsLayer(scale)
            .clip(shape)
            .background(MatchCardCompactSurface)
            .border(1.dp, GlassBorder, shape)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = ripple(color = PrimaryBlueBright.copy(alpha = 0.12f)),
                onClick = onClick
            )
            .padding(
                horizontal = ActionCardHorizontalPadding,
                vertical = ActionCardVerticalPadding
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ActionCardContentSpacing)
    ) {
        Box(
            modifier = Modifier
                .size(IconButtonSize)
                .clip(CircleShape)
                .background(PrimaryBlue.copy(alpha = 0.24f))
                .border(1.dp, GlassBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryBlueBright,
                modifier = Modifier.size(ActionIconSize)
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (showChevron) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = null,
                tint = SecondaryText,
                modifier = Modifier.size(ActionIconSize)
            )
        }
    }
}
