package com.example.wmfunbett2026.ui.screens.friends

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.data.model.FriendWithStats
import com.example.wmfunbett2026.data.model.toEuroLabel
import com.example.wmfunbett2026.data.repository.FunBettRepository
import com.example.wmfunbett2026.ui.components.AddFriendSheet
import com.example.wmfunbett2026.ui.components.MatchCenterEmptyState
import com.example.wmfunbett2026.ui.components.MatchCenterHeader
import com.example.wmfunbett2026.ui.components.PremiumCard
import com.example.wmfunbett2026.ui.components.ScreenContentHorizontalPadding
import com.example.wmfunbett2026.ui.components.ScreenContentTopPadding
import com.example.wmfunbett2026.ui.components.screenContentPadding
import com.example.wmfunbett2026.ui.theme.BackgroundDeep
import com.example.wmfunbett2026.ui.theme.TextPrimary
import com.example.wmfunbett2026.ui.theme.TextSecondary

@Composable
fun FriendsScreen(modifier: Modifier = Modifier) {
    FunBettRepository.dataVersion.intValue
    var showAddFriendSheet by remember { mutableStateOf(false) }
    val friendsWithStats = remember(FunBettRepository.dataVersion.intValue) {
        FunBettRepository.getFriendsWithStats()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDeep)
    ) {
        MatchCenterHeader(
            title = stringResource(R.string.screen_friends),
            trailingContent = {
                TextButton(onClick = { showAddFriendSheet = true }) {
                    Text(
                        text = stringResource(R.string.add_friend),
                        color = TextPrimary
                    )
                }
            }
        )
        if (friendsWithStats.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = ScreenContentHorizontalPadding,
                        vertical = ScreenContentTopPadding
                    ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MatchCenterEmptyState(
                    title = stringResource(R.string.friends_empty_title),
                    message = stringResource(R.string.friends_empty_message),
                    modifier = Modifier.fillMaxWidth()
                )
                TextButton(
                    onClick = { showAddFriendSheet = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.add_friend))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = screenContentPadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item(key = "count") {
                    Text(
                        text = stringResource(R.string.friends_count, friendsWithStats.size),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                items(friendsWithStats, key = { it.friend.id }) { item ->
                    FriendListCard(item = item)
                }
            }
        }
    }

    if (showAddFriendSheet) {
        AddFriendSheet(
            onDismiss = { showAddFriendSheet = false },
            onCreate = { name, note ->
                if (FunBettRepository.addFriend(name, note) != null) {
                    showAddFriendSheet = false
                }
            }
        )
    }
}

@Composable
private fun FriendListCard(
    item: FriendWithStats,
    modifier: Modifier = Modifier
) {
    val friend = item.friend
    PremiumCard(modifier = modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = friend.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            friend.note?.takeIf { it.isNotBlank() }?.let { note ->
                Text(
                    text = note,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.friend_active_entries, item.activeEntryCount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Text(
                    text = stringResource(
                        R.string.friend_active_amount,
                        item.activeAmountTotal.toEuroLabel()
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
    }
}
