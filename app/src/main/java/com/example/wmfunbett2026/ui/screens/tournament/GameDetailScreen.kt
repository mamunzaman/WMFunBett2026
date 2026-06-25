package com.example.wmfunbett2026.ui.screens.tournament

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.data.jackpot.JackpotChainCalculator
import com.example.wmfunbett2026.data.model.Game
import com.example.wmfunbett2026.data.model.TippGroup
import com.example.wmfunbett2026.data.model.toEuroLabel
import com.example.wmfunbett2026.data.repository.FunBettRepository
import com.example.wmfunbett2026.data.winner.TippGroupWinnerEngine
import com.example.wmfunbett2026.data.winner.TippGroupWinnerOutcome
import com.example.wmfunbett2026.ui.components.AddTippGroupSheet
import com.example.wmfunbett2026.ui.components.DeleteConfirmDialog
import com.example.wmfunbett2026.ui.components.DetailInlineAddButton
import com.example.wmfunbett2026.ui.components.HierarchyListContentPadding
import com.example.wmfunbett2026.ui.components.HierarchyScreenLayout
import com.example.wmfunbett2026.ui.components.HierarchySectionHeader
import com.example.wmfunbett2026.ui.components.MatchCenterMatchCardBody
import com.example.wmfunbett2026.ui.components.MatchCenterMatchCardShell
import com.example.wmfunbett2026.ui.components.SampleDataNotice
import com.example.wmfunbett2026.ui.components.SetResultSheet
import com.example.wmfunbett2026.ui.components.TippGroupOverviewFooterSection
import com.example.wmfunbett2026.ui.components.TippGroupListCard
import com.example.wmfunbett2026.ui.components.TippGroupOverviewMiniCard
import com.example.wmfunbett2026.ui.components.hierarchyContentPadding
import com.example.wmfunbett2026.ui.navigation.HierarchyLabels
import com.example.wmfunbett2026.ui.theme.GlassBorder
import com.example.wmfunbett2026.ui.theme.JackpotGold
import com.example.wmfunbett2026.ui.theme.PrimaryText
import com.example.wmfunbett2026.ui.theme.SecondaryText
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
    val tippGroups = remember(gameId, FunBettRepository.dataVersion.intValue) {
        FunBettRepository.getTippGroups(gameId)
    }
    val carryItems = remember(round, gameId, FunBettRepository.dataVersion.intValue) {
        if (round != null) JackpotChainCalculator.calculateCarryItems(round, gameId) else emptyList()
    }
    val incomingJackpotMax = remember(roundId, gameId, FunBettRepository.dataVersion.intValue) {
        FunBettRepository.getGameIncomingJackpotMax(roundId, gameId)
    }
    val headerJackpotLabel = incomingJackpotMax.takeIf { it > 0.0 }?.toEuroLabel()
    val totalPeople = tippGroups.sumOf { it.entries.size }
    val totalMoney = game?.totalKasse ?: 0.0

    HierarchyScreenLayout(
        title = game?.displayName ?: "Game",
        breadcrumbs = HierarchyLabels.forGameDetail(roundId, gameId),
        onBackClick = onBackClick,
        onSetResultClick = if (game != null) {{ showSetResultDialog = true }} else null,
        onDeleteClick = if (game != null) {{ showDeleteDialog = true }} else null,
        jackpotAmountLabel = headerJackpotLabel,
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
                    incomingJackpotLabel = incomingJackpotMax.takeIf { it > 0 }?.toEuroLabel(),
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
        AddTippGroupSheet(
            gameId = gameId,
            onDismiss = { showSampleAddTipp = false },
            onCreate = { tippType, entryAmount, note ->
                FunBettRepository.addTippGroup(
                    gameId = gameId,
                    tippType = tippType,
                    entryAmount = entryAmount,
                    note = note
                )
                showSampleAddTipp = false
            }
        )
    }

    if (showSetResultDialog && game != null) {
        SetResultSheet(
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
            titleRes = R.string.delete_match_confirm_title,
            messageRes = R.string.delete_match_confirm_message,
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

private const val TippsOverviewSizeDurationMs = 280
private const val TippsOverviewFadeInDelayMs = 120
private const val TippsOverviewFadeInDurationMs = 180
private const val TippsOverviewFadeOutDurationMs = 80

private val GameMatchCardContentPadding = 16.dp

@Composable
private fun GameMatchOverviewCard(
    game: Game,
    dayLabel: String?,
    incomingJackpotLabel: String?,
    tippGroupCount: Int,
    totalPeople: Int,
    totalMoneyLabel: String,
    carryLabel: String?,
    tippGroups: List<TippGroup>,
    onAddTippClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val matchdayLabel = dayLabel ?: "Matchday"

    MatchCenterMatchCardShell(game = game, modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clipToBounds()
                .animateContentSize(
                    animationSpec = tween(
                        durationMillis = TippsOverviewSizeDurationMs,
                        easing = FastOutSlowInEasing
                    )
                )
                .clickable { expanded = !expanded }
        ) {
            MatchCenterMatchCardBody(
                game = game,
                matchdayLabel = matchdayLabel,
                incomingJackpotLabel = incomingJackpotLabel
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = GameMatchCardContentPadding)
                    .padding(top = 4.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (expanded) {
                            Icons.Default.KeyboardArrowUp
                        } else {
                            Icons.Default.KeyboardArrowDown
                        },
                        contentDescription = null,
                        tint = SecondaryText,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (expanded) "Hide tipps overview" else "Show tipps overview",
                        style = MaterialTheme.typography.labelLarge,
                        color = SecondaryText
                    )
                }
                DetailInlineAddButton(
                    label = "Add Tipp",
                    onClick = onAddTippClick
                )
            }
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = TippsOverviewFadeInDurationMs,
                        delayMillis = TippsOverviewFadeInDelayMs,
                        easing = FastOutSlowInEasing
                    )
                ),
                exit = fadeOut(
                    animationSpec = tween(
                        durationMillis = TippsOverviewFadeOutDurationMs,
                        easing = FastOutSlowInEasing
                    )
                )
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    HorizontalDivider(color = GlassBorder, thickness = 1.dp)
                    TippsOverviewContent(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        contentPadding = GameMatchCardContentPadding,
                        game = game,
                        tippGroupCount = tippGroupCount,
                        totalPeople = totalPeople,
                        totalMoneyLabel = totalMoneyLabel,
                        carryLabel = carryLabel,
                        tippGroups = tippGroups
                    )
                }
            }
        }
    }
}

@Composable
private fun TippsOverviewContent(
    game: Game,
    tippGroupCount: Int,
    totalPeople: Int,
    totalMoneyLabel: String,
    carryLabel: String?,
    tippGroups: List<TippGroup>,
    contentPadding: Dp,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = contentPadding)
                .then(
                    if (tippGroups.isEmpty()) {
                        Modifier.padding(bottom = 16.dp)
                    } else {
                        Modifier
                    }
                ),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OverviewStat(label = "Tipp Groups", value = tippGroupCount.toString())
                OverviewStat(label = "Entries", value = totalPeople.toString())
                OverviewStat(label = "Collected", value = totalMoneyLabel, highlight = true)
            }
            if (carryLabel != null) {
                Text(
                    text = carryLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = JackpotGold.copy(alpha = 0.9f)
                )
            }
        }
        if (tippGroups.isNotEmpty()) {
            TippGroupOverviewFooterSection(contentHorizontalPadding = contentPadding) {
                tippGroups.forEachIndexed { index, group ->
                    if (index > 0) {
                        HorizontalDivider(
                            color = GlassBorder.copy(alpha = 0.7f),
                            thickness = 1.dp
                        )
                    }
                    val outcome = TippGroupWinnerEngine.calculate(game, group)
                    val entryLabel = entryAmountLabel(group).takeIf { it != "—" }
                    TippGroupOverviewMiniCard(
                        title = group.title,
                        scopeLabel = group.timeScope.label,
                        peopleCount = group.entries.size,
                        entryAmountLabel = entryLabel,
                        collectedLabel = group.totalAmount.toEuroLabel(),
                        statusLabel = winnerStatusLabel(outcome)
                    )
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
