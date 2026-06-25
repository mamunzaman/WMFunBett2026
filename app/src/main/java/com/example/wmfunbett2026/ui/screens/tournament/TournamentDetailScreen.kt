package com.example.wmfunbett2026.ui.screens.tournament

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.wmfunbett2026.R
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.data.model.Day
import com.example.wmfunbett2026.data.model.Game
import com.example.wmfunbett2026.data.model.toEuroLabel
import com.example.wmfunbett2026.data.repository.FunBettRepository
import com.example.wmfunbett2026.ui.components.AddGameDialog
import com.example.wmfunbett2026.ui.components.DeleteConfirmDialog
import com.example.wmfunbett2026.ui.components.HierarchyListContentPadding
import com.example.wmfunbett2026.ui.components.HierarchyScreenLayout
import com.example.wmfunbett2026.ui.components.HierarchySectionHeader
import com.example.wmfunbett2026.ui.components.MatchCenterCard
import com.example.wmfunbett2026.ui.components.MatchCenterEmptyState
import com.example.wmfunbett2026.ui.components.SampleDataNotice
import com.example.wmfunbett2026.ui.components.hierarchyContentPadding
import com.example.wmfunbett2026.ui.matchcenter.resolveHeaderJackpotForRound
import com.example.wmfunbett2026.ui.navigation.HierarchyLabels
import com.example.wmfunbett2026.ui.theme.SecondaryText
import com.example.wmfunbett2026.ui.theme.SurfaceDark
import com.example.wmfunbett2026.ui.theme.WMFunBett2026Theme

@Composable
fun TournamentDetailScreen(
    roundId: String,
    onBackClick: () -> Unit,
    onGameClick: (dayId: String, gameId: String) -> Unit,
    onDeleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    FunBettRepository.dataVersion.intValue
    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(roundId) {
        showAddDialog = false
        showDeleteDialog = false
    }

    val round = FunBettRepository.getRound(roundId)
    val days = FunBettRepository.getDays(roundId)
    val hasGames = days.any { it.games.isNotEmpty() }
    val headerJackpotLabel = remember(roundId, FunBettRepository.dataVersion.intValue) {
        resolveHeaderJackpotForRound(roundId)
    }

    HierarchyScreenLayout(
        title = round?.name ?: "Tournament",
        breadcrumbs = HierarchyLabels.forTournamentDetail(roundId),
        onBackClick = onBackClick,
        onFabClick = if (round != null) {{ showAddDialog = true }} else null,
        fabContentDescription = "Add game",
        onDeleteClick = if (round != null) {{ showDeleteDialog = true }} else null,
        jackpotAmountLabel = headerJackpotLabel,
        modifier = modifier
    ) { contentModifier ->
        if (round == null) {
            LazyColumn(
                modifier = contentModifier.fillMaxSize(),
                contentPadding = HierarchyListContentPadding
            ) {
                item(key = "notice") { SampleDataNotice() }
                item(key = "not_found") {
                    Text(
                        text = "Tournament not found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = SecondaryText
                    )
                }
            }
            return@HierarchyScreenLayout
        }

        LazyColumn(
            modifier = contentModifier.fillMaxSize(),
            contentPadding = hierarchyContentPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(key = "notice") { SampleDataNotice() }
            item(key = "section") { HierarchySectionHeader(title = "Games") }

            if (!hasGames) {
                item(key = "empty") {
                    MatchCenterEmptyState(
                        title = stringResource(R.string.empty_matches_title),
                        message = stringResource(R.string.empty_matches_message)
                    )
                }
            } else {
                days.forEach { day ->
                    if (day.games.isNotEmpty()) {
                        item(key = "day-header-${day.id}") {
                            DayGroupHeader(day = day)
                        }
                        items(
                            items = day.games,
                            key = { game -> "${day.id}-${game.id}" }
                        ) { game ->
                            MatchCenterCard(
                                game = game,
                                matchdayLabel = day.name,
                                onClick = { onGameClick(day.id, game.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddGameDialog(
            onDismiss = { showAddDialog = false },
            onSave = { dayLabel, teamA, teamB, dateLabel, timeLabel ->
                FunBettRepository.addGame(roundId, dayLabel, teamA, teamB, dateLabel, timeLabel)
                showAddDialog = false
            }
        )
    }

    if (showDeleteDialog) {
        DeleteConfirmDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                showDeleteDialog = false
                if (FunBettRepository.deleteRound(roundId)) {
                    onDeleted()
                }
            }
        )
    }
}

@Composable
private fun DayGroupHeader(day: Day, modifier: Modifier = Modifier) {
    HierarchySectionHeader(
        title = day.name,
        modifier = modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
fun TournamentDetailScreenPreview() {
    WMFunBett2026Theme {
        TournamentDetailScreen(
            roundId = FunBettRepository.ROUND_ID,
            onBackClick = {},
            onGameClick = { _, _ -> },
            onDeleted = {}
        )
    }
}
