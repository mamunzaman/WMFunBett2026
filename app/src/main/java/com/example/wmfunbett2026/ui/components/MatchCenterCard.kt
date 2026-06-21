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
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.wmfunbett2026.data.model.Game
import com.example.wmfunbett2026.ui.matchcenter.MatchCardDisplayMode
import com.example.wmfunbett2026.ui.matchcenter.MatchCenterOutcomeBadge
import com.example.wmfunbett2026.ui.theme.DangerRed
import com.example.wmfunbett2026.ui.theme.PrimaryBlue
import com.example.wmfunbett2026.ui.theme.PrimaryBlueBright
import com.example.wmfunbett2026.ui.theme.TextPrimary
import com.example.wmfunbett2026.ui.theme.WinnerGreen

/** @see MatchCard */
@Composable
fun MatchCenterCard(
    game: Game,
    matchdayLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    MatchCard(
        game = game,
        matchdayLabel = matchdayLabel,
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
fun MatchCenterMatchCardShell(
    modifier: Modifier = Modifier,
    game: Game? = null,
    displayMode: MatchCardDisplayMode = MatchCardDisplayMode.DETAIL,
    content: @Composable ColumnScope.() -> Unit
) {
    MatchCardShell(
        game = game,
        displayMode = displayMode,
        modifier = modifier,
        content = content
    )
}

@Composable
fun MatchCenterMatchCardBody(
    game: Game,
    matchdayLabel: String,
    modifier: Modifier = Modifier,
    bottomMetaOverride: String? = null,
    displayMode: MatchCardDisplayMode = MatchCardDisplayMode.DETAIL
) {
    MatchCardContent(
        game = game,
        matchdayLabel = matchdayLabel,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 16.dp),
        bottomMetaOverride = bottomMetaOverride,
        displayMode = displayMode
    )
}

@Composable
fun MatchStatusPill(badge: MatchCenterOutcomeBadge?) {
    if (badge == null) return

    when (badge) {
        MatchCenterOutcomeBadge.LIVE -> LiveStatusPill()
        else -> {
            val (background, text, labelRes) = when (badge) {
                MatchCenterOutcomeBadge.ACTIVE -> Triple(
                    PrimaryBlue.copy(alpha = 0.35f),
                    TextPrimary,
                    R.string.badge_active
                )
                MatchCenterOutcomeBadge.UPCOMING -> Triple(
                    PrimaryBlue.copy(alpha = 0.22f),
                    PrimaryBlueBright,
                    R.string.status_upcoming
                )
                MatchCenterOutcomeBadge.NO_WINNER,
                MatchCenterOutcomeBadge.FINISHED -> Triple(
                    WinnerGreen.copy(alpha = 0.22f),
                    WinnerGreen,
                    R.string.status_finished
                )
                MatchCenterOutcomeBadge.LIVE -> error("Handled above")
            }

            Text(
                text = stringResource(labelRes),
                modifier = Modifier
                    .background(background, RoundedCornerShape(999.dp))
                    .padding(horizontal = 12.dp, vertical = 5.dp),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = text
            )
        }
    }
}

@Composable
private fun LiveStatusPill(modifier: Modifier = Modifier) {
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

    Row(
        modifier = modifier
            .background(DangerRed.copy(alpha = 0.24f), RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(DangerRed.copy(alpha = dotAlpha))
        )
        Text(
            text = stringResource(R.string.status_live),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = DangerRed
        )
    }
}
