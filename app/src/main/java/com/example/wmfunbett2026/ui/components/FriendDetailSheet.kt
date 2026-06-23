package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.wmfunbett2026.ui.theme.DangerRed
import com.example.wmfunbett2026.ui.theme.Divider
import com.example.wmfunbett2026.ui.theme.JackpotGold
import com.example.wmfunbett2026.ui.theme.MatchCardCompactSurface
import com.example.wmfunbett2026.ui.theme.TextPrimary
import com.example.wmfunbett2026.ui.theme.TextSecondary

@Composable
fun FriendDetailSheet(
    friendId: String,
    onDismiss: () -> Unit,
    onDeleted: () -> Unit = onDismiss
) {
    FunBettRepository.dataVersion.intValue
    var showEditSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val summary = remember(friendId, FunBettRepository.dataVersion.intValue) {
        FunBettRepository.getFriendFinancialSummary(friendId)
    }
    val entries = remember(friendId, FunBettRepository.dataVersion.intValue) {
        FunBettRepository.getFriendEntries(friendId)
    }

    if (summary == null) return

    if (showEditSheet) {
        EditFriendSheet(
            friendId = friendId,
            onDismiss = { showEditSheet = false },
            onSave = { firstName, lastName, note ->
                if (FunBettRepository.updateFriend(friendId, firstName, lastName, note) != null) {
                    showEditSheet = false
                }
            }
        )
        return
    }

    if (showDeleteDialog) {
        val blockReason = FunBettRepository.getFriendDeleteBlockReason(friendId)
        if (blockReason != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text(stringResource(R.string.delete_friend_confirm_title)) },
                text = {
                    Text(
                        text = stringResource(R.string.delete_friend_has_entries_message),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text(stringResource(R.string.ok))
                    }
                }
            )
        } else {
            DeleteConfirmDialog(
                titleRes = R.string.delete_friend_confirm_title,
                messageRes = R.string.delete_friend_confirm_message,
                onDismiss = { showDeleteDialog = false },
                onConfirm = {
                    showDeleteDialog = false
                    if (FunBettRepository.deleteFriend(friendId)) {
                        onDeleted()
                    }
                }
            )
        }
    }

    FormBottomSheet(
        title = summary.friend.name,
        onDismiss = onDismiss,
        primaryActionLabel = stringResource(R.string.ok),
        onPrimaryAction = onDismiss,
        showCancel = false,
        headerActions = {
            FriendDetailOverflowMenu(
                onEdit = { showEditSheet = true },
                onDelete = { showDeleteDialog = true }
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FriendFinancialSummaryBlock(
                activeEntryCount = summary.activeEntryCount,
                activeAmountTotal = summary.activeAmountTotal
            )

            HorizontalDivider(color = Divider.copy(alpha = 0.45f))

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
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    entries.forEach { item ->
                        FriendEntryHistoryCard(item = item)
                    }
                }
            }
        }
    }
}

@Composable
private fun FriendDetailOverflowMenu(
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                tint = TextSecondary
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.action_edit_friend)) },
                onClick = {
                    expanded = false
                    onEdit()
                }
            )
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.action_delete_friend),
                        color = DangerRed
                    )
                },
                onClick = {
                    expanded = false
                    onDelete()
                }
            )
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
            .background(MatchCardCompactSurface)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = stringResource(R.string.friend_detail_active_entries, activeEntryCount),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
        Text(
            text = stringResource(
                R.string.friend_detail_active_amount,
                activeAmountTotal.toEuroLabel()
            ),
            style = MaterialTheme.typography.titleSmall,
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
            .background(MatchCardCompactSurface)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = item.leagueName,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = item.matchName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Text(
            text = item.tippGroupName,
            style = MaterialTheme.typography.bodySmall,
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
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimary
            )
            Text(
                text = item.amount.toEuroLabel(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = JackpotGold
            )
        }
    }
}
