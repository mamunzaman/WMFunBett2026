package com.example.wmfunbett2026.ui.screens.tournament

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.example.wmfunbett2026.data.jackpot.JackpotChainCalculator
import com.example.wmfunbett2026.data.model.toEuroLabel
import com.example.wmfunbett2026.data.repository.FunBettRepository
import com.example.wmfunbett2026.data.winner.TippGroupWinnerEngine
import com.example.wmfunbett2026.ui.components.AddEntryDialog
import com.example.wmfunbett2026.ui.components.DeleteConfirmDialog
import com.example.wmfunbett2026.ui.components.EntryListCard
import com.example.wmfunbett2026.ui.components.HierarchyListContentPadding
import com.example.wmfunbett2026.ui.components.HierarchyScreenLayout
import com.example.wmfunbett2026.ui.components.HierarchySectionHeader
import com.example.wmfunbett2026.ui.components.SampleDataNotice
import com.example.wmfunbett2026.ui.components.TippGroupWinnerSummaryCard
import com.example.wmfunbett2026.ui.components.WinnerShareSettingsDialog
import com.example.wmfunbett2026.ui.components.hierarchyContentPadding
import com.example.wmfunbett2026.ui.navigation.HierarchyLabels
import com.example.wmfunbett2026.ui.theme.JackpotGold
import com.example.wmfunbett2026.ui.theme.PrimaryText
import com.example.wmfunbett2026.ui.theme.SecondaryText
import com.example.wmfunbett2026.ui.theme.SurfaceDark
import com.example.wmfunbett2026.ui.theme.WMFunBett2026Theme

@Composable
fun TippGroupDetailScreen(
    roundId: String,
    dayId: String,
    gameId: String,
    tippGroupId: String,
    onBackClick: () -> Unit,
    onDeleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    FunBettRepository.dataVersion.intValue
    var showAddEntryDialog by remember { mutableStateOf(false) }
    var showDeleteGroupDialog by remember { mutableStateOf(false) }
    var showWinnerShareDialog by remember { mutableStateOf(false) }
    var entryToDelete by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(tippGroupId, gameId) {
        showAddEntryDialog = false
        showDeleteGroupDialog = false
        showWinnerShareDialog = false
        entryToDelete = null
    }

    val game = FunBettRepository.getGameInDay(dayId, gameId)
    val round = FunBettRepository.getRound(roundId)
    val tippGroup = FunBettRepository.getTippGroupInGame(gameId, tippGroupId)
    val entries = tippGroup?.entries.orEmpty()
    val carryItems = remember(round, gameId, FunBettRepository.dataVersion.intValue) {
        if (round != null) JackpotChainCalculator.calculateCarryItems(round, gameId) else emptyList()
    }
    val existingRoundAmount = entries.firstOrNull()?.currentRoundAmount
    val winnerOutcome = remember(game, tippGroup, FunBettRepository.dataVersion.intValue) {
        if (game != null && tippGroup != null) {
            TippGroupWinnerEngine.calculate(game, tippGroup)
        } else {
            null
        }
    }

    HierarchyScreenLayout(
        title = tippGroup?.title ?: "Tipp Group",
        breadcrumbs = HierarchyLabels.forTippGroupDetail(roundId, gameId, tippGroupId),
        onBackClick = onBackClick,
        onFabClick = if (tippGroup != null) {{ showAddEntryDialog = true }} else null,
        fabContentDescription = "Add entry",
        onWinnerShareSettingsClick = if (tippGroup != null) {{ showWinnerShareDialog = true }} else null,
        onDeleteClick = if (tippGroup != null) {{ showDeleteGroupDialog = true }} else null,
        deleteEnabled = entries.isEmpty(),
        modifier = modifier
    ) { contentModifier ->
        if (tippGroup == null) {
            LazyColumn(
                modifier = contentModifier.fillMaxWidth(),
                contentPadding = HierarchyListContentPadding
            ) {
                item(key = "notice") { SampleDataNotice() }
                item(key = "not_found") {
                    Text(
                        text = "Tipp group not found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = SecondaryText
                    )
                }
            }
            return@HierarchyScreenLayout
        }

        LazyColumn(
            modifier = contentModifier.fillMaxWidth(),
            contentPadding = hierarchyContentPadding(withFab = true),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(key = "notice") { SampleDataNotice() }
            item(key = "summary") {
                TippGroupSummaryCard(
                    title = tippGroup.title,
                    timeScope = tippGroup.timeScope.label,
                    totalAmount = tippGroup.totalAmount.toEuroLabel(),
                    entryCount = entries.size
                )
            }
            if (winnerOutcome != null) {
                item(key = "winner") {
                    TippGroupWinnerSummaryCard(outcome = winnerOutcome)
                }
            }
            item(key = "section") { HierarchySectionHeader(title = "Entries") }
            if (entries.isEmpty()) {
                item(key = "empty") {
                    Text(
                        text = "No entries yet — tap + to add one",
                        style = MaterialTheme.typography.bodyLarge,
                        color = SecondaryText
                    )
                }
            } else {
                items(entries, key = { it.id }) { entry ->
                    EntryListCard(
                        name = entry.name,
                        prediction = entry.prediction,
                        amount = entry.amount.toEuroLabel(),
                        roundStakeLabel = if (entry.amount != entry.currentRoundAmount) {
                            "Round stake: ${entry.currentRoundAmount.toEuroLabel()}"
                        } else {
                            null
                        },
                        note = entry.note,
                        isWinner = winnerOutcome?.let { outcome ->
                            TippGroupWinnerEngine.isWinningEntry(outcome, entry.id)
                        } == true,
                        onDelete = { entryToDelete = entry.id }
                    )
                }
            }
        }
    }

    if (showAddEntryDialog && game != null && tippGroup != null) {
        AddEntryDialog(
            teamA = game.teamA,
            teamB = game.teamB,
            carryItems = carryItems,
            existingRoundAmount = existingRoundAmount,
            onDismiss = { showAddEntryDialog = false },
            onSave = { name, prediction, totalPaid, currentRoundAmount, note ->
                FunBettRepository.addEntry(
                    tippGroupId,
                    name,
                    prediction,
                    totalPaid,
                    currentRoundAmount,
                    note
                )
                showAddEntryDialog = false
            }
        )
    }

    if (showWinnerShareDialog) {
        WinnerShareSettingsDialog(onDismiss = { showWinnerShareDialog = false })
    }

    if (showDeleteGroupDialog) {
        DeleteConfirmDialog(
            onDismiss = { showDeleteGroupDialog = false },
            onConfirm = {
                showDeleteGroupDialog = false
                if (FunBettRepository.deleteTippGroup(tippGroupId)) {
                    onDeleted()
                }
            }
        )
    }

    entryToDelete?.let { entryId ->
        DeleteConfirmDialog(
            onDismiss = { entryToDelete = null },
            onConfirm = {
                FunBettRepository.deleteEntry(tippGroupId, entryId)
                entryToDelete = null
            }
        )
    }
}

@Composable
private fun TippGroupSummaryCard(
    title: String,
    timeScope: String,
    totalAmount: String,
    entryCount: Int,
    modifier: Modifier = Modifier
) {
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
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryText
            )
            Text(
                text = timeScope,
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryText
            )
            Text(
                text = "$entryCount entries · Total $totalAmount",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = JackpotGold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TippGroupDetailScreenPreview() {
    WMFunBett2026Theme {
        TippGroupDetailScreen(
            roundId = FunBettRepository.ROUND_ID,
            dayId = FunBettRepository.DAY_ID,
            gameId = FunBettRepository.GAME_ID,
            tippGroupId = FunBettRepository.TIPP_GROUP_ID,
            onBackClick = {},
            onDeleted = {}
        )
    }
}
