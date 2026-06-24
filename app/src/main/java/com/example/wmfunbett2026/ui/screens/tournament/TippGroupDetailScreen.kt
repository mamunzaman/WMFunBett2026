package com.example.wmfunbett2026.ui.screens.tournament

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.data.jackpot.JackpotChainCalculator
import com.example.wmfunbett2026.data.model.Entry
import com.example.wmfunbett2026.data.model.Game
import com.example.wmfunbett2026.data.model.TippGroup
import com.example.wmfunbett2026.data.jackpot.JackpotCarryOverSummary
import com.example.wmfunbett2026.data.jackpot.TippGroupV2Settlement
import com.example.wmfunbett2026.data.jackpot.TippGroupV2SettlementPhase
import com.example.wmfunbett2026.data.model.TippGroupEntryBlockReason
import com.example.wmfunbett2026.data.model.toEuroLabel
import com.example.wmfunbett2026.data.repository.FunBettRepository
import com.example.wmfunbett2026.ui.components.AddEntrySheet
import com.example.wmfunbett2026.ui.components.DeleteConfirmDialog
import com.example.wmfunbett2026.ui.components.EditEntrySheet
import com.example.wmfunbett2026.ui.components.EntryBlockedInfoSheet
import com.example.wmfunbett2026.ui.components.EntryClosedInfoCard
import com.example.wmfunbett2026.ui.components.EntryRowActionSheet
import com.example.wmfunbett2026.ui.components.TippGroupEntryTable
import com.example.wmfunbett2026.ui.components.FormBottomSheet
import com.example.wmfunbett2026.ui.components.GlassPrimaryActionButton
import com.example.wmfunbett2026.ui.components.GlassScopePill
import androidx.compose.material3.TextButton
import com.example.wmfunbett2026.ui.theme.DangerRed
import com.example.wmfunbett2026.ui.components.HierarchyListContentPadding
import com.example.wmfunbett2026.ui.components.HierarchyScreenLayout
import com.example.wmfunbett2026.ui.components.HierarchySectionHeader
import com.example.wmfunbett2026.ui.components.MatchCenterTippGroupDetailMatchCard
import com.example.wmfunbett2026.ui.components.hierarchyContentPadding
import com.example.wmfunbett2026.ui.matchcenter.entryV2PayoutLabel
import com.example.wmfunbett2026.ui.matchcenter.tippGroupWinnerNames
import com.example.wmfunbett2026.ui.matchcenter.tippGroupWinningEntryIds
import com.example.wmfunbett2026.ui.matchcenter.winnerLineLabel
import com.example.wmfunbett2026.ui.navigation.HierarchyLabels
import com.example.wmfunbett2026.ui.theme.Divider
import com.example.wmfunbett2026.ui.theme.JackpotGold
import com.example.wmfunbett2026.ui.theme.PrimaryText
import com.example.wmfunbett2026.ui.theme.SecondaryText
import com.example.wmfunbett2026.ui.theme.Surface
import com.example.wmfunbett2026.ui.theme.WMFunBett2026Theme

private val TippGroupDetailPanelShape = RoundedCornerShape(14.dp)

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
    var showAddEntry by remember { mutableStateOf(false) }
    var showEntryBlockedInfo by remember { mutableStateOf<TippGroupEntryBlockReason?>(null) }
    var editingEntry by remember { mutableStateOf<Entry?>(null) }
    var deletingEntry by remember { mutableStateOf<Entry?>(null) }
    var entryActionTarget by remember { mutableStateOf<Entry?>(null) }
    var showEntryActionSheet by remember { mutableStateOf(false) }
    var selectionMode by remember { mutableStateOf(false) }
    var selectedEntryIds by remember { mutableStateOf(setOf<String>()) }
    var showBulkDeleteConfirm by remember { mutableStateOf(false) }
    var selectedPersonName by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(tippGroupId, gameId) {
        showDeleteGroupDialog = false
        showAddEntry = false
        showEntryBlockedInfo = null
        editingEntry = null
        deletingEntry = null
        entryActionTarget = null
        showEntryActionSheet = false
        selectionMode = false
        selectedEntryIds = emptySet()
        showBulkDeleteConfirm = false
        selectedPersonName = null
    }

    val game = FunBettRepository.getGameInDay(dayId, gameId)
    val tippGroup = FunBettRepository.getTippGroupInGame(gameId, tippGroupId)
    val entries = remember(tippGroupId, FunBettRepository.dataVersion.intValue) {
        FunBettRepository.getTippGroupInGame(gameId, tippGroupId)?.entries.orEmpty()
    }
    val entryBlockReason = remember(tippGroupId, gameId, FunBettRepository.dataVersion.intValue) {
        FunBettRepository.getTippGroupEntryBlockReason(tippGroupId)
    }
    val canAddEntry = entryBlockReason == null
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
    val v2Settlement = remember(roundId, gameId, tippGroupId, FunBettRepository.dataVersion.intValue) {
        FunBettRepository.getTippGroupV2Settlement(roundId, gameId, tippGroupId)
    }
    val entryPayouts = remember(v2Settlement) {
        v2Settlement?.calculation?.payoutsByEntryId.orEmpty()
    }
    val jackpotSummary = remember(roundId, gameId, tippGroupId, FunBettRepository.dataVersion.intValue) {
        FunBettRepository.getJackpotCarryOverSummary(roundId, gameId, tippGroupId)
    }
    val matchdayLabel = remember(dayId, FunBettRepository.dataVersion.intValue) {
        FunBettRepository.getDay(dayId)?.name ?: "Matchday"
    }

    HierarchyScreenLayout(
        title = if (selectionMode) {
            stringResource(R.string.entry_selection_count, selectedEntryIds.size)
        } else {
            tippGroup?.title ?: "Tipp Group"
        },
        breadcrumbs = HierarchyLabels.forTippGroupDetail(roundId, gameId, tippGroupId),
        onBackClick = onBackClick,
        onDeleteClick = if (tippGroup != null && !selectionMode) {{ showDeleteGroupDialog = true }} else null,
        deleteEnabled = entries.isEmpty(),
        modifier = modifier
    ) { contentModifier ->
        if (tippGroup == null || game == null) {
            LazyColumn(
                modifier = contentModifier.fillMaxWidth(),
                contentPadding = HierarchyListContentPadding
            ) {
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
            item(key = "summary") {
                TippGroupDetailHeaderSection(
                    game = game,
                    matchdayLabel = matchdayLabel,
                    title = tippGroup.title,
                    scopeLabel = tippGroup.timeScope.label,
                    peopleCount = entries.size,
                    entryAmountLabel = entryAmountLabel(tippGroup),
                    totalAmountLabel = tippGroup.totalAmount.toEuroLabel(),
                    entryBlockReason = entryBlockReason,
                    canAddEntry = canAddEntry,
                    onAddEntryClick = {
                        when (entryBlockReason) {
                            null -> showAddEntry = true
                            else -> showEntryBlockedInfo = entryBlockReason
                        }
                    },
                    jackpotSummary = jackpotSummary
                )
            }
            v2Settlement?.let { settlement ->
                item(key = "result_calculation") {
                    TippGroupV2ResultCalculationSection(settlement = settlement)
                }
            }
            item(key = "section") { HierarchySectionHeader(title = "Entries") }
            if (selectionMode) {
                item(key = "selection_bar") {
                    EntryBulkSelectionBar(
                        selectedCount = selectedEntryIds.size,
                        onCancel = {
                            selectionMode = false
                            selectedEntryIds = emptySet()
                        },
                        onDeleteSelected = {
                            if (selectedEntryIds.isNotEmpty()) {
                                showBulkDeleteConfirm = true
                            }
                        }
                    )
                }
            }
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
                        entryPayouts = entryPayouts,
                        onEntryClick = { entry -> selectedPersonName = entry.friendName },
                        onEditEntry = { entry -> editingEntry = entry },
                        onDeleteEntry = { entry -> deletingEntry = entry },
                        selectionMode = selectionMode,
                        selectedEntryIds = selectedEntryIds,
                        onToggleEntrySelection = { entry ->
                            selectedEntryIds = if (entry.id in selectedEntryIds) {
                                selectedEntryIds - entry.id
                            } else {
                                selectedEntryIds + entry.id
                            }
                        },
                        onEntryLongPress = { entry ->
                            entryActionTarget = entry
                            showEntryActionSheet = true
                        }
                    )
                }
            }
        }
    }

    editingEntry?.let { entry ->
        EditEntrySheet(
            tippGroupId = tippGroupId,
            entry = entry,
            onDismiss = { editingEntry = null },
            onSave = { request ->
                if (FunBettRepository.updateEntry(
                        tippGroupId = tippGroupId,
                        entryId = entry.id,
                        request = request
                    ) != null
                ) {
                    editingEntry = null
                }
            }
        )
    }

    deletingEntry?.let { entry ->
        DeleteConfirmDialog(
            titleRes = R.string.delete_entry_confirm_title,
            messageRes = R.string.delete_entry_confirm_message,
            onDismiss = { deletingEntry = null },
            onConfirm = {
                deletingEntry = null
                FunBettRepository.deleteEntry(tippGroupId, entry.id)
            }
        )
    }

    if (showBulkDeleteConfirm) {
        DeleteConfirmDialog(
            titleRes = R.string.delete_entries_bulk_confirm_title,
            messageRes = R.string.delete_entries_bulk_confirm_message,
            onDismiss = { showBulkDeleteConfirm = false },
            onConfirm = {
                FunBettRepository.deleteEntries(tippGroupId, selectedEntryIds)
                showBulkDeleteConfirm = false
                selectionMode = false
                selectedEntryIds = emptySet()
            }
        )
    }

    if (showEntryActionSheet && entryActionTarget != null) {
        val target = entryActionTarget!!
        EntryRowActionSheet(
            onDismiss = {
                showEntryActionSheet = false
                entryActionTarget = null
            },
            onEdit = {
                editingEntry = target
            },
            onDelete = {
                deletingEntry = target
            },
            onSelectMultiple = {
                selectionMode = true
                selectedEntryIds = setOf(target.id)
            }
        )
    }

    if (showAddEntry) {
        AddEntrySheet(
            tippGroupId = tippGroupId,
            roundId = roundId,
            gameId = gameId,
            onDismiss = { showAddEntry = false },
            onCreate = { friendId, prediction, note, participation ->
                if (FunBettRepository.addEntryToTippGroup(
                        tippGroupId = tippGroupId,
                        friendId = friendId,
                        prediction = prediction,
                        note = note,
                        participation = participation
                    ) != null
                ) {
                    showAddEntry = false
                }
            }
        )
    }

    showEntryBlockedInfo?.let { reason ->
        EntryBlockedInfoSheet(
            reason = reason,
            onDismiss = { showEntryBlockedInfo = null }
        )
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
private fun TippGroupDetailPanel(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(TippGroupDetailPanelShape)
            .background(Surface)
            .border(1.dp, Divider.copy(alpha = 0.65f), TippGroupDetailPanelShape)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        content = content
    )
}

@Composable
private fun TippGroupDetailStatRow(
    label: String,
    value: String,
    icon: ImageVector,
    highlightValue: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = SecondaryText,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = SecondaryText
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (highlightValue) JackpotGold else PrimaryText
        )
    }
}

@Composable
private fun TippGroupDetailStatDivider() {
    HorizontalDivider(color = Divider.copy(alpha = 0.45f))
}

@Composable
private fun TippGroupDetailHeaderSection(
    game: Game,
    matchdayLabel: String,
    title: String,
    scopeLabel: String,
    peopleCount: Int,
    entryAmountLabel: String,
    totalAmountLabel: String,
    entryBlockReason: TippGroupEntryBlockReason?,
    canAddEntry: Boolean,
    onAddEntryClick: () -> Unit,
    jackpotSummary: JackpotCarryOverSummary?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        MatchCenterTippGroupDetailMatchCard(
            game = game,
            matchdayLabel = matchdayLabel,
            tippGroupTitle = title,
            collectedLabel = totalAmountLabel,
            incomingJackpotLabel = jackpotSummary?.incomingJackpot
                ?.takeIf { it > 0 }
                ?.toEuroLabel()
        )
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = PrimaryText
        )
        GlassScopePill(label = scopeLabel)
        TippGroupDetailPanel {
            TippGroupDetailStatRow(
                label = stringResource(R.string.tipp_group_people),
                value = peopleCount.toString(),
                icon = Icons.Outlined.Groups
            )
            TippGroupDetailStatDivider()
            TippGroupDetailStatRow(
                label = stringResource(R.string.tipp_group_entry_per_person),
                value = entryAmountLabel,
                icon = Icons.Outlined.Payments
            )
            TippGroupDetailStatDivider()
            TippGroupDetailStatRow(
                label = stringResource(R.string.tipp_group_collected),
                value = totalAmountLabel,
                icon = Icons.Outlined.Savings,
                highlightValue = true
            )
        }
        when {
            canAddEntry -> {
                GlassPrimaryActionButton(
                    label = stringResource(R.string.action_add_entry),
                    onClick = onAddEntryClick
                )
            }
            entryBlockReason != null -> {
                EntryClosedInfoCard(reason = entryBlockReason)
            }
        }
    }
}

@Composable
private fun TippGroupV2ResultCalculationSection(
    settlement: TippGroupV2Settlement,
    modifier: Modifier = Modifier
) {
    val calc = settlement.calculation

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = stringResource(R.string.result_calculation_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = PrimaryText
        )
        TippGroupDetailPanel {
            when (settlement.phase) {
                TippGroupV2SettlementPhase.WAITING_RESULT -> {
                    ResultCalculationTextRow(stringResource(R.string.result_waiting_final))
                }
                TippGroupV2SettlementPhase.NO_ENTRIES -> {
                    ResultCalculationTextRow(stringResource(R.string.result_no_entries))
                }
                TippGroupV2SettlementPhase.FINISHED_NO_WINNERS -> {
                    ResultCalculationTextRow(stringResource(R.string.result_no_winner))
                    calc?.let {
                        ResultCalculationTextRow(
                            stringResource(R.string.result_current_pot, it.currentPot.toEuroLabel())
                        )
                        if (it.carryForwardJackpot > 0.0) {
                            ResultCalculationTextRow(
                                stringResource(
                                    R.string.result_jackpot_carry,
                                    it.carryForwardJackpot.toEuroLabel()
                                )
                            )
                        }
                        if (it.localReturnedAmount > 0.0) {
                            ResultCalculationTextRow(
                                stringResource(
                                    R.string.result_local_returned,
                                    it.localReturnedAmount.toEuroLabel()
                                )
                            )
                        }
                    }
                }
                TippGroupV2SettlementPhase.FINISHED_WINNERS -> {
                    calc?.let {
                        ResultCalculationTextRow(
                            stringResource(R.string.result_current_pot, it.currentPot.toEuroLabel())
                        )
                        ResultCalculationTextRow(
                            stringResource(
                                R.string.result_current_share,
                                it.currentSharePerWinner.toEuroLabel()
                            )
                        )
                        if (it.jackpotPot > 0.0) {
                            ResultCalculationTextRow(
                                stringResource(R.string.result_jackpot_pot, it.jackpotPot.toEuroLabel())
                            )
                        }
                        if (it.jackpotWinners.isNotEmpty()) {
                            ResultCalculationTextRow(
                                stringResource(
                                    R.string.result_jackpot_share,
                                    it.jackpotSharePerWinner.toEuroLabel()
                                )
                            )
                        }
                    }
                    if (settlement.winnerLines.isNotEmpty()) {
                        TippGroupDetailStatDivider()
                        Text(
                            text = stringResource(R.string.result_winners_title),
                            modifier = Modifier.padding(vertical = 10.dp),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = SecondaryText
                        )
                        settlement.winnerLines.forEach { line ->
                            Text(
                                text = winnerLineLabel(line),
                                modifier = Modifier.padding(bottom = 8.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = JackpotGold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultCalculationTextRow(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        style = MaterialTheme.typography.bodyMedium,
        color = PrimaryText
    )
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

@Composable
private fun EntryBulkSelectionBar(
    selectedCount: Int,
    onCancel: () -> Unit,
    onDeleteSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(TippGroupDetailPanelShape)
            .background(Surface)
            .border(1.dp, Divider.copy(alpha = 0.85f), TippGroupDetailPanelShape)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.entry_selection_count, selectedCount),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryText
        )
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            TextButton(onClick = onCancel) {
                Text(
                    text = stringResource(R.string.cancel),
                    color = SecondaryText
                )
            }
            if (selectedCount > 0) {
                TextButton(onClick = onDeleteSelected) {
                    Text(
                        text = stringResource(R.string.delete),
                        color = DangerRed
                    )
                }
            }
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
