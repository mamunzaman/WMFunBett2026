package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.data.model.Game
import com.example.wmfunbett2026.data.model.toEuroLabel
import com.example.wmfunbett2026.ui.matchcenter.MatchCenterOutcomeBadge
import com.example.wmfunbett2026.ui.matchcenter.centerScoreText
import com.example.wmfunbett2026.ui.matchcenter.primaryTippLabel
import com.example.wmfunbett2026.ui.matchcenter.resolveOutcomeBadge
import com.example.wmfunbett2026.ui.matchcenter.teamFlagEmoji
import com.example.wmfunbett2026.ui.theme.DangerRed
import com.example.wmfunbett2026.ui.theme.JackpotGold
import com.example.wmfunbett2026.ui.theme.PrimaryBlue
import com.example.wmfunbett2026.ui.theme.PrimaryText
import com.example.wmfunbett2026.ui.theme.SecondaryText
import com.example.wmfunbett2026.ui.theme.SurfaceDark
import com.example.wmfunbett2026.ui.theme.WinnerGreen

private val MatchCardShape = RoundedCornerShape(18.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchCenterCard(
    game: Game,
    matchdayLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    MatchCenterMatchCardShell(
        modifier = modifier.clickable(onClick = onClick)
    ) {
        MatchCenterMatchCardBody(
            game = game,
            matchdayLabel = matchdayLabel
        )
    }
}

@Composable
fun MatchCenterMatchCardShell(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MatchCardShape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SurfaceDark,
                        SurfaceDark.copy(alpha = 0.92f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = PrimaryBlue.copy(alpha = 0.22f),
                shape = MatchCardShape
            ),
        content = content
    )
}

@Composable
fun MatchCenterMatchCardBody(
    game: Game,
    matchdayLabel: String,
    modifier: Modifier = Modifier,
    bottomMetaOverride: String? = null
) {
    val outcomeBadge = resolveOutcomeBadge(game)
    val tippLabel = game.primaryTippLabel()
    val bottomMeta = bottomMetaOverride ?: buildString {
        if (tippLabel != null) {
            append(tippLabel)
            append(" · ")
        }
        append(game.totalKasse.toEuroLabel())
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MatchdayBadge(label = matchdayLabel)
            MatchStatusPill(badge = outcomeBadge)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TeamSide(
                teamName = game.teamA,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = game.centerScoreText(),
                modifier = Modifier.padding(horizontal = 14.dp),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = PrimaryText,
                fontSize = 32.sp
            )
            TeamSide(
                teamName = game.teamB,
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = bottomMeta,
                style = MaterialTheme.typography.bodyMedium,
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
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryText
                )
            }
        }
    }
}

@Composable
private fun TeamSide(
    teamName: String,
    textAlign: TextAlign,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = when (textAlign) {
            TextAlign.End -> Alignment.End
            else -> Alignment.Start
        }
    ) {
        Text(
            text = teamFlagEmoji(teamName),
            fontSize = 34.sp,
            textAlign = textAlign,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = teamName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = PrimaryText,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = textAlign,
            modifier = Modifier.fillMaxWidth(),
            lineHeight = 26.sp
        )
    }
}

@Composable
private fun MatchdayBadge(label: String) {
    Text(
        text = label,
        modifier = Modifier
            .background(PrimaryBlue.copy(alpha = 0.32f), RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 5.dp),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = PrimaryText
    )
}

@Composable
fun MatchStatusPill(badge: MatchCenterOutcomeBadge?) {
    if (badge == null) return

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
