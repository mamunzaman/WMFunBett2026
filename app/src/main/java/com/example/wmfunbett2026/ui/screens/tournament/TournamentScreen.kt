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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.data.model.Round
import com.example.wmfunbett2026.data.model.toEuroLabel
import com.example.wmfunbett2026.data.repository.FunBettRepository
import com.example.wmfunbett2026.ui.components.AddTournamentDialog
import com.example.wmfunbett2026.ui.components.HierarchyScreenLayout
import com.example.wmfunbett2026.ui.components.HierarchySectionHeader
import com.example.wmfunbett2026.ui.components.MatchCenterCard
import com.example.wmfunbett2026.ui.components.MatchCenterDashboard
import com.example.wmfunbett2026.ui.components.MatchCenterEmptyState
import com.example.wmfunbett2026.ui.components.hierarchyContentPadding
import com.example.wmfunbett2026.ui.matchcenter.countActiveGames
import com.example.wmfunbett2026.ui.matchcenter.countOpenRounds
import com.example.wmfunbett2026.ui.matchcenter.flattenAllGames
import com.example.wmfunbett2026.ui.theme.JackpotGold
import com.example.wmfunbett2026.ui.theme.PrimaryText
import com.example.wmfunbett2026.ui.theme.SecondaryText
import com.example.wmfunbett2026.ui.theme.SurfaceDark
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item(key = "dashboard") {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.screen_tipps),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                    Spacer(modifier = Modifier.height(14.dp))
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
                        onClick = { onTournamentClick(round.id) }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TournamentCard(
    round: Round,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = round.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryText
                )
                Text(
                    text = round.note ?: stringResource(R.string.tournament_card_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryText
                )
            }
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                modifier = Modifier.padding(start = 8.dp),
                tint = JackpotGold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TournamentScreenPreview() {
    WMFunBett2026Theme {
        TournamentScreen(onTournamentClick = {}, onGameClick = { _, _, _ -> })
    }
}
