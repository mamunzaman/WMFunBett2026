package com.example.wmfunbett2026.ui.designsystem.chips

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.ui.designsystem.layout.DefaultCornerRadius
import com.example.wmfunbett2026.ui.designsystem.layout.SegmentControlHeight
import com.example.wmfunbett2026.ui.theme.Divider
import com.example.wmfunbett2026.ui.theme.GlassBorder
import com.example.wmfunbett2026.ui.theme.MatchCardCompactSurface
import com.example.wmfunbett2026.ui.theme.PrimaryBlue
import com.example.wmfunbett2026.ui.theme.PrimaryBlueBright
import com.example.wmfunbett2026.ui.theme.SecondaryText
import com.example.wmfunbett2026.ui.theme.TextPrimary

@Composable
fun <T> AppSegmentedStatusControl(
    options: List<T>,
    selected: T,
    label: @Composable (T) -> String,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    equals: (T, T) -> Boolean = { a, b -> a == b }
) {
    val shape = RoundedCornerShape(DefaultCornerRadius)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(SegmentControlHeight)
            .clip(shape)
            .border(1.dp, GlassBorder, shape)
            .background(MatchCardCompactSurface.copy(alpha = 0.55f)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEachIndexed { index, option ->
            val isSelected = equals(option, selected)
            val segmentShape = when {
                options.size == 1 -> shape
                index == 0 -> RoundedCornerShape(
                    topStart = DefaultCornerRadius,
                    bottomStart = DefaultCornerRadius,
                    topEnd = 0.dp,
                    bottomEnd = 0.dp
                )
                index == options.lastIndex -> RoundedCornerShape(
                    topStart = 0.dp,
                    bottomStart = 0.dp,
                    topEnd = DefaultCornerRadius,
                    bottomEnd = DefaultCornerRadius
                )
                else -> RoundedCornerShape(0.dp)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(segmentShape)
                    .background(
                        if (isSelected) PrimaryBlue.copy(alpha = 0.34f)
                        else MatchCardCompactSurface.copy(alpha = 0.28f)
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = PrimaryBlue.copy(alpha = 0.12f)),
                        onClick = { onSelected(option) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label(option),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) PrimaryBlueBright else SecondaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }

            if (index < options.lastIndex) {
                VerticalDivider(
                    modifier = Modifier.fillMaxHeight(),
                    thickness = 1.dp,
                    color = Divider.copy(alpha = 0.45f)
                )
            }
        }
    }
}
