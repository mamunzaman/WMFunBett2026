package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.ui.matchcenter.matchTeamFlagEmojiOrNull
import com.example.wmfunbett2026.ui.matchcenter.matchTeamInitials
import com.example.wmfunbett2026.ui.theme.Divider
import com.example.wmfunbett2026.ui.theme.PrimaryBlue
import com.example.wmfunbett2026.ui.theme.Surface
import com.example.wmfunbett2026.ui.theme.SurfaceVariant
import com.example.wmfunbett2026.ui.theme.TextPrimary
import com.example.wmfunbett2026.ui.theme.TextSecondary

enum class MatchScoreDisplayMode {
    /** Numeric score with +/- steppers (Add Entry prediction, Live/Finished result). */
    NUMERIC,
    /** Placeholder dash when no result yet (Not Started). */
    EMPTY_DASH
}

@Composable
fun MatchScoreInputCard(
    teamA: String,
    teamB: String,
    scoreA: String,
    scoreB: String,
    onScoreAChange: (String) -> Unit,
    onScoreBChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    displayMode: MatchScoreDisplayMode = MatchScoreDisplayMode.NUMERIC
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
            .border(1.dp, Divider.copy(alpha = 0.55f), RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TeamScoreInput(
                teamName = teamA,
                score = scoreA,
                onScoreChange = onScoreAChange,
                displayMode = displayMode,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = ":",
                modifier = Modifier
                    .padding(top = 72.dp)
                    .padding(horizontal = 4.dp),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            TeamScoreInput(
                teamName = teamB,
                score = scoreB,
                onScoreChange = onScoreBChange,
                displayMode = displayMode,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun TeamScoreInput(
    teamName: String,
    score: String,
    onScoreChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    displayMode: MatchScoreDisplayMode = MatchScoreDisplayMode.NUMERIC
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TeamLogoSlot(teamName = teamName)
        Text(
            text = teamName,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        when (displayMode) {
            MatchScoreDisplayMode.NUMERIC -> ScoreStepperBox(
                score = score,
                onScoreChange = onScoreChange,
                teamName = teamName
            )
            MatchScoreDisplayMode.EMPTY_DASH -> ScoreEmptyDisplayBox()
        }
    }
}

@Composable
private fun ScoreEmptyDisplayBox(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceVariant.copy(alpha = 0.45f))
            .border(1.dp, Divider.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "-",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TeamLogoSlot(
    teamName: String,
    modifier: Modifier = Modifier
) {
    val flag = matchTeamFlagEmojiOrNull(teamName)
    Box(
        modifier = modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(SurfaceVariant)
            .border(1.dp, Divider.copy(alpha = 0.4f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (flag != null) {
            Text(text = flag, fontSize = 26.sp)
        } else {
            Text(
                text = matchTeamInitials(teamName),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun ScoreStepperBox(
    score: String,
    onScoreChange: (String) -> Unit,
    teamName: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceVariant.copy(alpha = 0.65f))
            .border(1.dp, Divider.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
            .padding(horizontal = 2.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = { onScoreChange(adjustScore(score, -1)) },
            modifier = Modifier.size(36.dp),
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = TextSecondary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = stringResource(R.string.decrease_score_for_team, teamName)
            )
        }
        Text(
            text = score.ifEmpty { "0" },
            modifier = Modifier.widthIn(min = 28.dp),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        IconButton(
            onClick = { onScoreChange(adjustScore(score, 1)) },
            modifier = Modifier.size(36.dp),
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = PrimaryBlue
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.increase_score_for_team, teamName)
            )
        }
    }
}

private fun adjustScore(current: String, delta: Int): String {
    val value = current.trim().toIntOrNull() ?: 0
    return (value + delta).coerceIn(0, 99).toString()
}
