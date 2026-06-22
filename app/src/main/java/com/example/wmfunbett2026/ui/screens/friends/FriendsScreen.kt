package com.example.wmfunbett2026.ui.screens.friends

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.wmfunbett2026.data.repository.FunBettRepository
import com.example.wmfunbett2026.ui.components.MatchCenterEmptyState
import com.example.wmfunbett2026.ui.components.MatchCenterHeader
import com.example.wmfunbett2026.ui.components.PremiumCard
import com.example.wmfunbett2026.ui.components.ScreenContentHorizontalPadding
import com.example.wmfunbett2026.ui.components.ScreenContentTopPadding
import com.example.wmfunbett2026.ui.components.FriendDetailSheet
import com.example.wmfunbett2026.ui.components.screenContentPadding
import com.example.wmfunbett2026.ui.matchcenter.FriendSummary
import com.example.wmfunbett2026.ui.matchcenter.loadFriendSummaries
import com.example.wmfunbett2026.ui.matchcenter.totalTippedLabel
import com.example.wmfunbett2026.ui.theme.BackgroundDeep
import com.example.wmfunbett2026.ui.theme.TextPrimary
import com.example.wmfunbett2026.ui.theme.TextSecondary

@Composable
fun FriendsScreen(modifier: Modifier = Modifier) {
    FunBettRepository.dataVersion.intValue
    val friends = remember(FunBettRepository.dataVersion.intValue) { loadFriendSummaries() }
    var selectedFriend by remember { mutableStateOf<FriendSummary?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDeep)
    ) {
        MatchCenterHeader(title = stringResource(R.string.screen_friends))
        if (friends.isEmpty()) {
            MatchCenterEmptyState(
                title = stringResource(R.string.friends_empty_title),
                message = stringResource(R.string.friends_empty_message),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = ScreenContentHorizontalPadding,
                        vertical = ScreenContentTopPadding
                    )
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = screenContentPadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item(key = "intro") {
                    Text(
                        text = stringResource(R.string.friends_intro),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                items(friends, key = { it.name }) { friend ->
                    FriendCard(
                        friend = friend,
                        onClick = { selectedFriend = friend }
                    )
                }
            }
        }
    }

    selectedFriend?.let { friend ->
        FriendDetailSheet(
            friend = friend,
            onDismiss = { selectedFriend = null }
        )
    }
}

@Composable
private fun FriendCard(
    friend: FriendSummary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    PremiumCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = friend.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.friend_matches_count, friend.joinedMatches),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Text(
                    text = stringResource(R.string.friend_total_tipped, friend.totalTippedLabel()),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
            Text(
                text = stringResource(R.string.friend_wins_placeholder, friend.winsPlaceholder),
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
        }
    }
}
