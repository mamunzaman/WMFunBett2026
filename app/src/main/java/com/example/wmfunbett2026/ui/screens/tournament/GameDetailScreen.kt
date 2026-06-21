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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.data.model.Game
import com.example.wmfunbett2026.data.model.TimeScope
import com.example.wmfunbett2026.data.model.TippGroup
import com.example.wmfunbett2026.data.model.toEuroLabel
import com.example.wmfunbett2026.data.repository.FunBettRepository
import com.example.wmfunbett2026.data.tipp.TippScopeAvailability
import com.example.wmfunbett2026.ui.components.AddTippGroupDialog
import com.example.wmfunbett2026.ui.components.DeleteConfirmDialog
import com.example.wmfunbett2026.ui.components.HierarchyListContentPadding
import com.example.wmfunbett2026.ui.components.HierarchyScreenLayout
import com.example.wmfunbett2026.ui.components.HierarchySectionHeader
import com.example.wmfunbett2026.ui.components.NavListCard
import com.example.wmfunbett2026.ui.components.SampleDataNotice
import com.example.wmfunbett2026.ui.components.hierarchyContentPadding
import com.example.wmfunbett2026.ui.navigation.HierarchyLabels
import com.example.wmfunbett2026.ui.theme.JackpotGold
import com.example.wmfunbett2026.ui.theme.PrimaryText
import com.example.wmfunbett2026.ui.theme.SecondaryText
import com.example.wmfunbett2026.ui.theme.SurfaceDark
import com.example.wmfunbett2026.ui.theme.WMFunBett2026Theme

@Composable
fun GameDetailScreen(
    roundId: String,
    dayId: String,
    gameId: String,
    onBackClick: () -> Unit,
    onTippGroupClick: (String) -> Unit,
    onDeleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    FunBettRepository.dataVersion.intValue
    var showAddDialog by remember { mutableStateOf(false) }
    var showNoScopeDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var availableScopesForDialog by remember { mutableStateOf<List<TimeScope>>(emptyList()) }

    LaunchedEffect(gameId) {
        showAddDialog = false
        showNoScopeDialog = false
        showDeleteDialog = false
        availableScopesForDialog = emptyList()
    }

    val game = FunBettRepository.getGameInDay(dayId, gameId)
    val day = FunBettRepository.getDay(dayId)
    val tippGroups = FunBettRepository.getTippGroups(gameId)
    val availableScopes = remember(game, FunBettRepository.dataVersion.intValue) {
        game?.let { resolveAvailableScopes(it) }.orEmpty()
    }
    val noMatchTimeNote = if (game != null && !TippScopeAvailability.hasMatchTime(game)) {
        TippScopeAvailability.NO_MATCH_TIME_NOTE
    } else {
        null
    }

    fun onAddTippGroupClick() {
        val currentGame = FunBettRepository.getGameInDay(dayId, gameId) ?: return
        val freshScopes = resolveAvailableScopes(currentGame)
        if (freshScopes.isEmpty()) {
            showNoScopeDialog = true
        } else {
            availableScopesForDialog = freshScopes
            showAddDialog = true
        }
    }

    HierarchyScreenLayout(
        title = game?.displayName ?: "Game",
        breadcrumbs = HierarchyLabels.forGameDetail(roundId, gameId),
        onBackClick = onBackClick,
        onFabClick = if (game != null) {{ onAddTippGroupClick() }} else null,
        fabContentDescription = "Add tipp group",
        onDeleteClick = if (game != null) {{ showDeleteDialog = true }} else null,
        modifier = modifier
    ) { contentModifier ->
        if (game == null) {
            LazyColumn(
                modifier = contentModifier.fillMaxSize(),
                contentPadding = HierarchyListContentPadding
            ) {
                item { SampleDataNotice() }
                item {
                    Text(
                        text = "Game not found",
                        color = SecondaryText
                    )
                }
            }
            return@HierarchyScreenLayout
        }

        LazyColumn(
            modifier = contentModifier.fillMaxSize(),
            contentPadding = hierarchyContentPadding(withFab = true),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(key = "notice") { SampleDataNotice() }
            item(key = "game_info") {
                GameInfoCard(
                    game = game,
                    dayLabel = day?.name
                )
            }
            item(key = "section") { HierarchySectionHeader(title = "Tipp Groups") }
            if (availableScopes.isEmpty() && tippGroups.isNotEmpty()) {
                item(key = "no_scopes") {
                    Text(
                        text = TippScopeAvailability.NONE_AVAILABLE_MESSAGE,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SecondaryText
                    )
                }
            }
            if (tippGroups.isEmpty()) {
                item(key = "empty") {
                    Text(
                        text = if (availableScopes.isEmpty()) {
                            TippScopeAvailability.NONE_AVAILABLE_MESSAGE
                        } else {
                            "No tipp groups yet — tap + to add one"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = SecondaryText
                    )
                }
            } else {
                items(tippGroups, key = { it.id }) { tippGroup ->
                    TippGroupNavCard(
                        tippGroup = tippGroup,
                        onClick = { onTippGroupClick(tippGroup.id) }
                    )
                }
            }
        }
    }

    if (showAddDialog && availableScopesForDialog.isNotEmpty()) {
        AddTippGroupDialog(
            availableScopes = availableScopesForDialog,
            noMatchTimeNote = noMatchTimeNote,
            onDismiss = {
                showAddDialog = false
                availableScopesForDialog = emptyList()
            },
            onSave = { title, timeScope ->
                FunBettRepository.addTippGroup(gameId, title, timeScope)
                showAddDialog = false
                availableScopesForDialog = emptyList()
            }
        )
    }

    if (showNoScopeDialog) {
        AlertDialog(
            onDismissRequest = { showNoScopeDialog = false },
            title = { Text("No Tipp type available") },
            text = { Text("No Tipp type is available for this game anymore.") },
            confirmButton = {
                TextButton(onClick = { showNoScopeDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    if (showDeleteDialog) {
        DeleteConfirmDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                showDeleteDialog = false
                if (FunBettRepository.deleteGame(gameId)) {
                    onDeleted()
                }
            }
        )
    }
}

private fun resolveAvailableScopes(game: Game): List<TimeScope> {
    return runCatching {
        TippScopeAvailability.getAvailableScopes(game)
    }.getOrElse {
        val existing = game.tippGroups.map { it.timeScope }.toSet()
        TimeScope.entries.filter { scope -> scope !in existing }
    }
}

@Composable
private fun GameInfoCard(
    game: Game,
    dayLabel: String?,
    modifier: Modifier = Modifier
) {
    val scheduleLabel = listOfNotNull(dayLabel, game.dateTimeLabel).joinToString(" · ")

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "${game.teamA}  vs  ${game.teamB}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = PrimaryText
            )
            Text(
                text = scheduleLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryText
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Result: ${game.resultPlaceholder}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryText
                )
                Text(
                    text = "Game Kasse: ${game.totalKasse.toEuroLabel()}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = JackpotGold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TippGroupNavCard(
    tippGroup: TippGroup,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavListCard(
        title = tippGroup.title,
        subtitle = "${tippGroup.timeScope.label} · ${tippGroup.entries.size} entries · ${tippGroup.totalAmount.toEuroLabel()}",
        onClick = onClick,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GameDetailScreenPreview() {
    WMFunBett2026Theme {
        GameDetailScreen(
            roundId = FunBettRepository.ROUND_ID,
            dayId = FunBettRepository.DAY_ID,
            gameId = FunBettRepository.GAME_ID,
            onBackClick = {},
            onTippGroupClick = {},
            onDeleted = {}
        )
    }
}
