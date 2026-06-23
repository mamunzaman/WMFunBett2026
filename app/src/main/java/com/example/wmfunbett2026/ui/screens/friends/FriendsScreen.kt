package com.example.wmfunbett2026.ui.screens.friends

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.data.model.toEuroLabel
import com.example.wmfunbett2026.data.repository.FunBettRepository
import com.example.wmfunbett2026.ui.components.AddFriendSheet
import com.example.wmfunbett2026.ui.components.FormOutlinedTextField
import com.example.wmfunbett2026.ui.components.FriendDetailSheet
import com.example.wmfunbett2026.ui.components.FriendGridCard
import com.example.wmfunbett2026.ui.components.FriendListRow
import com.example.wmfunbett2026.ui.components.FriendOverviewStatCard
import com.example.wmfunbett2026.ui.components.FriendsContentStaggerStart
import com.example.wmfunbett2026.ui.components.FriendsEntranceHost
import com.example.wmfunbett2026.ui.components.FriendsToolbarButton
import com.example.wmfunbett2026.ui.components.FriendsToolbarIconButton
import com.example.wmfunbett2026.ui.components.MatchCenterBottomNavReservedHeight
import com.example.wmfunbett2026.ui.components.MatchCenterEmptyState
import com.example.wmfunbett2026.ui.components.MatchCenterHeader
import com.example.wmfunbett2026.ui.components.ScreenContentHorizontalPadding
import com.example.wmfunbett2026.ui.components.screenContentPadding
import com.example.wmfunbett2026.ui.theme.BackgroundDeep
import com.example.wmfunbett2026.ui.theme.JackpotGold
import com.example.wmfunbett2026.ui.theme.PrimaryBlue
import com.example.wmfunbett2026.ui.theme.TextPrimary
import com.example.wmfunbett2026.ui.theme.TextSecondary

private enum class FriendsViewMode { Grid, List }

private enum class FriendsSortMode { NameAsc }

@Composable
fun FriendsScreen(modifier: Modifier = Modifier) {
    FunBettRepository.dataVersion.intValue
    var showAddFriendSheet by remember { mutableStateOf(false) }
    var selectedFriendId by remember { mutableStateOf<String?>(null) }
    var searchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var viewMode by remember { mutableStateOf(FriendsViewMode.Grid) }
    var sortMode by remember { mutableStateOf(FriendsSortMode.NameAsc) }
    var entranceSession by remember { mutableIntStateOf(0) }

    LaunchedEffect(viewMode) {
        entranceSession++
    }

    val friendsWithStats = remember(FunBettRepository.dataVersion.intValue) {
        FunBettRepository.getFriendsWithStats()
    }
    val displayedFriends = remember(friendsWithStats, searchQuery, sortMode) {
        friendsWithStats
            .filter { friend ->
                searchQuery.isBlank() ||
                    friend.friend.name.contains(searchQuery.trim(), ignoreCase = true)
            }
            .let { list ->
                when (sortMode) {
                    FriendsSortMode.NameAsc -> list.sortedBy { it.friend.name.lowercase() }
                }
            }
    }
    val overview = remember(friendsWithStats) {
        Triple(
            friendsWithStats.size,
            friendsWithStats.sumOf { it.activeEntryCount },
            friendsWithStats.sumOf { it.activeAmountTotal }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDeep)
    ) {
        MatchCenterHeader(
            title = stringResource(R.string.screen_friends),
            trailingContent = {
                IconButton(onClick = {
                    searchActive = !searchActive
                    if (!searchActive) searchQuery = ""
                }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.search),
                        tint = if (searchActive) PrimaryBlue else TextPrimary
                    )
                }
                IconButton(onClick = { showAddFriendSheet = true }) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = stringResource(R.string.add_friend),
                        tint = TextPrimary
                    )
                }
            }
        )

        AnimatedVisibility(
            visible = searchActive,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ScreenContentHorizontalPadding)
                    .padding(top = 8.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FormOutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    label = { Text(stringResource(R.string.search)) },
                    singleLine = true
                )
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.friends_search_clear),
                            tint = TextSecondary
                        )
                    }
                }
            }
        }

        if (friendsWithStats.isEmpty()) {
            FriendsEmptyContent(onAddFriendClick = { showAddFriendSheet = true })
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundDeep)
                        .padding(horizontal = ScreenContentHorizontalPadding)
                        .padding(top = 12.dp, bottom = 8.dp)
                ) {
                    FriendsControlsRow(
                        friendCount = displayedFriends.size,
                        totalFriendCount = overview.first,
                        viewMode = viewMode,
                        onViewModeChange = { viewMode = it }
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(
                        start = ScreenContentHorizontalPadding,
                        end = ScreenContentHorizontalPadding,
                        bottom = MatchCenterBottomNavReservedHeight
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item(key = "overview") {
                        FriendsEntranceHost(
                            staggerIndex = 0,
                            entranceSession = entranceSession,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            FriendsOverviewSummaryRow(
                                friendCount = overview.first,
                                activeEntryCount = overview.second,
                                activeAmountTotal = overview.third
                            )
                        }
                    }

                    if (displayedFriends.isEmpty()) {
                        item(key = "search_empty") {
                            MatchCenterEmptyState(
                                title = stringResource(R.string.friends_search_no_results_title),
                                message = stringResource(R.string.friends_search_no_results_message),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else when (viewMode) {
                        FriendsViewMode.Grid -> {
                            displayedFriends.chunked(2).forEachIndexed { rowIndex, rowItems ->
                                item(key = "grid_${viewMode.name}_${entranceSession}_$rowIndex") {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        rowItems.forEach { item ->
                                            val cardIndex = displayedFriends.indexOfFirst {
                                                it.friend.id == item.friend.id
                                            }
                                            FriendsEntranceHost(
                                                staggerIndex = FriendsContentStaggerStart + cardIndex,
                                                entranceSession = entranceSession,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                FriendGridCard(
                                                    item = item,
                                                    onClick = { selectedFriendId = item.friend.id },
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                            }
                                        }
                                        if (rowItems.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                        FriendsViewMode.List -> {
                            itemsIndexed(
                                items = displayedFriends,
                                key = { _, item -> "list_${viewMode.name}_${entranceSession}_${item.friend.id}" }
                            ) { index, item ->
                                FriendsEntranceHost(
                                    staggerIndex = FriendsContentStaggerStart + index,
                                    entranceSession = entranceSession,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    FriendListRow(
                                        item = item,
                                        onClick = { selectedFriendId = item.friend.id }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddFriendSheet) {
        AddFriendSheet(
            onDismiss = { showAddFriendSheet = false },
            onCreate = { firstName, lastName, note ->
                if (FunBettRepository.addFriend(firstName, lastName, note) != null) {
                    showAddFriendSheet = false
                }
            }
        )
    }

    selectedFriendId?.let { friendId ->
        FriendDetailSheet(
            friendId = friendId,
            onDismiss = { selectedFriendId = null },
            onDeleted = { selectedFriendId = null }
        )
    }
}

@Composable
private fun FriendsControlsRow(
    friendCount: Int,
    totalFriendCount: Int,
    viewMode: FriendsViewMode,
    onViewModeChange: (FriendsViewMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val countLabel = if (friendCount != totalFriendCount) {
        stringResource(R.string.friends_count_filtered, friendCount, totalFriendCount)
    } else {
        stringResource(R.string.friends_count_label, friendCount)
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = countLabel,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FriendsToolbarButton(
                selected = true,
                onClick = {}
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.SortByAlpha,
                        contentDescription = stringResource(R.string.friends_sort_name_az),
                        tint = PrimaryBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = stringResource(R.string.friends_sort_chip_az),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = PrimaryBlue
                    )
                }
            }
            FriendsToolbarIconButton(
                selected = viewMode == FriendsViewMode.Grid,
                onClick = { onViewModeChange(FriendsViewMode.Grid) },
                icon = Icons.Default.GridView,
                contentDescription = stringResource(R.string.friends_view_grid)
            )
            FriendsToolbarIconButton(
                selected = viewMode == FriendsViewMode.List,
                onClick = { onViewModeChange(FriendsViewMode.List) },
                icon = Icons.Default.ViewList,
                contentDescription = stringResource(R.string.friends_view_list)
            )
        }
    }
}

@Composable
private fun FriendsEmptyContent(
    onAddFriendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(screenContentPadding()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MatchCenterEmptyState(
            title = stringResource(R.string.friends_empty_title),
            message = stringResource(R.string.friends_empty_message),
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onAddFriendClick,
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryBlue,
                contentColor = TextPrimary
            )
        ) {
            Text(stringResource(R.string.add_friend))
        }
    }
}

@Composable
private fun FriendsOverviewSummaryRow(
    friendCount: Int,
    activeEntryCount: Int,
    activeAmountTotal: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FriendOverviewStatCard(
            label = stringResource(R.string.friends_summary_friends),
            value = friendCount.toString(),
            modifier = Modifier.weight(1f)
        )
        FriendOverviewStatCard(
            label = stringResource(R.string.friends_summary_entries),
            value = activeEntryCount.toString(),
            modifier = Modifier.weight(1f)
        )
        FriendOverviewStatCard(
            label = stringResource(R.string.friends_summary_amount),
            value = activeAmountTotal.toEuroLabel(),
            valueColor = JackpotGold,
            modifier = Modifier.weight(1f)
        )
    }
}
