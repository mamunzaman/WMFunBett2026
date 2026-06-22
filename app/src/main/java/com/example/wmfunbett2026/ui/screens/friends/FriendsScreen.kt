package com.example.wmfunbett2026.ui.screens.friends

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.data.model.FriendWithStats
import com.example.wmfunbett2026.data.model.toEuroLabel
import com.example.wmfunbett2026.data.repository.FunBettRepository
import com.example.wmfunbett2026.ui.components.AddFriendSheet
import com.example.wmfunbett2026.ui.components.FormOutlinedTextField
import com.example.wmfunbett2026.ui.components.FriendDetailSheet
import com.example.wmfunbett2026.ui.components.FriendInitialsAvatar
import com.example.wmfunbett2026.ui.components.MatchCenterBottomNavReservedHeight
import com.example.wmfunbett2026.ui.components.MatchCenterEmptyState
import com.example.wmfunbett2026.ui.components.PremiumCard
import com.example.wmfunbett2026.ui.components.ScreenContentHorizontalPadding
import com.example.wmfunbett2026.ui.components.ScreenContentTopPadding
import com.example.wmfunbett2026.ui.components.friendDisplayInitials
import com.example.wmfunbett2026.ui.theme.BackgroundDeep
import com.example.wmfunbett2026.ui.theme.Divider
import com.example.wmfunbett2026.ui.theme.JackpotGold
import com.example.wmfunbett2026.ui.theme.PremiumHeaderGradient
import com.example.wmfunbett2026.ui.theme.PrimaryBlue
import com.example.wmfunbett2026.ui.theme.TextPrimary
import com.example.wmfunbett2026.ui.theme.TextSecondary
import kotlinx.coroutines.delay

private enum class FriendsViewMode { Grid, List }

private enum class FriendsSortMode { NameAsc }

private const val FriendsEntranceDurationMs = 400
private const val FriendsEntranceStaggerMs = 85
private val FriendsEntranceOffset = 32.dp
private const val FriendsCardStaggerStart = 3

@Composable
fun FriendsScreen(modifier: Modifier = Modifier) {
    FunBettRepository.dataVersion.intValue
    var showAddFriendSheet by remember { mutableStateOf(false) }
    var selectedFriendId by remember { mutableStateOf<String?>(null) }
    var searchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var viewMode by remember { mutableStateOf(FriendsViewMode.Grid) }
    var sortMode by remember { mutableStateOf(FriendsSortMode.NameAsc) }

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
        FriendsScreenHeader(
            searchActive = searchActive,
            onSearchClick = {
                searchActive = !searchActive
                if (!searchActive) searchQuery = ""
            },
            onAddFriendClick = { showAddFriendSheet = true }
        )

        AnimatedVisibility(
            visible = searchActive,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PremiumHeaderGradient)
                    .padding(horizontal = ScreenContentHorizontalPadding)
                    .padding(top = 4.dp, bottom = 16.dp),
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
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = ScreenContentHorizontalPadding,
                    end = ScreenContentHorizontalPadding,
                    top = ScreenContentTopPadding + 4.dp,
                    bottom = MatchCenterBottomNavReservedHeight
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item(key = "controls") {
                    FriendsControlsRow(
                        friendCount = displayedFriends.size,
                        totalFriendCount = overview.first,
                        viewMode = viewMode,
                        onViewModeChange = { viewMode = it },
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                item(key = "overview") {
                    FriendsOverviewSummaryRow(
                        friendCount = overview.first,
                        activeEntryCount = overview.second,
                        activeAmountTotal = overview.third,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
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
                            item(key = "grid_row_$rowIndex") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    rowItems.forEach { item ->
                                        val cardIndex = displayedFriends.indexOfFirst {
                                            it.friend.id == item.friend.id
                                        }
                                        FriendGridCard(
                                            item = item,
                                            onClick = { selectedFriendId = item.friend.id },
                                            staggerIndex = FriendsCardStaggerStart + cardIndex,
                                            modifier = Modifier.weight(1f)
                                        )
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
                            key = { _, item -> item.friend.id }
                        ) { index, item ->
                            FriendListCard(
                                item = item,
                                onClick = { selectedFriendId = item.friend.id },
                                staggerIndex = FriendsCardStaggerStart + index
                            )
                        }
                    }
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

    selectedFriendId?.let { friendId ->
        FriendDetailSheet(
            friendId = friendId,
            onDismiss = { selectedFriendId = null }
        )
    }
}

@Composable
private fun FriendsEntranceHost(
    staggerIndex: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var animateIn by remember(staggerIndex) { mutableStateOf(false) }
    val startOffsetPx = with(LocalDensity.current) { FriendsEntranceOffset.toPx() }

    LaunchedEffect(staggerIndex) {
        animateIn = false
        delay(staggerIndex * FriendsEntranceStaggerMs.toLong())
        animateIn = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0f,
        animationSpec = tween(
            durationMillis = FriendsEntranceDurationMs,
            easing = FastOutSlowInEasing
        ),
        label = "friendsEntranceAlpha"
    )
    val offsetYPx by animateFloatAsState(
        targetValue = if (animateIn) 0f else startOffsetPx,
        animationSpec = tween(
            durationMillis = FriendsEntranceDurationMs,
            easing = FastOutSlowInEasing
        ),
        label = "friendsEntranceOffset"
    )

    Box(
        modifier = modifier.graphicsLayer {
            this.alpha = alpha
            translationY = offsetYPx
        }
    ) {
        content()
    }
}

@Composable
private fun FriendsScreenHeader(
    searchActive: Boolean,
    onSearchClick: () -> Unit,
    onAddFriendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(PremiumHeaderGradient)
            .statusBarsPadding()
            .padding(horizontal = ScreenContentHorizontalPadding, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.screen_friends),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.friends_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.search),
                    tint = if (searchActive) PrimaryBlue else TextPrimary
                )
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue)
                    .clickable(onClick = onAddFriendClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = stringResource(R.string.add_friend),
                    tint = TextPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
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
    val countLabel = if (searchActiveCountDiffers(friendCount, totalFriendCount)) {
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
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FriendsSortChip()
            FriendsViewToggleButton(
                selected = viewMode == FriendsViewMode.Grid,
                icon = Icons.Default.GridView,
                contentDescription = stringResource(R.string.friends_view_grid),
                onClick = { onViewModeChange(FriendsViewMode.Grid) }
            )
            FriendsViewToggleButton(
                selected = viewMode == FriendsViewMode.List,
                icon = Icons.Default.ViewList,
                contentDescription = stringResource(R.string.friends_view_list),
                onClick = { onViewModeChange(FriendsViewMode.List) }
            )
        }
    }
}

private fun searchActiveCountDiffers(displayed: Int, total: Int): Boolean =
    displayed != total

@Composable
private fun FriendsSortChip(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(PrimaryBlue.copy(alpha = 0.22f))
            .border(1.dp, PrimaryBlue, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.SortByAlpha,
            contentDescription = stringResource(R.string.friends_sort_name_az),
            tint = PrimaryBlue,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = stringResource(R.string.friends_sort_chip_az),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryBlue
        )
    }
}

@Composable
private fun FriendsViewToggleButton(
    selected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val background = if (selected) PrimaryBlue.copy(alpha = 0.22f) else Color.Transparent
    val borderColor = if (selected) PrimaryBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)

    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (selected) PrimaryBlue else TextSecondary,
            modifier = Modifier.size(20.dp)
        )
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
        Button(
            onClick = onAddFriendClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
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
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FriendOverviewSummaryCard(
            label = stringResource(R.string.friends_summary_friends),
            value = friendCount.toString(),
            staggerIndex = 0,
            modifier = Modifier.weight(1f)
        )
        FriendOverviewSummaryCard(
            label = stringResource(R.string.friends_summary_entries),
            value = activeEntryCount.toString(),
            staggerIndex = 1,
            modifier = Modifier.weight(1f)
        )
        FriendOverviewSummaryCard(
            label = stringResource(R.string.friends_summary_amount),
            value = activeAmountTotal.toEuroLabel(),
            valueColor = JackpotGold,
            staggerIndex = 2,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun FriendOverviewSummaryCard(
    label: String,
    value: String,
    staggerIndex: Int,
    modifier: Modifier = Modifier,
    valueColor: Color = TextPrimary
) {
    FriendsEntranceHost(staggerIndex = staggerIndex, modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            PrimaryBlue.copy(alpha = 0.16f),
                            PrimaryBlue.copy(alpha = 0.08f)
                        )
                    )
                )
                .border(1.dp, PrimaryBlue.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                .padding(horizontal = 10.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = valueColor,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun FriendGridCard(
    item: FriendWithStats,
    onClick: () -> Unit,
    staggerIndex: Int,
    modifier: Modifier = Modifier
) {
    val friend = item.friend
    val cardGradient = Brush.verticalGradient(
        colors = listOf(
            PrimaryBlue.copy(alpha = 0.1f),
            Color.Transparent
        )
    )

    FriendsEntranceHost(staggerIndex = staggerIndex, modifier = modifier) {
        PremiumCard(
            onClick = onClick,
            gradient = cardGradient,
            borderColor = PrimaryBlue.copy(alpha = 0.24f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FriendInitialsAvatar(
                    initials = friendDisplayInitials(friend.name),
                    size = 52.dp
                )
                Text(
                    text = friend.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(R.string.friend_grid_entries, item.activeEntryCount),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = item.activeAmountTotal.toEuroLabel(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = JackpotGold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun FriendListCard(
    item: FriendWithStats,
    onClick: () -> Unit,
    staggerIndex: Int,
    modifier: Modifier = Modifier
) {
    val friend = item.friend

    FriendsEntranceHost(staggerIndex = staggerIndex, modifier = modifier.fillMaxWidth()) {
        PremiumCard(
            onClick = onClick,
            borderColor = Divider.copy(alpha = 0.85f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                FriendInitialsAvatar(
                    initials = friendDisplayInitials(friend.name),
                    size = 50.dp
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = friend.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = stringResource(R.string.friend_active_entries, item.activeEntryCount),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                Text(
                    text = item.activeAmountTotal.toEuroLabel(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = JackpotGold
                )
            }
        }
    }
}
