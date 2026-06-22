package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.ui.matchcenter.FriendSummary
import com.example.wmfunbett2026.ui.matchcenter.totalTippedLabel
import com.example.wmfunbett2026.ui.theme.TextPrimary
import com.example.wmfunbett2026.ui.theme.TextSecondary

@Composable
fun FriendDetailSheet(
    friend: FriendSummary,
    onDismiss: () -> Unit
) {
    FormBottomSheet(
        title = friend.name,
        onDismiss = onDismiss,
        primaryActionLabel = stringResource(R.string.ok),
        onPrimaryAction = onDismiss,
        showCancel = false
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = stringResource(R.string.friend_detail_matches, friend.joinedMatches),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            Text(
                text = stringResource(R.string.friend_detail_tipped, friend.totalTippedLabel()),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            Text(
                text = stringResource(R.string.friend_detail_wins, friend.winsPlaceholder),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            Text(
                text = stringResource(R.string.friend_detail_from_entries),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}
