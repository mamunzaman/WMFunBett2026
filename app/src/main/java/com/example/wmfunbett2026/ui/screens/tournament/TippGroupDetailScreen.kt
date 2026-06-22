package com.example.wmfunbett2026.ui.screens.tournament

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.data.jackpot.JackpotChainCalculator
import com.example.wmfunbett2026.data.model.Entry
import com.example.wmfunbett2026.data.model.Game
import com.example.wmfunbett2026.data.model.TippGroup
import com.example.wmfunbett2026.data.model.toEuroLabel
import com.example.wmfunbett2026.data.repository.FunBettRepository
import com.example.wmfunbett2026.data.winner.TippGroupWinnerEngine
import com.example.wmfunbett2026.data.winner.TippGroupWinnerOutcome
import com.example.wmfunbett2026.ui.components.AddEntryPlaceholderSheet
import com.example.wmfunbett2026.ui.components.DeleteConfirmDialog
import com.example.wmfunbett2026.ui.components.DetailStatusChip
import com.example.wmfunbett2026.ui.components.FormBottomSheet
import com.example.wmfunbett2026.ui.components.GlassEntryCard
import com.example.wmfunbett2026.ui.components.GlassPrimaryActionButton
import com.example.wmfunbett2026.ui.components.GlassScopePill
import com.example.wmfunbett2026.ui.components.GlassStatChip
import com.example.wmfunbett2026.ui.components.GlassSurface
import com.example.wmfunbett2026.ui.components.HierarchyListContentPadding
import com.example.wmfunbett2026.ui.components.HierarchyScreenLayout
import com.example.wmfunbett2026.ui.components.HierarchySectionHeader
import com.example.wmfunbett2026.ui.components.SampleDataNotice
import com.example.wmfunbett2026.ui.components.WinnerShareSettingsDialog
import com.example.wmfunbett2026.ui.components.hierarchyContentPadding
import com.example.wmfunbett2026.ui.matchcenter.teamFlagEmoji
import com.example.wmfunbett2026.ui.navigation.HierarchyLabels
import com.example.wmfunbett2026.ui.theme.JackpotGold
import com.example.wmfunbett2026.ui.theme.PrimaryText
import com.example.wmfunbett2026.ui.theme.SecondaryText
import com.example.wmfunbett2026.ui.theme.WMFunBett2026Theme

private data class PersonGameTippRow(
    val tippTitle: String,
    val scopeLabel: String,
    val prediction: String,
    val amount: Double
)

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
    var showDeleteGroupDialog by remember { mutableStateOf(false) }
    var showWinnerShareDialog by remember { mutableStateOf(false) }
    var showSampleAddPerson by remember { mutableStateOf(false) }
    var selectedPersonName by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(tippGroupId, gameId) {
        showDeleteGroupDialog = false
        showWinnerShareDialog = false
        showSampleAddPerson = false
        selectedPersonName = null
    }

    val game = FunBettRepository.getGameInDay(dayId, gameId)
    val tippGroup = FunBettRepository.getTippGroupInGame(gameId, tippGroupId)
    val entries = tippGroup?.entries.orEmpty()
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
        onWinnerShareSettingsClick = if (tippGroup != null) {{ showWinnerShareDialog = true }} else null,
        onDeleteClick = if (tippGroup != null) {{ showDeleteGroupDialog = true }} else null,
        deleteEnabled = entries.isEmpty(),
        modifier = modifier
    ) { contentModifier ->
        if (tippGroup == null || game == null) {
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
            contentPadding = hierarchyContentPadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item(key = "notice") { SampleDataNotice() }
            item(key = "summary") {
                TippGroupGlassHeaderCard(
                    title = tippGroup.title,
                    teamA = game.teamA,
                    teamB = game.teamB,
                    scopeLabel = tippGroup.timeScope.label,
                    peopleCount = entries.size,
                    entryAmountLabel = entryAmountLabel(tippGroup),
                    totalAmountLabel = tippGroup.totalAmount.toEuroLabel(),
                    winnerStatusLabel = winnerOutcome?.let { winnerStatusLabel(it) } ?: "Open",
                    onAddPersonClick = { showSampleAddPerson = true }
                )
            }
            item(key = "section") { HierarchySectionHeader(title = "Entries") }
            if (entries.isEmpty()) {
                item(key = "empty") {
                    Text(
                        text = "No entries yet — use Add Person below",
                        style = MaterialTheme.typography.bodyLarge,
                        color = SecondaryText
                    )
                }
            } else {
                items(entries, key = { it.id }) { entry ->
                    GlassEntryCard(
                        name = entry.name,
                        prediction = entry.prediction,
                        amountLabel = entry.amount.toEuroLabel(),
                        statusLabel = entryStatusLabel(winnerOutcome, entry),
                        note = entry.note,
                        adjustmentNotice = entryAdjustmentNotice(entry, tippGroup),
                        isWinner = winnerOutcome?.let { outcome ->
                            TippGroupWinnerEngine.isWinningEntry(outcome, entry.id)
                        } == true,
                        onClick = { selectedPersonName = entry.name }
                    )
                }
            }
        }
    }

    if (showSampleAddPerson) {
        AddEntryPlaceholderSheet(onDismiss = { showSampleAddPerson = false })
    }

    selectedPersonName?.let { personName ->
        val currentGame = game ?: return@let
        PersonInGameDetailSheet(
            personName = personName,
            game = currentGame,
            rows = collectPersonGameTipps(currentGame, personName),
            onDismiss = { selectedPersonName = null }
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
}

@Composable
private fun TippGroupGlassHeaderCard(
    title: String,
    teamA: String,
    teamB: String,
    scopeLabel: String,
    peopleCount: Int,
    entryAmountLabel: String,
    totalAmountLabel: String,
    winnerStatusLabel: String,
    onAddPersonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassSurface(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "${teamFlagEmoji(teamA)} $teamA  vs  ${teamFlagEmoji(teamB)} $teamB",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = SecondaryText
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                DetailStatusChip(label = winnerStatusLabel)
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
                GlassScopePill(label = scopeLabel)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GlassStatChip(
                    label = "People",
                    value = peopleCount.toString(),
                    modifier = Modifier.weight(1f)
                )
                GlassStatChip(
                    label = "Entry / person",
                    value = entryAmountLabel,
                    modifier = Modifier.weight(1f)
                )
                GlassStatChip(
                    label = "Collected",
                    value = totalAmountLabel,
                    highlight = true,
                    modifier = Modifier.weight(1f)
                )
            }
            GlassPrimaryActionButton(
                label = "Add Person",
                onClick = onAddPersonClick
            )
        }
    }
}

@Composable
private fun PersonInGameDetailSheet(
    personName: String,
    game: Game,
    rows: List<PersonGameTippRow>,
    onDismiss: () -> Unit
) {
    val totalInGame = rows.sumOf { it.amount }

    FormBottomSheet(
        title = personName,
        onDismiss = onDismiss,
        primaryActionLabel = stringResource(R.string.ok),
        onPrimaryAction = onDismiss,
        showCancel = false
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "${game.teamA} vs ${game.teamB}",
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryText
            )
            Text(
                text = "Tipps in this game only",
                style = MaterialTheme.typography.labelLarge,
                color = SecondaryText
            )
            if (rows.isEmpty()) {
                Text(
                    text = "No tipp entries found for this game.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryText
                )
            } else {
                rows.forEach { row ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = row.tippTitle,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryText
                        )
                        Text(
                            text = row.scopeLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryText
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = row.prediction,
                                style = MaterialTheme.typography.bodyMedium,
                                color = PrimaryText
                            )
                            Text(
                                text = row.amount.toEuroLabel(),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = JackpotGold
                            )
                        }
                    }
                    HorizontalDivider(color = SecondaryText.copy(alpha = 0.15f))
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total in this game",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryText
                )
                Text(
                    text = totalInGame.toEuroLabel(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = JackpotGold
                )
            }
        }
    }
}

private fun collectPersonGameTipps(game: Game, personName: String): List<PersonGameTippRow> {
    val normalized = personName.trim().lowercase()
    return game.tippGroups.flatMap { group ->
        group.entries
            .filter { it.name.trim().lowercase() == normalized }
            .map { entry ->
                PersonGameTippRow(
                    tippTitle = group.title,
                    scopeLabel = group.timeScope.label,
                    prediction = entry.prediction,
                    amount = entry.amount
                )
            }
    }
}

private fun entryAmountLabel(tippGroup: TippGroup): String {
    val amount = JackpotChainCalculator.requiredPerPersonAmount(tippGroup)
    return amount?.toEuroLabel() ?: "—"
}

private fun standardRoundAmount(tippGroup: TippGroup): Double? =
    tippGroup.entries.firstOrNull()?.currentRoundAmount

private fun entryAdjustmentNotice(entry: Entry, tippGroup: TippGroup): String? {
    val standard = standardRoundAmount(tippGroup) ?: return null
    if (entry.currentRoundAmount == standard && entry.amount == standard) return null
    return if (entry.amount > standard || entry.currentRoundAmount > standard) {
        "Joined after carry-over"
    } else {
        "Later added / adjusted amount"
    }
}

private fun winnerStatusLabel(outcome: TippGroupWinnerOutcome): String = when (outcome) {
    TippGroupWinnerOutcome.Pending -> "Open"
    TippGroupWinnerOutcome.NoWinner -> "No winner yet"
    is TippGroupWinnerOutcome.Winners -> "Winner split"
}

private fun entryStatusLabel(outcome: TippGroupWinnerOutcome?, entry: Entry): String {
    if (outcome == null || outcome is TippGroupWinnerOutcome.Pending) return "Pending"
    return if (TippGroupWinnerEngine.isWinningEntry(outcome, entry.id)) "Winner" else "Lost"
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
