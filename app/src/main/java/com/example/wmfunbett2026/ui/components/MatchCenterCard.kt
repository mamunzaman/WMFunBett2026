package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.data.model.Game
import com.example.wmfunbett2026.ui.matchcenter.MatchCenterOutcomeBadge
import com.example.wmfunbett2026.ui.theme.DangerRed
import com.example.wmfunbett2026.ui.theme.PrimaryBlue
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
    content: @Composable ColumnScope.() -> Unit
) {
    MatchCardShell(modifier = modifier, content = content)
}

@Composable
fun MatchCenterMatchCardBody(
    game: Game,
    matchdayLabel: String,
    modifier: Modifier = Modifier,
    bottomMetaOverride: String? = null
) {
    MatchCardContent(
        game = game,
        matchdayLabel = matchdayLabel,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 16.dp),
        bottomMetaOverride = bottomMetaOverride
    )
}

@Composable
fun MatchStatusPill(badge: MatchCenterOutcomeBadge?) {
    if (badge == null) return

    val (background, text, labelRes) = when (badge) {
        MatchCenterOutcomeBadge.ACTIVE -> Triple(
            PrimaryBlue.copy(alpha = 0.35f),
            TextPrimary,
            R.string.badge_active
        )
        MatchCenterOutcomeBadge.LIVE -> Triple(
            DangerRed.copy(alpha = 0.28f),
            DangerRed,
            R.string.status_live
        )
        MatchCenterOutcomeBadge.NO_WINNER,
        MatchCenterOutcomeBadge.FINISHED -> Triple(
            WinnerGreen.copy(alpha = 0.22f),
            WinnerGreen,
            R.string.status_finished
        )
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
