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
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.EmojiEvents
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
import com.example.wmfunbett2026.data.model.TippGroupEntryBlockReason
import com.example.wmfunbett2026.data.model.TippGroupSettlementStatus
import com.example.wmfunbett2026.data.model.TippGroupSettlementSummary
import com.example.wmfunbett2026.data.model.toEuroLabel
import com.example.wmfunbett2026.data.repository.FunBettRepository
import com.example.wmfunbett2026.ui.components.AddEntrySheet
import com.example.wmfunbett2026.ui.components.DeleteConfirmDialog
import com.example.wmfunbett2026.ui.components.EditEntrySheet
import com.example.wmfunbett2026.ui.components.EntryBlockedInfoSheet
import com.example.wmfunbett2026.ui.components.EntryClosedInfoCard
import com.example.wmfunbett2026.ui.components.TippGroupEntryTable
import com.example.wmfunbett2026.ui.components.FormBottomSheet
import com.example.wmfunbett2026.ui.components.GlassPrimaryActionButton
import com.example.wmfunbett2026.ui.components.GlassScopePill
import com.example.wmfunbett2026.ui.components.HierarchyListContentPadding
import com.example.wmfunbett2026.ui.components.HierarchyScreenLayout
import com.example.wmfunbett2026.ui.components.HierarchySectionHeader
import com.example.wmfunbett2026.ui.components.MatchCenterTippGroupDetailMatchCard
import com.example.wmfunbett2026.ui.components.hierarchyContentPadding
import com.example.wmfunbett2026.ui.matchcenter.tippGroupWinnerNames
import com.example.wmfunbett2026.ui.matchcenter.tippGroupWinningEntryIds
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
    var selectedPersonName by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(tippGroupId, gameId) {
        showDeleteGroupDialog = false
        showAddEntry = false
        showEntryBlockedInfo = null
        editingEntry = null
        deletingEntry = null
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
    val settlement = remember(roundId, gameId, tippGroupId, FunBettRepository.dataVersion.intValue) {
        FunBettRepository.getTippGroupSettlementSummary(roundId, gameId, tippGroupId)
    }
    val jackpotSummary = remember(roundId, gameId, tippGroupId, FunBettRepository.dataVersion.intValue) {
        FunBettRepository.getJackpotCarryOverSummary(roundId, gameId, tippGroupId)
    }
    val matchdayLabel = remember(dayId, FunBettRepository.dataVersion.intValue) {
        FunBettRepository.getDay(dayId)?.name ?: "Matchday"
    }

    HierarchyScreenLayout(
        title = tippGroup?.title ?: "Tipp Group",
        breadcrumbs = HierarchyLabels.forTippGroupDetail(roundId, gameId, tippGroupId),
        onBackClick = onBackClick,
        onDeleteClick = if (tippGroup != null) {{ showDeleteGroupDialog = true }} else null,
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
            settlement?.let { summary ->
                jackpotSummary?.let { jackpot ->
                    item(key = "settlement") {
                        TippGroupSettlementSection(
                            summary = summary,
                            jackpot = jackpot
                        )
                    }
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
                        onEntryClick = { entry -> selectedPersonName = entry.friendName },
                        onEditEntry = { entry -> editingEntry = entry },
                        onDeleteEntry = { entry -> deletingEntry = entry }
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

    if (showAddEntry) {
        AddEntrySheet(
            tippGroupId = tippGroupId,
            onDismiss = { showAddEntry = false },
            onCreate = { firstName, lastName, prediction, note ->
                if (FunBettRepository.addEntryToTippGroup(
                        tippGroupId = tippGroupId,
                        firstName = firstName,
                        lastName = lastName,
                        prediction = prediction,
                        note = note
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
private fun TippGroupSettlementSection(
    summary: TippGroupSettlementSummary,
    jackpot: JackpotCarryOverSummary,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = stringResource(R.string.settlement_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = PrimaryText
        )
        TippGroupDetailPanel {
            TippGroupDetailStatRow(
                label = stringResource(R.string.settlement_incoming_jackpot),
                value = jackpot.incomingJackpot.toEuroLabel(),
                icon = Icons.Outlined.EmojiEvents,
                highlightValue = jackpot.incomingJackpot > 0
            )
            TippGroupDetailStatDivider()
            TippGroupDetailStatRow(
                label = stringResource(R.string.settlement_collected),
                value = jackpot.currentCollected.toEuroLabel(),
                icon = Icons.Outlined.AccountBalanceWallet
            )
            when (summary.status) {
                TippGroupSettlementStatus.PENDING -> {
                    TippGroupDetailStatDivider()
                    TippGroupDetailStatRow(
                        label = stringResource(R.string.settlement_total_pot),
                        value = jackpot.totalPot.toEuroLabel(),
                        icon = Icons.Outlined.Savings,
                        highlightValue = true
                    )
                    TippGroupDetailStatDivider()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.settlement_pending_title),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryText
                        )
                        Text(
                            text = stringResource(R.string.settlement_pending_message),
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryText
                        )
                    }
                }
                TippGroupSettlementStatus.NO_WINNERS -> {
                    TippGroupDetailStatDivider()
                    TippGroupDetailStatRow(
                        label = stringResource(R.string.settlement_carried_forward),
                        value = jackpot.carriedOut.toEuroLabel(),
                        icon = Icons.Outlined.Savings,
                        highlightValue = true
                    )
                }
                TippGroupSettlementStatus.WINNERS -> {
                    TippGroupDetailStatDivider()
                    TippGroupDetailStatRow(
                        label = stringResource(R.string.settlement_total_pot),
                        value = jackpot.totalPot.toEuroLabel(),
                        icon = Icons.Outlined.Savings,
                        highlightValue = true
                    )
                    TippGroupDetailStatDivider()
                    TippGroupDetailStatRow(
                        label = stringResource(R.string.settlement_winners),
                        value = jackpot.winnerCount.toString(),
                        icon = Icons.Outlined.Groups
                    )
                    TippGroupDetailStatDivider()
                    TippGroupDetailStatRow(
                        label = stringResource(R.string.settlement_share_per_winner),
                        value = jackpot.sharePerWinner.toEuroLabel(),
                        icon = Icons.Outlined.EmojiEvents,
                        highlightValue = true
                    )
                }
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
