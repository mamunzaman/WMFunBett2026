package com.example.wmfunbett2026.ui.screens.tournament

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
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
import com.example.wmfunbett2026.data.model.toEuroLabel
import com.example.wmfunbett2026.data.repository.FunBettRepository
import com.example.wmfunbett2026.data.winner.TippGroupWinnerEngine
import com.example.wmfunbett2026.data.winner.TippGroupWinnerOutcome
import com.example.wmfunbett2026.ui.components.DeleteConfirmDialog
import com.example.wmfunbett2026.ui.components.DetailInlineAddButton
import com.example.wmfunbett2026.ui.components.FormBottomSheet
import com.example.wmfunbett2026.ui.components.HierarchyListContentPadding
import com.example.wmfunbett2026.ui.components.HierarchyScreenLayout
import com.example.wmfunbett2026.ui.components.HierarchySectionHeader
import com.example.wmfunbett2026.ui.components.MatchStatusBadge
import com.example.wmfunbett2026.ui.components.SampleDataNotice
import com.example.wmfunbett2026.ui.components.SetResultDialog
import com.example.wmfunbett2026.ui.components.TippGroupListCard
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
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showSetResultDialog by remember { mutableStateOf(false) }
    var showSampleAddTipp by remember { mutableStateOf(false) }

    LaunchedEffect(gameId) {
        showDeleteDialog = false
        showSetResultDialog = false
        showSampleAddTipp = false
    }

    val game = FunBettRepository.getGameInDay(dayId, gameId)
    val day = FunBettRepository.getDay(dayId)
    val round = FunBettRepository.getRound(roundId)
    val tippGroups = FunBettRepository.getTippGroups(gameId)
    val carryItems = remember(round, gameId, FunBettRepository.dataVersion.intValue) {
        if (round != null) JackpotChainCalculator.calculateCarryItems(round, gameId) else emptyList()
    }
    val totalPeople = tippGroups.sumOf { it.entries.size }
    val totalMoney = game?.totalKasse ?: 0.0

    HierarchyScreenLayout(
        title = game?.displayName ?: "Game",
        breadcrumbs = HierarchyLabels.forGameDetail(roundId, gameId),
        onBackClick = onBackClick,
        onSetResultClick = if (game != null) {{ showSetResultDialog = true }} else null,
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
            contentPadding = hierarchyContentPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(key = "notice") { SampleDataNotice() }
            item(key = "game_info") {
                GameMatchOverviewCard(
                    game = game,
                    dayLabel = day?.name,
                    tippGroupCount = tippGroups.size,
                    totalPeople = totalPeople,
                    totalMoneyLabel = totalMoney.toEuroLabel(),
                    carryLabel = buildCarryLabel(carryItems),
                    tippGroups = tippGroups,
                    onAddTippClick = { showSampleAddTipp = true }
                )
            }
            item(key = "section") { HierarchySectionHeader(title = "Tipp Groups") }
            if (tippGroups.isEmpty()) {
                item(key = "empty") {
                    Text(
                        text = "No tipp groups yet — use Add Tipp in the overview",
                        style = MaterialTheme.typography.bodyLarge,
                        color = SecondaryText
                    )
                }
            } else {
                items(tippGroups, key = { it.id }) { tippGroup ->
                    val outcome = TippGroupWinnerEngine.calculate(game, tippGroup)
                    TippGroupListCard(
                        title = tippGroup.title,
                        scopeLabel = tippGroup.timeScope.label,
                        peopleCount = tippGroup.entries.size,
                        entryAmountLabel = entryAmountLabel(tippGroup),
                        totalAmountLabel = tippGroup.totalAmount.toEuroLabel(),
                        winnerStatusLabel = winnerStatusLabel(outcome),
                        onClick = { onTippGroupClick(tippGroup.id) }
                    )
                }
            }
        }
    }

    if (showSampleAddTipp) {
        FormBottomSheet(
            title = "Add Tipp",
            onDismiss = { showSampleAddTipp = false },
            primaryActionLabel = stringResource(R.string.ok),
            onPrimaryAction = { showSampleAddTipp = false },
            showCancel = false
        ) {
            Text(
                text = "Sample action only — tipp creation will be wired in a later step.",
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryText
            )
        }
    }

    if (showSetResultDialog && game != null) {
        SetResultDialog(
            game = game,
            onDismiss = { showSetResultDialog = false },
            onSave = { teamAScore, teamBScore, status ->
                FunBettRepository.updateGameResult(gameId, teamAScore, teamBScore, status)
                showSetResultDialog = false
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameMatchOverviewCard(
    game: Game,
    dayLabel: String?,
    tippGroupCount: Int,
    totalPeople: Int,
    totalMoneyLabel: String,
    carryLabel: String?,
    tippGroups: List<TippGroup>,
    onAddTippClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val scheduleLabel = listOfNotNull(dayLabel, game.dateTimeLabel).joinToString(" · ")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${game.teamA}  vs  ${game.teamB}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                    Text(
                        text = scheduleLabel,
                        modifier = Modifier.padding(top = 4.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = SecondaryText
                    )
                }
                MatchStatusBadge(status = game.status)
            }
            Text(
                text = game.resultDisplayText(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = if (game.hasResult) PrimaryText else SecondaryText
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OverviewStat(label = "Tipps", value = tippGroupCount.toString())
                OverviewStat(label = "People", value = totalPeople.toString())
                OverviewStat(label = "Total", value = totalMoneyLabel, highlight = true)
            }
            if (carryLabel != null) {
                Text(
                    text = carryLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = JackpotGold.copy(alpha = 0.9f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (expanded) "Hide overview" else "Tap for overview",
                    style = MaterialTheme.typography.labelMedium,
                    color = SecondaryText
                )
                DetailInlineAddButton(
                    label = "Add Tipp",
                    onClick = onAddTippClick
                )
            }
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HorizontalDivider(color = SecondaryText.copy(alpha = 0.2f))
                    Text(
                        text = "Overview",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryText
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OverviewStat(label = "Tipp Groups", value = tippGroupCount.toString())
                        OverviewStat(label = "Entries", value = totalPeople.toString())
                        OverviewStat(label = "Collected", value = totalMoneyLabel, highlight = true)
                    }
                    if (tippGroups.isNotEmpty()) {
                        Text(
                            text = "Per Tipp Group",
                            style = MaterialTheme.typography.labelLarge,
                            color = SecondaryText
                        )
                        tippGroups.forEach { group ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = group.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = PrimaryText,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "${group.entries.size} · ${group.totalAmount.toEuroLabel()}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = SecondaryText
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OverviewStat(
    label: String,
    value: String,
    highlight: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = SecondaryText
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = if (highlight) JackpotGold else PrimaryText
        )
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

private fun buildCarryLabel(carryItems: List<com.example.wmfunbett2026.data.jackpot.JackpotCarryItem>): String? {
    if (carryItems.isEmpty()) return null
    val total = carryItems.sumOf { it.requiredAmountPerPerson }
    return "Jackpot carry-in: ${total.toEuroLabel()} per person"
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
