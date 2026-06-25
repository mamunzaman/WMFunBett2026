package com.example.wmfunbett2026.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import com.example.wmfunbett2026.ui.designsystem.buttons.AppMenuButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.data.model.Entry
import com.example.wmfunbett2026.data.model.Game
import com.example.wmfunbett2026.data.model.MatchStatus
import com.example.wmfunbett2026.data.model.TippGroupEntryBlockReason
import com.example.wmfunbett2026.data.model.TippGroupSettlementSummary
import com.example.wmfunbett2026.data.model.toEuroLabel
import com.example.wmfunbett2026.ui.designsystem.feedback.AppInfoMessage
import com.example.wmfunbett2026.ui.designsystem.feedback.AppInfoMessageStyle
import com.example.wmfunbett2026.ui.matchcenter.shouldShowEntryWinnerShare
import com.example.wmfunbett2026.ui.theme.DangerRed
import com.example.wmfunbett2026.ui.theme.DarkNavy
import com.example.wmfunbett2026.ui.theme.Divider
import com.example.wmfunbett2026.ui.theme.GlassBorder
import com.example.wmfunbett2026.ui.theme.JackpotGold
import com.example.wmfunbett2026.ui.theme.MatchCardCompactSurface
import com.example.wmfunbett2026.ui.theme.PrimaryBlue
import com.example.wmfunbett2026.ui.theme.PrimaryBlueBright
import com.example.wmfunbett2026.ui.theme.Surface
import com.example.wmfunbett2026.ui.theme.TextMuted
import com.example.wmfunbett2026.ui.theme.TextPrimary
import com.example.wmfunbett2026.ui.theme.TextSecondary
import com.example.wmfunbett2026.ui.theme.WinnerGreen
import kotlinx.coroutines.delay

private val EntryListCardShape = RoundedCornerShape(18.dp)
private val EntryListSpacing = 12.dp
private val EntryDetailPanelShape = RoundedCornerShape(14.dp)
private val EntryAvatarSize = 38.dp
private val EntryCollapsedMinHeight = 60.dp
private const val EntryCardPressScale = 0.985f
private const val EntryExpandAnimMs = 200
private const val EntryBringIntoViewDelayMs = 300L

@Composable
fun TippGroupEntryTable(
    game: Game,
    entries: List<Entry>,
    winningEntryIds: Set<String>,
    winnerNames: List<String>,
    settlement: TippGroupSettlementSummary,
    onEntryClick: (Entry) -> Unit,
    onEditEntry: (Entry) -> Unit,
    onDeleteEntry: (Entry) -> Unit,
    modifier: Modifier = Modifier,
    selectionMode: Boolean = false,
    selectedEntryIds: Set<String> = emptySet(),
    expandedEntryId: String? = null,
    onExpandedEntryIdChange: (String?) -> Unit = {},
    onToggleEntrySelection: (Entry) -> Unit = {},
    onEntryLongPress: (Entry) -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(EntryListSpacing)
    ) {
        if (winnerNames.isNotEmpty()) {
            EntryWinnerSummaryRow(winnerCount = winnerNames.size)
        }
        entries.forEach { entry ->
            TippGroupEntryListItem(
                game = game,
                entry = entry,
                winningEntryIds = winningEntryIds,
                settlement = settlement,
                selectionMode = selectionMode,
                selectedEntryIds = selectedEntryIds,
                expanded = expandedEntryId == entry.id,
                onExpandedChange = { shouldExpand ->
                    onExpandedEntryIdChange(if (shouldExpand) entry.id else null)
                },
                onEditEntry = onEditEntry,
                onDeleteEntry = onDeleteEntry,
                onToggleEntrySelection = onToggleEntrySelection,
                onEntryLongPress = onEntryLongPress
            )
        }
    }
}

@Composable
fun TippGroupEntryListItem(
    game: Game,
    entry: Entry,
    winningEntryIds: Set<String>,
    settlement: TippGroupSettlementSummary,
    onEditEntry: (Entry) -> Unit,
    onDeleteEntry: (Entry) -> Unit,
    modifier: Modifier = Modifier,
    selectionMode: Boolean = false,
    selectedEntryIds: Set<String> = emptySet(),
    expanded: Boolean = false,
    onExpandedChange: (Boolean) -> Unit = {},
    onToggleEntrySelection: (Entry) -> Unit = {},
    onEntryLongPress: (Entry) -> Unit = {}
) {
    val matchStatus = game.status
    val scoreDash = stringResource(R.string.entry_table_score_dash)
    val currentScoreLabel = when (matchStatus) {
        MatchStatus.NOT_STARTED -> scoreDash
        else -> game.compactScoreOrNull() ?: scoreDash
    }
    val isWinner = entry.id in winningEntryIds
    val winnerShareAmountLabel = if (shouldShowEntryWinnerShare(game, isWinner, settlement)) {
        settlement.sharePerWinner.toEuroLabel()
    } else {
        null
    }

    EntryCard(
        name = entry.friendName,
        prediction = entry.prediction,
        amountLabel = entry.amount.toEuroLabel(),
        note = entry.note?.takeIf { it.isNotBlank() },
        matchStatus = matchStatus,
        currentScoreLabel = currentScoreLabel,
        isWinner = isWinner,
        winnerShareAmountLabel = winnerShareAmountLabel,
        selectionMode = selectionMode,
        selected = entry.id in selectedEntryIds,
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier,
        onClick = if (selectionMode) {
            { onToggleEntrySelection(entry) }
        } else {
            null
        },
        onLongClick = if (!selectionMode) {
            { onEntryLongPress(entry) }
        } else {
            null
        },
        onEditClick = if (!selectionMode) {
            { onEditEntry(entry) }
        } else {
            null
        },
        onDeleteClick = if (!selectionMode) {
            { onDeleteEntry(entry) }
        } else {
            null
        }
    )
}

@Composable
fun EntryWinnerSummaryRow(
    winnerCount: Int,
    modifier: Modifier = Modifier
) {
    val summary = if (winnerCount == 1) {
        stringResource(R.string.tipp_group_winner_count_one)
    } else {
        stringResource(R.string.tipp_group_winner_count_many, winnerCount)
    }

    Text(
        text = summary,
        modifier = modifier
            .fillMaxWidth()
            .clip(EntryListCardShape)
            .background(WinnerGreen.copy(alpha = 0.1f))
            .border(1.dp, WinnerGreen.copy(alpha = 0.28f), EntryListCardShape)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = WinnerGreen
    )
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun EntryCard(
    name: String,
    prediction: String,
    amountLabel: String,
    matchStatus: MatchStatus,
    currentScoreLabel: String,
    modifier: Modifier = Modifier,
    note: String? = null,
    isWinner: Boolean = false,
    winnerShareAmountLabel: String? = null,
    selectionMode: Boolean = false,
    selected: Boolean = false,
    expanded: Boolean = false,
    onExpandedChange: (Boolean) -> Unit = {},
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null
) {
    val expandedBringIntoViewRequester = remember { BringIntoViewRequester() }
    val expandSizeSpec = tween<IntSize>(durationMillis = EntryExpandAnimMs, easing = FastOutSlowInEasing)
    val expandFadeSpec = tween<Float>(durationMillis = EntryExpandAnimMs, easing = FastOutSlowInEasing)

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) EntryCardPressScale else 1f,
        animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing),
        label = "entryCardPressScale"
    )

    val handleBodyClick: () -> Unit = {
        if (selectionMode) {
            onClick?.invoke()
        } else {
            onExpandedChange(!expanded)
        }
    }

    LaunchedEffect(expanded, selectionMode) {
        if (expanded && !selectionMode) {
            delay(EntryBringIntoViewDelayMs)
            expandedBringIntoViewRequester.bringIntoView()
        }
    }

    val cardBorderColor = when {
        selectionMode && selected -> PrimaryBlue.copy(alpha = 0.6f)
        isWinner -> WinnerGreen.copy(alpha = 0.22f)
        else -> GlassBorder
    }
    val cardBackground = when {
        selectionMode && selected -> PrimaryBlue.copy(alpha = 0.12f)
        isWinner -> WinnerGreen.copy(alpha = 0.06f)
        else -> Surface
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .shadow(
                elevation = 4.dp,
                shape = EntryListCardShape,
                ambientColor = Color.Black.copy(alpha = 0.32f),
                spotColor = Color.Black.copy(alpha = 0.22f)
            )
            .clip(EntryListCardShape)
            .background(cardBackground)
            .border(1.dp, cardBorderColor, EntryListCardShape)
            .then(
                when {
                    onLongClick != null -> Modifier.combinedClickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = handleBodyClick,
                        onLongClick = onLongClick
                    )
                    else -> Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = handleBodyClick
                    )
                }
            )
    ) {
        if (isWinner) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(3.dp)
                    .fillMaxHeight()
                    .background(WinnerGreen.copy(alpha = 0.65f))
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(animationSpec = expandSizeSpec)
                .padding(
                    start = if (isWinner) 13.dp else 14.dp,
                    end = 12.dp,
                    top = if (expanded) 14.dp else 10.dp,
                    bottom = 10.dp
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = if (expanded) 0.dp else EntryCollapsedMinHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectionMode) {
                    Checkbox(
                        checked = selected,
                        onCheckedChange = { onClick?.invoke() },
                        colors = CheckboxDefaults.colors(
                            checkedColor = PrimaryBlue,
                            checkmarkColor = TextPrimary,
                            uncheckedColor = TextSecondary
                        ),
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
                EntryPremiumAvatar(
                    initials = friendDisplayInitials(name),
                    modifier = Modifier.padding(end = 10.dp)
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!expanded) {
                        EntryCollapsedInfoLine(
                            note = note,
                            prediction = prediction,
                            isWinner = isWinner,
                            winnerShareAmountLabel = winnerShareAmountLabel
                        )
                    }
                }

                if (!expanded) {
                    Text(
                        text = amountLabel,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = JackpotGold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(start = 8.dp, end = 4.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowUp,
                        contentDescription = null,
                        tint = TextMuted.copy(alpha = 0.75f),
                        modifier = Modifier
                            .padding(start = 8.dp, end = 4.dp)
                            .size(18.dp)
                    )
                }

                if (onEditClick != null || onDeleteClick != null) {
                    EntryRowOverflowMenu(
                        onEdit = onEditClick,
                        onDelete = onDeleteClick
                    )
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(animationSpec = expandSizeSpec) +
                    fadeIn(animationSpec = expandFadeSpec),
                exit = shrinkVertically(animationSpec = expandSizeSpec) +
                    fadeOut(animationSpec = expandFadeSpec)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    HorizontalDivider(
                        modifier = Modifier.padding(top = 14.dp, bottom = 10.dp),
                        color = Divider.copy(alpha = 0.35f),
                        thickness = 0.5.dp
                    )
                    EntryExpandedDetailPanel(
                        predictionScore = formatEntryScoreDisplay(prediction),
                        currentScore = formatEntryScoreDisplay(currentScoreLabel),
                        statusLabel = entryMatchStatusLabel(matchStatus),
                        stakeLabel = amountLabel,
                        winnerShareAmountLabel = winnerShareAmountLabel
                    )
                    Spacer(
                        modifier = Modifier
                            .height(1.dp)
                            .bringIntoViewRequester(expandedBringIntoViewRequester)
                    )
                }
            }
        }
    }
}

@Composable
private fun EntryCollapsedInfoLine(
    note: String?,
    prediction: String,
    isWinner: Boolean,
    winnerShareAmountLabel: String?,
    modifier: Modifier = Modifier
) {
    val winnerLabel = stringResource(R.string.entry_winner_badge)
    val separator = " · "

    Text(
        text = buildAnnotatedString {
            note?.takeIf { it.isNotBlank() }?.let { noteText ->
                withStyle(SpanStyle(color = TextMuted)) {
                    append(noteText)
                }
                withStyle(SpanStyle(color = TextMuted)) {
                    append(separator)
                }
            }
            if (isWinner) {
                if (winnerShareAmountLabel != null) {
                    withStyle(
                        SpanStyle(
                            color = JackpotGold,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append("$winnerLabel $winnerShareAmountLabel")
                    }
                } else {
                    withStyle(
                        SpanStyle(
                            color = WinnerGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append(winnerLabel)
                    }
                }
                withStyle(SpanStyle(color = TextMuted)) {
                    append(separator)
                }
            }
            withStyle(
                SpanStyle(
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(prediction)
            }
        },
        modifier = modifier.padding(top = 3.dp),
        style = MaterialTheme.typography.labelMedium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun EntryExpandedDetailPanel(
    predictionScore: String,
    currentScore: String,
    statusLabel: String,
    stakeLabel: String,
    winnerShareAmountLabel: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(EntryDetailPanelShape)
            .background(MatchCardCompactSurface)
            .border(1.dp, GlassBorder, EntryDetailPanelShape)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            EntryDetailScoreColumn(
                label = stringResource(R.string.prediction),
                score = predictionScore,
                modifier = Modifier.weight(1f)
            )
            EntryDetailScoreColumn(
                label = stringResource(R.string.entry_table_current),
                score = currentScore,
                statusLabel = statusLabel,
                modifier = Modifier.weight(1f)
            )
        }

        HorizontalDivider(
            color = Divider.copy(alpha = 0.22f),
            thickness = 0.5.dp
        )

        EntryDetailMoneyRow(
            label = stringResource(R.string.entry_card_stake),
            amount = stakeLabel
        )

        winnerShareAmountLabel?.let { shareAmount ->
            EntryDetailMoneyRow(
                label = stringResource(R.string.entry_detail_winner_share),
                amount = shareAmount
            )
        }
    }
}

@Composable
private fun EntryDetailScoreColumn(
    label: String,
    score: String,
    modifier: Modifier = Modifier,
    statusLabel: String? = null
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = score,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        statusLabel?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun EntryDetailMoneyRow(
    label: String,
    amount: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = TextMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = amount,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = JackpotGold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun entryMatchStatusLabel(matchStatus: MatchStatus): String {
    return when (matchStatus) {
        MatchStatus.NOT_STARTED -> stringResource(R.string.status_not_started)
        MatchStatus.LIVE -> stringResource(R.string.status_live)
        MatchStatus.FINISHED -> stringResource(R.string.status_finished)
    }
}

private fun formatEntryScoreDisplay(score: String): String {
    val trimmed = score.trim()
    if (':' in trimmed) {
        return trimmed.replace(":", " : ")
    }
    return trimmed
}

@Composable
private fun EntryPremiumAvatar(
    initials: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 3.dp,
                shape = CircleShape,
                ambientColor = PrimaryBlue.copy(alpha = 0.18f),
                spotColor = PrimaryBlue.copy(alpha = 0.24f)
            )
            .size(EntryAvatarSize)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        PrimaryBlue.copy(alpha = 0.48f),
                        PrimaryBlue.copy(alpha = 0.24f),
                        DarkNavy.copy(alpha = 0.72f)
                    )
                )
            )
            .border(1.dp, PrimaryBlueBright.copy(alpha = 0.4f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            fontSize = 13.sp,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun EntryRowOverflowMenu(
    onEdit: (() -> Unit)?,
    onDelete: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        AppMenuButton(
            onClick = { expanded = true },
            filled = false,
            iconTint = TextSecondary,
            buttonSize = 32.dp
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            onEdit?.let { edit ->
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.action_edit_entry)) },
                    onClick = {
                        expanded = false
                        edit()
                    }
                )
            }
            onDelete?.let { delete ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(R.string.action_delete_entry),
                            color = DangerRed
                        )
                    },
                    onClick = {
                        expanded = false
                        delete()
                    }
                )
            }
        }
    }
}

private val EntryInfoCardShape = RoundedCornerShape(14.dp)

@Composable
private fun EntryStatusInfoCard(
    title: String,
    message: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    accentGold: Boolean = false
) {
    AppInfoMessage(
        title = title,
        message = message,
        icon = icon,
        modifier = modifier,
        style = if (accentGold) AppInfoMessageStyle.Accent else AppInfoMessageStyle.Info
    )
}

@Composable
fun AllFriendsJoinedInfoCard(
    modifier: Modifier = Modifier
) {
    EntryStatusInfoCard(
        title = stringResource(R.string.add_entry_all_friends_joined_title),
        message = stringResource(R.string.add_entry_all_friends_joined_message),
        icon = Icons.Outlined.Groups,
        modifier = modifier,
        accentGold = true
    )
}

@Composable
fun TippCreationClosedInfoCard(
    modifier: Modifier = Modifier
) {
    EntryStatusInfoCard(
        title = stringResource(R.string.tipp_creation_closed_title),
        message = stringResource(R.string.tipp_creation_closed_message),
        icon = Icons.Outlined.Lock,
        modifier = modifier
    )
}

@Composable
fun EntryClosedInfoCard(
    reason: TippGroupEntryBlockReason,
    modifier: Modifier = Modifier
) {
    when (reason) {
        TippGroupEntryBlockReason.MATCH_LIVE -> {
            EntryStatusInfoCard(
                title = stringResource(R.string.entry_closed_title),
                message = stringResource(R.string.entry_closed_live_message),
                icon = Icons.Outlined.Lock,
                modifier = modifier
            )
        }
        TippGroupEntryBlockReason.MATCH_FINISHED -> {
            EntryStatusInfoCard(
                title = stringResource(R.string.entry_closed_title),
                message = stringResource(R.string.entry_closed_finished_message),
                icon = Icons.Outlined.Lock,
                modifier = modifier
            )
        }
        TippGroupEntryBlockReason.ALL_FRIENDS_JOINED -> {
            AllFriendsJoinedInfoCard(modifier = modifier)
        }
    }
}

@Composable
fun EntryRowActionSheet(
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSelectMultiple: () -> Unit
) {
    FormActionMenuSheet(
        title = stringResource(R.string.entry_actions_title),
        onDismiss = onDismiss,
        actions = listOf(
            FormActionMenuItem(
                label = stringResource(R.string.action_edit_entry),
                onClick = onEdit
            ),
            FormActionMenuItem(
                label = stringResource(R.string.action_delete_entry),
                onClick = onDelete,
                destructive = true
            ),
            FormActionMenuItem(
                label = stringResource(R.string.action_select_multiple_entries),
                onClick = onSelectMultiple
            )
        )
    )
}

@Composable
fun EntryBlockedInfoSheet(
    reason: TippGroupEntryBlockReason,
    onDismiss: () -> Unit
) {
    val sheetTitle = when (reason) {
        TippGroupEntryBlockReason.MATCH_LIVE,
        TippGroupEntryBlockReason.MATCH_FINISHED -> stringResource(R.string.entry_closed_title)
        TippGroupEntryBlockReason.ALL_FRIENDS_JOINED ->
            stringResource(R.string.add_entry_all_friends_joined_title)
    }
    FormBottomSheet(
        title = sheetTitle,
        onDismiss = onDismiss,
        primaryActionLabel = stringResource(R.string.ok),
        onPrimaryAction = onDismiss,
        showCancel = false
    ) {
        EntryClosedInfoCard(
            reason = reason,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AllFriendsJoinedInfoSheet(
    onDismiss: () -> Unit
) {
    FormBottomSheet(
        title = stringResource(R.string.add_entry_all_friends_joined_title),
        onDismiss = onDismiss,
        primaryActionLabel = stringResource(R.string.ok),
        onPrimaryAction = onDismiss,
        showCancel = false
    ) {
        AllFriendsJoinedInfoCard(modifier = Modifier.fillMaxWidth())
    }
}
