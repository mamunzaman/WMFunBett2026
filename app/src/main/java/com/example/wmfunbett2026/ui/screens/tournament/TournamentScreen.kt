package com.example.wmfunbett2026.ui.screens.tournament

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.data.model.Round
import com.example.wmfunbett2026.data.model.toEuroLabel
import com.example.wmfunbett2026.data.repository.FunBettRepository
import com.example.wmfunbett2026.ui.components.AddTournamentDialog
import com.example.wmfunbett2026.ui.components.HierarchyScreenLayout
import com.example.wmfunbett2026.ui.components.HierarchySectionHeader
import com.example.wmfunbett2026.ui.components.SampleDataNotice
import com.example.wmfunbett2026.ui.components.hierarchyContentPadding
import com.example.wmfunbett2026.ui.navigation.HierarchyLabels
import com.example.wmfunbett2026.ui.theme.JackpotGold
import com.example.wmfunbett2026.ui.theme.PrimaryText
import com.example.wmfunbett2026.ui.theme.SecondaryText
import com.example.wmfunbett2026.ui.theme.SurfaceDark
import com.example.wmfunbett2026.ui.theme.WMFunBett2026Theme

@Composable
fun TournamentScreen(
    onTournamentClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    FunBettRepository.dataVersion.intValue
    var showAddDialog by remember { mutableStateOf(false) }

    val rounds = FunBettRepository.getRounds()
    val gameCount = FunBettRepository.getTotalGameCount()
    val kassePreview = FunBettRepository.getTotalKassePreview().toEuroLabel()

    HierarchyScreenLayout(
        title = "Tipps",
        breadcrumbs = HierarchyLabels.forTournamentList(),
        onBackClick = null,
        onFabClick = { showAddDialog = true },
        fabContentDescription = "Add tournament",
        modifier = modifier
    ) { contentModifier ->
        LazyColumn(
            modifier = contentModifier.fillMaxSize(),
            contentPadding = hierarchyContentPadding(withFab = true),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(key = "notice") { SampleDataNotice() }
            item(key = "stats") {
                TournamentStatsRow(
                    tournamentCount = rounds.size,
                    gameCount = gameCount,
                    kassePreview = kassePreview
                )
            }
            item(key = "section") {
                HierarchySectionHeader(title = "Your Tournaments")
            }
            if (rounds.isEmpty()) {
                item(key = "empty") {
                    Text(
                        text = "No tournaments yet — tap + to add one",
                        style = MaterialTheme.typography.bodyLarge,
                        color = SecondaryText
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

@Composable
private fun TournamentStatsRow(
    tournamentCount: Int,
    gameCount: Int,
    kassePreview: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TournamentMiniStatCard(label = "Tournaments", value = tournamentCount.toString(), modifier = Modifier.weight(1f))
        TournamentMiniStatCard(label = "Games", value = gameCount.toString(), modifier = Modifier.weight(1f))
        TournamentMiniStatCard(label = "Kasse", value = kassePreview, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun TournamentMiniStatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = SecondaryText
            )
            Text(
                text = value,
                modifier = Modifier.padding(top = 4.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PrimaryText
            )
        }
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
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 18.dp, end = 16.dp, top = 18.dp, bottom = 18.dp),
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
                    text = round.note ?: "Tap to view games",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryText
                )
            }
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = JackpotGold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TournamentScreenPreview() {
    WMFunBett2026Theme {
        TournamentScreen(onTournamentClick = {})
    }
}
