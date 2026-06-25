package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
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
import com.example.wmfunbett2026.ui.matchcenter.FlatGameItem
import com.example.wmfunbett2026.ui.matchcenter.MatchSelectFilter
import com.example.wmfunbett2026.ui.matchcenter.MatchTimeQuickFilter
import com.example.wmfunbett2026.ui.matchcenter.applyMatchQuickFilters
import com.example.wmfunbett2026.ui.matchcenter.filterMatches
import com.example.wmfunbett2026.ui.matchcenter.groupMatchesBySection
import com.example.wmfunbett2026.ui.matchcenter.resolveHeaderJackpotAmountLabel
import com.example.wmfunbett2026.ui.matchcenter.loadLeagueSummaries
import com.example.wmfunbett2026.ui.theme.BackgroundDeep
import com.example.wmfunbett2026.ui.theme.TextPrimary
import java.time.format.DateTimeFormatter
import java.util.Locale

private val MatchEditDayFormatter =
    DateTimeFormatter.ofPattern("EEE, d MMM yyyy", Locale.getDefault())
private val MatchEditTimeFormatter =
    DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())

@Composable
fun MatchListScreenContent(
    title: String,
    games: List<FlatGameItem>,
    onGameClick: (FlatGameItem) -> Unit,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    onGameDeleted: ((String) -> Unit)? = null,
    showLiveAction: Boolean = false,
    showQuickFilters: Boolean = false,
    animateEntrance: Boolean = false,
    entranceSession: Int = 0,
    emptyTitle: String? = null,
    emptyMessage: String? = null
) {
    val defaultEmptyTitle = stringResource(R.string.empty_matches_title)
    val defaultEmptyMessage = stringResource(R.string.empty_matches_message)
    val resolvedEmptyTitle = emptyTitle ?: defaultEmptyTitle
    val resolvedEmptyMessage = emptyMessage ?: defaultEmptyMessage

    var liveOnlyActive by remember { mutableStateOf(false) }
    var selectFilter by remember { mutableStateOf(MatchSelectFilter.ALL_MATCHES) }
    var showSelectSheet by remember { mutableStateOf(false) }
    var timeQuickFilter by remember { mutableStateOf<MatchTimeQuickFilter?>(null) }
    var leagueQuickFilterId by remember { mutableStateOf<String?>(null) }
    var matchActionTarget by remember { mutableStateOf<FlatGameItem?>(null) }
    var editMatchTarget by remember { mutableStateOf<FlatGameItem?>(null) }
    var showMatchActionSheet by remember { mutableStateOf(false) }
    var showEditMatchSheet by remember { mutableStateOf(false) }
    var deletingMatch by remember { mutableStateOf<FlatGameItem?>(null) }

    val headerFiltered = remember(games, liveOnlyActive, selectFilter) {
        filterMatches(games, liveOnlyActive, selectFilter)
    }
    val filtered = remember(headerFiltered, timeQuickFilter, leagueQuickFilterId) {
        applyMatchQuickFilters(headerFiltered, timeQuickFilter, leagueQuickFilterId)
    }
    val grouped = remember(filtered) { groupMatchesBySection(filtered) }
    val cardStaggerIndices = remember(grouped) {
        val indices = linkedMapOf<String, Int>()
        var index = 0
        grouped.values.flatten().forEach { item ->
            indices["${item.roundId}-${item.dayId}-${item.game.id}"] = index++
        }
        indices
    }
    val calendarFilterActive = !liveOnlyActive && selectFilter != MatchSelectFilter.ALL_MATCHES

    val jackpotAmountLabel = remember(games, FunBettRepository.dataVersion.intValue) {
        resolveHeaderJackpotAmountLabel(games)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDeep)
    ) {
        MatchCenterHeader(
            title = title,
            onBackClick = onBackClick,
            jackpotAmountLabel = jackpotAmountLabel,
            trailingContent = {
                MatchesHeaderActions(
                    liveFilterActive = liveOnlyActive,
                    calendarFilterActive = calendarFilterActive,
                    onLiveFilterClick = {
                        liveOnlyActive = !liveOnlyActive
                        if (liveOnlyActive) {
                            selectFilter = MatchSelectFilter.ALL_MATCHES
                        }
                    },
                    onCalendarClick = { showSelectSheet = true },
                    onSearchClick = { },
                    showLivePill = showLiveAction
                )
            }
        )

        if (showQuickFilters) {
            MatchQuickFilterRail(
                selectedTimeFilter = timeQuickFilter,
                selectedLeagueFilterId = leagueQuickFilterId,
                onTimeFilterClick = { filter ->
                    timeQuickFilter = if (timeQuickFilter == filter) null else filter
                },
                onLeagueFilterClick = { leagueId ->
                    leagueQuickFilterId = if (leagueQuickFilterId == leagueId) null else leagueId
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = ScreenContentTopPadding, bottom = 12.dp)
            )
        }

        if (filtered.isEmpty()) {
            val (emptyTitleText, emptyMessageText) = when {
                liveOnlyActive || timeQuickFilter == MatchTimeQuickFilter.LIVE ->
                    stringResource(R.string.empty_live_title) to
                        stringResource(R.string.empty_live_message)
                else -> resolvedEmptyTitle to resolvedEmptyMessage
            }
            MatchCenterEmptyState(
                title = emptyTitleText,
                message = emptyMessageText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = ScreenContentHorizontalPadding,
                        vertical = if (showQuickFilters) 8.dp else ScreenContentTopPadding
                    )
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = if (showQuickFilters) {
                    PaddingValues(
                        start = ScreenContentHorizontalPadding,
                        end = ScreenContentHorizontalPadding,
                        top = 0.dp,
                        bottom = MatchCenterBottomNavReservedHeight
                    )
                } else {
                    screenContentPadding()
                },
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                grouped.forEach { (section, sectionGames) ->
                    item(key = "header-${section.name}") {
                        Text(
                            text = stringResource(section.titleRes),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                        )
                    }
                    items(sectionGames, key = { "${it.roundId}-${it.dayId}-${it.game.id}" }) { item ->
                        val itemKey = "${item.roundId}-${item.dayId}-${item.game.id}"
                        MatchCenterCard(
                            game = item.game,
                            matchdayLabel = item.dayName,
                            onClick = { onGameClick(item) },
                            onLongClick = {
                                matchActionTarget = item
                                showMatchActionSheet = true
                            },
                            staggerIndex = cardStaggerIndices[itemKey] ?: 0,
                            entranceSession = entranceSession,
                            animateEntrance = animateEntrance
                        )
                    }
                }
            }
        }
    }

    if (showSelectSheet) {
        MatchSelectSheet(
            selected = if (liveOnlyActive) MatchSelectFilter.LIVE else selectFilter,
            onSelect = { option ->
                when (option) {
                    MatchSelectFilter.LIVE -> {
                        liveOnlyActive = true
                        selectFilter = MatchSelectFilter.ALL_MATCHES
                    }
                    else -> {
                        liveOnlyActive = false
                        selectFilter = option
                    }
                }
            },
            onDismiss = { showSelectSheet = false }
        )
    }

    if (showMatchActionSheet && matchActionTarget != null) {
        FormActionMenuSheet(
            title = stringResource(R.string.match_actions_title),
            onDismiss = {
                showMatchActionSheet = false
                matchActionTarget = null
            },
            actions = listOf(
                FormActionMenuItem(
                    label = stringResource(R.string.action_edit_match),
                    onClick = {
                        editMatchTarget = matchActionTarget
                        showEditMatchSheet = true
                    }
                ),
                FormActionMenuItem(
                    label = stringResource(R.string.action_delete_match),
                    destructive = true,
                    onClick = {
                        deletingMatch = matchActionTarget
                    }
                )
            )
        )
    }

    if (showEditMatchSheet && editMatchTarget != null) {
        val target = editMatchTarget!!
        val lockedLeagueId = loadLeagueSummaries().find { it.roundId == target.roundId }?.id
        AddMatchSheet(
            lockedLeagueId = lockedLeagueId,
            editTarget = EditMatchSheetTarget(
                gameId = target.game.id,
                roundId = target.roundId,
                dayName = target.dayName
            ),
            onDismiss = {
                showEditMatchSheet = false
                editMatchTarget = null
            },
            onCreate = { _, _, _, _, _, _, _ -> },
            onSave = { gameId, roundId, _, _, teamA, teamB, day, time, note ->
                FunBettRepository.updateGame(
                    gameId = gameId,
                    roundId = roundId,
                    dayLabel = day.format(MatchEditDayFormatter),
                    teamA = teamA,
                    teamB = teamB,
                    dateLabel = day.format(MatchEditDayFormatter),
                    timeLabel = time.format(MatchEditTimeFormatter),
                    note = note
                )
                showEditMatchSheet = false
                editMatchTarget = null
            }
        )
    }

    deletingMatch?.let { target ->
        DeleteConfirmDialog(
            titleRes = R.string.delete_match_confirm_title,
            messageRes = R.string.delete_match_confirm_message,
            onDismiss = { deletingMatch = null },
            onConfirm = {
                val gameId = target.game.id
                if (FunBettRepository.deleteGame(gameId)) {
                    onGameDeleted?.invoke(gameId)
                }
                deletingMatch = null
            }
        )
    }
}
