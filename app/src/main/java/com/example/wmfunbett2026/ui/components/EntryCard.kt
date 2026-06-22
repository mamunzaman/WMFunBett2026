package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.example.wmfunbett2026.data.model.Entry
import com.example.wmfunbett2026.data.model.Game
import com.example.wmfunbett2026.data.model.MatchStatus
import com.example.wmfunbett2026.data.model.toEuroLabel
import com.example.wmfunbett2026.ui.theme.Divider
import com.example.wmfunbett2026.ui.theme.JackpotGold
import com.example.wmfunbett2026.ui.theme.MatchCardCompactSurface
import com.example.wmfunbett2026.ui.theme.PrimaryBlue
import com.example.wmfunbett2026.ui.theme.Surface
import com.example.wmfunbett2026.ui.theme.TextPrimary
import com.example.wmfunbett2026.ui.theme.TextSecondary

private val EntryTableShape = RoundedCornerShape(14.dp)
private val EntryTableRowHeight = 78.dp
private val EntryWinnerBandInset = 10.dp
private val EntryWinnerBandVerticalInset = 4.dp
private val EntryWinnerBandEdgeExtraInset = 4.dp
private val EntryWinnerBandShape = RoundedCornerShape(8.dp)
private val PickColumnWeight = 1.2f
private val PredictColumnWeight = 0.85f
private val CurrentColumnWeight = 1f
private val StakeColumnWeight = 0.75f

@Composable
fun TippGroupEntryTable(
    game: Game,
    entries: List<Entry>,
    winningEntryIds: Set<String>,
    winnerNames: List<String>,
    onEntryClick: (Entry) -> Unit,
    modifier: Modifier = Modifier
) {
    val matchStatus = game.status
    val scoreDash = stringResource(R.string.entry_table_score_dash)
    val currentScoreLabel = when (matchStatus) {
        MatchStatus.NOT_STARTED -> scoreDash
        else -> game.compactScoreOrNull() ?: scoreDash
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(EntryTableShape)
            .background(Surface)
            .border(1.dp, Divider.copy(alpha = 0.85f), EntryTableShape)
    ) {
        if (winnerNames.isNotEmpty()) {
            EntryWinnerSummaryRow(winnerCount = winnerNames.size)
            HorizontalDivider(color = Divider.copy(alpha = 0.7f))
        }
        EntryTableHeaderRow()
        entries.forEachIndexed { index, entry ->
            EntryCard(
                name = entry.friendName,
                prediction = entry.prediction,
                amountLabel = entry.amount.toEuroLabel(),
                matchStatus = matchStatus,
                currentScoreLabel = currentScoreLabel,
                isWinner = entry.id in winningEntryIds,
                isFirstRow = index == 0,
                isLastRow = index == entries.lastIndex,
                showDivider = index < entries.lastIndex,
                onClick = { onEntryClick(entry) }
            )
        }
    }
}

@Composable
private fun EntryWinnerSummaryRow(
    winnerCount: Int,
    modifier: Modifier = Modifier
) {
    val summary = if (winnerCount == 1) {
        stringResource(R.string.tipp_group_winner_count_one)
    } else {
        stringResource(R.string.tipp_group_winner_count_many, winnerCount)
    }

    Text(
        text = summary,
        modifier = modifier
            .fillMaxWidth()
            .background(JackpotGold.copy(alpha = 0.08f))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = JackpotGold
    )
}

@Composable
private fun EntryTableHeaderRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        EntryTableHeaderCell(
            text = stringResource(R.string.entry_table_pick),
            modifier = Modifier.weight(PickColumnWeight),
            textAlign = TextAlign.Start
        )
        EntryTableHeaderCell(
            text = stringResource(R.string.entry_table_predict),
            modifier = Modifier.weight(PredictColumnWeight),
            textAlign = TextAlign.Center
        )
        EntryTableHeaderCell(
            text = stringResource(R.string.entry_table_current),
            modifier = Modifier.weight(CurrentColumnWeight),
            textAlign = TextAlign.Center
        )
        EntryTableHeaderCell(
            text = stringResource(R.string.entry_table_stake),
            modifier = Modifier.weight(StakeColumnWeight),
            textAlign = TextAlign.End
        )
    }
    HorizontalDivider(color = Divider.copy(alpha = 0.7f))
}

@Composable
private fun EntryTableHeaderCell(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start
) {
    Text(
        text = text.uppercase(),
        modifier = modifier,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
        color = TextSecondary,
        letterSpacing = 0.8.sp,
        textAlign = textAlign,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun EntryCard(
    name: String,
    prediction: String,
    amountLabel: String,
    matchStatus: MatchStatus,
    currentScoreLabel: String,
    isWinner: Boolean = false,
    isFirstRow: Boolean = false,
    isLastRow: Boolean = false,
    showDivider: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val predictionColor = if (isWinner) JackpotGold else TextPrimary

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(EntryTableRowHeight)
        ) {
            if (isWinner) {
                val topInset = EntryWinnerBandVerticalInset +
                    if (isFirstRow) EntryWinnerBandEdgeExtraInset else 0.dp
                val bottomInset = EntryWinnerBandVerticalInset +
                    if (isLastRow) EntryWinnerBandEdgeExtraInset else 0.dp

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = EntryWinnerBandInset,
                            end = EntryWinnerBandInset,
                            top = topInset,
                            bottom = bottomInset
                        )
                        .clip(EntryWinnerBandShape)
                        .background(JackpotGold.copy(alpha = 0.05f))
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
            Row(
                modifier = Modifier.weight(PickColumnWeight),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FriendInitialsAvatar(
                    initials = friendDisplayInitials(name),
                    size = 32.dp
                )
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    if (isWinner) {
                        Icon(
                            imageVector = Icons.Outlined.EmojiEvents,
                            contentDescription = null,
                            tint = JackpotGold,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isWinner) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (isWinner) TextPrimary else TextPrimary.copy(alpha = 0.88f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Box(
                modifier = Modifier.weight(PredictColumnWeight),
                contentAlignment = Alignment.Center
            ) {
                EntryPredictionBadge(
                    prediction = prediction,
                    color = predictionColor,
                    highlighted = isWinner
                )
            }
            Box(
                modifier = Modifier.weight(CurrentColumnWeight),
                contentAlignment = Alignment.Center
            ) {
                EntryCurrentColumnCell(
                    matchStatus = matchStatus,
                    scoreLabel = currentScoreLabel
                )
            }
            Box(
                modifier = Modifier.weight(StakeColumnWeight),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = amountLabel,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = JackpotGold,
                    maxLines = 1,
                    textAlign = TextAlign.End
                )
            }
        }
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 12.dp),
                color = Divider.copy(alpha = 0.55f)
            )
        }
    }
}

@Composable
private fun EntryPredictionBadge(
    prediction: String,
    color: androidx.compose.ui.graphics.Color,
    highlighted: Boolean = false,
    modifier: Modifier = Modifier
) {
    val borderColor = if (highlighted) {
        JackpotGold.copy(alpha = 0.32f)
    } else {
        Divider.copy(alpha = 0.45f)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MatchCardCompactSurface)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = prediction,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = color,
            maxLines = 1
        )
    }
}

@Composable
private fun EntryCurrentColumnCell(
    matchStatus: MatchStatus,
    scoreLabel: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        MatchStatusBadge(
            status = matchStatus,
            style = MatchStatusBadgeStyle.Compact
        )
        Text(
            text = scoreLabel,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AllFriendsJoinedInfoCard(
    message: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = message,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(PrimaryBlue.copy(alpha = 0.1f))
            .border(1.dp, PrimaryBlue.copy(alpha = 0.24f), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        style = MaterialTheme.typography.bodyMedium,
        color = TextSecondary,
        textAlign = TextAlign.Center
    )
}

@Composable
fun AllFriendsJoinedInfoSheet(
    onDismiss: () -> Unit
) {
    FormBottomSheet(
        title = stringResource(R.string.add_entry),
        onDismiss = onDismiss,
        primaryActionLabel = stringResource(R.string.ok),
        onPrimaryAction = onDismiss,
        showCancel = false
    ) {
        AllFriendsJoinedInfoCard(
            message = stringResource(R.string.add_entry_all_friends_joined),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
