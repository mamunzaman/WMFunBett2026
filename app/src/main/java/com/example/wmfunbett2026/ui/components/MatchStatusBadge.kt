package com.example.wmfunbett2026.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.data.model.MatchStatus
import com.example.wmfunbett2026.ui.theme.DangerRed
import com.example.wmfunbett2026.ui.theme.SecondaryText
import com.example.wmfunbett2026.ui.theme.WinnerGreen

enum class MatchStatusBadgeStyle {
    Pill,
    Compact
}

@Composable
fun MatchStatusBadge(
    status: MatchStatus,
    modifier: Modifier = Modifier,
    style: MatchStatusBadgeStyle = MatchStatusBadgeStyle.Pill
) {
    when (status) {
        MatchStatus.LIVE -> LiveMatchStatusBadge(modifier = modifier, style = style)
        MatchStatus.NOT_STARTED -> NotStartedMatchStatusBadge(modifier = modifier, style = style)
        MatchStatus.FINISHED -> FinishedMatchStatusBadge(modifier = modifier, style = style)
    }
}

@Composable
private fun LiveMatchStatusBadge(
    modifier: Modifier = Modifier,
    style: MatchStatusBadgeStyle
) {
    val pulse = rememberInfiniteTransition(label = "livePulse")
    val dotAlpha by pulse.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "liveDotAlpha"
    )
    val dotSize = if (style == MatchStatusBadgeStyle.Compact) 6.dp else 8.dp
    val horizontalPadding = if (style == MatchStatusBadgeStyle.Compact) 0.dp else 12.dp
    val verticalPadding = if (style == MatchStatusBadgeStyle.Compact) 0.dp else 5.dp
    val textStyle = if (style == MatchStatusBadgeStyle.Compact) {
        MaterialTheme.typography.labelSmall
    } else {
        MaterialTheme.typography.labelMedium
    }

    Row(
        modifier = modifier
            .then(
                if (style == MatchStatusBadgeStyle.Pill) {
                    Modifier.background(DangerRed.copy(alpha = 0.24f), RoundedCornerShape(999.dp))
                } else {
                    Modifier
                }
            )
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(if (style == MatchStatusBadgeStyle.Compact) 4.dp else 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(dotSize)
                .clip(CircleShape)
                .background(DangerRed.copy(alpha = dotAlpha))
        )
        Text(
            text = stringResource(R.string.header_live),
            style = textStyle,
            fontWeight = FontWeight.Bold,
            color = DangerRed
        )
    }
}

@Composable
private fun NotStartedMatchStatusBadge(
    modifier: Modifier = Modifier,
    style: MatchStatusBadgeStyle
) {
    val textStyle = if (style == MatchStatusBadgeStyle.Compact) {
        MaterialTheme.typography.labelSmall
    } else {
        MaterialTheme.typography.labelMedium
    }
    val horizontalPadding = if (style == MatchStatusBadgeStyle.Compact) 0.dp else 12.dp
    val verticalPadding = if (style == MatchStatusBadgeStyle.Compact) 0.dp else 5.dp
    val iconSize = if (style == MatchStatusBadgeStyle.Compact) 12.dp else 14.dp

    Row(
        modifier = modifier
            .then(
                if (style == MatchStatusBadgeStyle.Pill) {
                    Modifier.background(SecondaryText.copy(alpha = 0.2f), RoundedCornerShape(999.dp))
                } else {
                    Modifier
                }
            )
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.Schedule,
            contentDescription = null,
            tint = SecondaryText,
            modifier = Modifier.size(iconSize)
        )
        Text(
            text = stringResource(R.string.status_not_started).uppercase(),
            style = textStyle,
            fontWeight = FontWeight.SemiBold,
            color = SecondaryText
        )
    }
}

@Composable
private fun FinishedMatchStatusBadge(
    modifier: Modifier = Modifier,
    style: MatchStatusBadgeStyle
) {
    val textStyle = if (style == MatchStatusBadgeStyle.Compact) {
        MaterialTheme.typography.labelSmall
    } else {
        MaterialTheme.typography.labelMedium
    }
    val horizontalPadding = if (style == MatchStatusBadgeStyle.Compact) 0.dp else 12.dp
    val verticalPadding = if (style == MatchStatusBadgeStyle.Compact) 0.dp else 5.dp
    val (background, textColor) = if (style == MatchStatusBadgeStyle.Pill) {
        WinnerGreen.copy(alpha = 0.22f) to WinnerGreen
    } else {
        androidx.compose.ui.graphics.Color.Transparent to SecondaryText
    }

    Text(
        text = stringResource(R.string.entry_table_status_ft).uppercase(),
        modifier = modifier
            .background(background, RoundedCornerShape(999.dp))
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        style = textStyle,
        fontWeight = FontWeight.SemiBold,
        color = textColor
    )
}
