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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.data.model.FriendEntryHistoryItem
import com.example.wmfunbett2026.data.model.toEuroLabel
import com.example.wmfunbett2026.data.repository.FunBettRepository
import com.example.wmfunbett2026.ui.theme.JackpotGold
import com.example.wmfunbett2026.ui.theme.PrimaryBlue
import com.example.wmfunbett2026.ui.theme.TextPrimary
import com.example.wmfunbett2026.ui.theme.TextSecondary

@Composable
fun FriendDetailSheet(
    friendId: String,
    onDismiss: () -> Unit
) {
    FunBettRepository.dataVersion.intValue
    val summary = remember(friendId, FunBettRepository.dataVersion.intValue) {
        FunBettRepository.getFriendFinancialSummary(friendId)
    }
    val entries = remember(friendId, FunBettRepository.dataVersion.intValue) {
        FunBettRepository.getFriendEntries(friendId)
    }

    if (summary == null) return

    FormBottomSheet(
        title = summary.friend.name,
        onDismiss = onDismiss,
        primaryActionLabel = stringResource(R.string.ok),
        onPrimaryAction = onDismiss,
        showCancel = false
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FriendFinancialSummaryBlock(
                activeEntryCount = summary.activeEntryCount,
                activeAmountTotal = summary.activeAmountTotal
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))

            Text(
                text = stringResource(R.string.friend_detail_entry_history),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            if (entries.isEmpty()) {
                MatchCenterEmptyState(
                    title = stringResource(R.string.friend_detail_no_entries_title),
                    message = stringResource(R.string.friend_detail_no_entries_message),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    entries.forEach { item ->
                        FriendEntryHistoryCard(item = item)
                    }
                }
            }
        }
    }
}

@Composable
private fun FriendFinancialSummaryBlock(
    activeEntryCount: Int,
    activeAmountTotal: Double,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(PrimaryBlue.copy(alpha = 0.12f))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.friend_detail_active_entries, activeEntryCount),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
        Text(
            text = stringResource(
                R.string.friend_detail_active_amount,
                activeAmountTotal.toEuroLabel()
            ),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = JackpotGold
        )
    }
}

@Composable
private fun FriendEntryHistoryCard(
    item: FriendEntryHistoryItem,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = item.leagueName,
            style = MaterialTheme.typography.labelMedium,
            color = PrimaryBlue,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = item.matchName,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = item.tippGroupName,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.friend_history_prediction, item.prediction),
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
            Text(
                text = item.amount.toEuroLabel(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = JackpotGold
            )
        }
    }
}
