package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.data.model.Game
import com.example.wmfunbett2026.data.model.toEuroLabel
import com.example.wmfunbett2026.ui.matchcenter.MatchCenterOutcomeBadge
import com.example.wmfunbett2026.ui.matchcenter.centerScoreText
import com.example.wmfunbett2026.ui.matchcenter.primaryTippLabel
import com.example.wmfunbett2026.ui.matchcenter.resolveOutcomeBadge
import com.example.wmfunbett2026.ui.theme.DangerRed
import com.example.wmfunbett2026.ui.theme.JackpotGold
import com.example.wmfunbett2026.ui.theme.PrimaryBlue
import com.example.wmfunbett2026.ui.theme.PrimaryText
import com.example.wmfunbett2026.ui.theme.SecondaryText
import com.example.wmfunbett2026.ui.theme.SurfaceDark
import com.example.wmfunbett2026.ui.theme.WinnerGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchCenterCard(
    game: Game,
    matchdayLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val outcomeBadge = resolveOutcomeBadge(game)
    val tippLabel = game.primaryTippLabel()

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MatchdayBadge(label = matchdayLabel)
                outcomeBadge?.let { MatchOutcomeBadge(badge = it) }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = game.teamA,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End
                )
                Text(
                    text = game.centerScoreText(),
                    modifier = Modifier.padding(horizontal = 12.dp),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
                Text(
                    text = game.teamB,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = buildString {
                        if (tippLabel != null) append(tippLabel)
                        append(" · ")
                        append(game.totalKasse.toEuroLabel())
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = SecondaryText,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        text = game.dateTimeLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryText
                    )
                }
            }
        }
    }
}

@Composable
private fun MatchdayBadge(label: String) {
    Text(
        text = label,
        modifier = Modifier
            .background(PrimaryBlue.copy(alpha = 0.35f), RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
        color = PrimaryText
    )
}

@Composable
private fun MatchOutcomeBadge(badge: MatchCenterOutcomeBadge) {
    val (background, text, labelRes) = when (badge) {
        MatchCenterOutcomeBadge.ACTIVE -> Triple(
            PrimaryBlue.copy(alpha = 0.35f),
            PrimaryText,
            R.string.badge_active
        )
        MatchCenterOutcomeBadge.LIVE -> Triple(
            DangerRed.copy(alpha = 0.28f),
            DangerRed,
            R.string.status_live
        )
        MatchCenterOutcomeBadge.NO_WINNER -> Triple(
            DangerRed.copy(alpha = 0.22f),
            DangerRed,
            R.string.badge_no_winner
        )
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
            .padding(horizontal = 10.dp, vertical = 4.dp),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
        color = text
    )
}
