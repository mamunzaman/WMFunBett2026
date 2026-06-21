package com.example.wmfunbett2026.ui.screens.tournament

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.data.model.toEuroLabel
import com.example.wmfunbett2026.data.repository.FunBettRepository
import com.example.wmfunbett2026.ui.components.AddTournamentDialog
import com.example.wmfunbett2026.ui.components.HierarchyScreenLayout
import com.example.wmfunbett2026.ui.components.HierarchySectionHeader
import com.example.wmfunbett2026.ui.components.MatchCenterCard
import com.example.wmfunbett2026.ui.components.MatchCenterDashboard
import com.example.wmfunbett2026.ui.components.MatchCenterEmptyState
import com.example.wmfunbett2026.ui.components.TournamentCard
import com.example.wmfunbett2026.ui.components.hierarchyContentPadding
import com.example.wmfunbett2026.ui.matchcenter.countActiveGames
import com.example.wmfunbett2026.ui.matchcenter.countOpenRounds
import com.example.wmfunbett2026.ui.matchcenter.flattenAllGames
import com.example.wmfunbett2026.ui.theme.TextPrimary
import com.example.wmfunbett2026.ui.theme.WMFunBett2026Theme

@Composable
fun TournamentScreen(
    onTournamentClick: (String) -> Unit,
    onGameClick: (roundId: String, dayId: String, gameId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    FunBettRepository.dataVersion.intValue
    var showAddDialog by remember { mutableStateOf(false) }

    val rounds = FunBettRepository.getRounds()
    val flatGames = remember(rounds) { flattenAllGames(rounds) }
    val kassePreview = FunBettRepository.getTotalKassePreview().toEuroLabel()
    val openRounds = remember(rounds) { countOpenRounds(rounds) }
    val activeGames = remember(rounds) { countActiveGames(rounds) }

    HierarchyScreenLayout(
        title = stringResource(R.string.app_name),
        breadcrumbs = emptyList(),
        onBackClick = null,
        onFabClick = { showAddDialog = true },
        showSearchIcon = true,
        modifier = modifier
    ) { contentModifier ->
        LazyColumn(
            modifier = contentModifier.fillMaxSize(),
            contentPadding = hierarchyContentPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(key = "dashboard") {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.screen_tipps),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    MatchCenterDashboard(
                        jackpotAmount = kassePreview,
                        openRoundsCount = openRounds,
                        activeGamesCount = activeGames
                    )
                }
            }

            item(key = "matches_header") {
                HierarchySectionHeader(title = stringResource(R.string.section_matches))
            }

            if (flatGames.isEmpty()) {
                item(key = "empty_matches") {
                    MatchCenterEmptyState(
                        title = stringResource(R.string.empty_matches_title),
                        message = stringResource(R.string.empty_matches_message)
                    )
                }
            } else {
                items(flatGames, key = { "${it.roundId}-${it.dayId}-${it.game.id}" }) { item ->
                    MatchCenterCard(
                        game = item.game,
                        matchdayLabel = item.dayName,
                        onClick = { onGameClick(item.roundId, item.dayId, item.game.id) }
                    )
                }
            }

            item(key = "tournaments_header") {
                HierarchySectionHeader(title = stringResource(R.string.section_tournaments))
            }

            if (rounds.isEmpty()) {
                item(key = "empty_tournaments") {
                    MatchCenterEmptyState(
                        title = stringResource(R.string.empty_tournaments_title),
                        message = stringResource(R.string.empty_tournaments_message)
                    )
                }
            } else {
                items(rounds, key = { it.id }) { round ->
                    TournamentCard(
                        round = round,
                        onClick = { onTournamentClick(round.id) },
                        subtitle = round.note ?: stringResource(R.string.tournament_card_hint)
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddTournamentDialog(
            onDismiss = { showAddDialog = false },
            onSave = { name, note ->
                FunBettRepository.addRound(name, note)
                showAddDialog = false
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TournamentScreenPreview() {
    WMFunBett2026Theme {
        TournamentScreen(onTournamentClick = {}, onGameClick = { _, _, _ -> })
    }
}
