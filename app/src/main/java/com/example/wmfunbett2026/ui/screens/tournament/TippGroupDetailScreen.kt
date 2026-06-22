package com.example.wmfunbett2026.ui.screens.tournament

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.wmfunbett2026.data.model.Game
import com.example.wmfunbett2026.data.model.TippGroup
import com.example.wmfunbett2026.data.model.TippGroupSettlementStatus
import com.example.wmfunbett2026.data.model.TippGroupSettlementSummary
import com.example.wmfunbett2026.data.model.toEuroLabel
import com.example.wmfunbett2026.data.repository.FunBettRepository
import com.example.wmfunbett2026.data.winner.TippGroupWinnerEngine
import com.example.wmfunbett2026.data.winner.TippGroupWinnerOutcome
import com.example.wmfunbett2026.ui.components.AddEntrySheet
import com.example.wmfunbett2026.ui.components.AllFriendsJoinedInfoCard
import com.example.wmfunbett2026.ui.components.AllFriendsJoinedInfoSheet
import com.example.wmfunbett2026.ui.components.DeleteConfirmDialog
import com.example.wmfunbett2026.ui.components.DetailStatusChip
import com.example.wmfunbett2026.ui.components.TippGroupEntryTable
import com.example.wmfunbett2026.ui.components.FormBottomSheet
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
import com.example.wmfunbett2026.ui.matchcenter.tippGroupWinnerNames
import com.example.wmfunbett2026.ui.matchcenter.tippGroupWinningEntryIds
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
    var showAddEntry by remember { mutableStateOf(false) }
    var showAllFriendsJoinedInfo by remember { mutableStateOf(false) }
    var selectedPersonName by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(tippGroupId, gameId) {
        showDeleteGroupDialog = false
        showWinnerShareDialog = false
        showAddEntry = false
        showAllFriendsJoinedInfo = false
        selectedPersonName = null
    }

    val game = FunBettRepository.getGameInDay(dayId, gameId)
    val tippGroup = FunBettRepository.getTippGroupInGame(gameId, tippGroupId)
    val entries = remember(tippGroupId, FunBettRepository.dataVersion.intValue) {
        FunBettRepository.getTippGroupInGame(gameId, tippGroupId)?.entries.orEmpty()
    }
    val canAddEntry = remember(tippGroupId, FunBettRepository.dataVersion.intValue) {
        FunBettRepository.hasAvailableFriendsForTippGroup(tippGroupId)
    }
    val winnerOutcome = remember(game, tippGroup, FunBettRepository.dataVersion.intValue) {
        if (game != null && tippGroup != null) {
            TippGroupWinnerEngine.calculate(game, tippGroup)
        } else {
            null
        }
    }
    val winningEntryIds = remember(game, tippGroup, FunBettRepository.dataVersion.intValue) {
        if (game != null && tippGroup != null) {
            tippGroupWinningEntryIds(game, tippGroup)
        } else {
            emptySet()
        }
    }
    val winnerNames = remember(game, tippGroup, FunBettRepository.dataVersion.intValue) {
        if (game != null && tippGroup != null) {
            tippGroupWinnerNames(game, tippGroup)
        } else {
            emptyList()
        }
    }
    val settlement = remember(gameId, tippGroupId, FunBettRepository.dataVersion.intValue) {
        FunBettRepository.getTippGroupSettlementSummary(gameId, tippGroupId)
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
                    canAddEntry = canAddEntry,
                    onAddEntryClick = {
                        if (canAddEntry) showAddEntry = true else showAllFriendsJoinedInfo = true
                    }
                )
            }
            settlement?.let { summary ->
                item(key = "settlement") {
                    TippGroupSettlementCard(summary = summary)
                }
            }
            item(key = "section") { HierarchySectionHeader(title = "Entries") }
            if (entries.isEmpty()) {
                item(key = "empty") {
                    Text(
                        text = stringResource(R.string.tipp_group_entries_empty),
                        style = MaterialTheme.typography.bodyLarge,
                        color = SecondaryText
                    )
                }
            } else {
                item(key = "entries_table") {
                    TippGroupEntryTable(
                        game = game,
                        entries = entries,
                        winningEntryIds = winningEntryIds,
                        winnerNames = winnerNames,
                        settlement = settlement ?: TippGroupSettlementSummary(
                            status = TippGroupSettlementStatus.PENDING,
                            totalCollected = 0.0,
                            winnerCount = 0,
                            sharePerWinner = 0.0
                        ),
                        onEntryClick = { entry -> selectedPersonName = entry.friendName }
                    )
                }
            }
        }
    }

    if (showAddEntry) {
        AddEntrySheet(
            tippGroupId = tippGroupId,
            onDismiss = { showAddEntry = false },
            onCreate = { friendId, prediction, note ->
                FunBettRepository.addEntryToTippGroup(
                    tippGroupId = tippGroupId,
                    friendId = friendId,
                    prediction = prediction,
                    note = note
                )
                showAddEntry = false
            }
        )
    }

    if (showAllFriendsJoinedInfo) {
        AllFriendsJoinedInfoSheet(onDismiss = { showAllFriendsJoinedInfo = false })
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
private fun TippGroupSettlementCard(
    summary: TippGroupSettlementSummary,
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
                text = stringResource(R.string.settlement_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = PrimaryText
            )
            when (summary.status) {
                TippGroupSettlementStatus.PENDING -> {
                    Text(
                        text = stringResource(R.string.settlement_pending_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryText
                    )
                    Text(
                        text = stringResource(R.string.settlement_pending_message),
                        style = MaterialTheme.typography.bodyMedium,
                        color = SecondaryText
                    )
                }
                TippGroupSettlementStatus.NO_WINNERS,
                TippGroupSettlementStatus.WINNERS -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GlassStatChip(
                            label = stringResource(R.string.settlement_collected),
                            value = summary.totalCollected.toEuroLabel(),
                            modifier = Modifier.weight(1f)
                        )
                        GlassStatChip(
                            label = stringResource(R.string.settlement_winners),
                            value = summary.winnerCount.toString(),
                            modifier = Modifier.weight(1f)
                        )
                        GlassStatChip(
                            label = stringResource(R.string.settlement_share_per_winner),
                            value = when (summary.status) {
                                TippGroupSettlementStatus.WINNERS ->
                                    summary.sharePerWinner.toEuroLabel()
                                else -> stringResource(R.string.settlement_no_winner)
                            },
                            highlight = summary.status == TippGroupSettlementStatus.WINNERS,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
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
    canAddEntry: Boolean,
    onAddEntryClick: () -> Unit,
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
            if (canAddEntry) {
                GlassPrimaryActionButton(
                    label = stringResource(R.string.action_add_entry),
                    onClick = onAddEntryClick
                )
            } else {
                AllFriendsJoinedInfoCard(
                    message = stringResource(R.string.add_entry_all_friends_joined)
                )
            }
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
            .filter { it.friendName.trim().lowercase() == normalized }
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

private fun winnerStatusLabel(outcome: TippGroupWinnerOutcome): String = when (outcome) {
    TippGroupWinnerOutcome.Pending -> "Open"
    TippGroupWinnerOutcome.NoWinner -> "No winner yet"
    is TippGroupWinnerOutcome.Winners -> "Winner split"
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
