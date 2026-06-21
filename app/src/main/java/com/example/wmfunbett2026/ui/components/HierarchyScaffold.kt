package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.ui.theme.BackgroundDeep
import com.example.wmfunbett2026.ui.theme.DangerRed
import com.example.wmfunbett2026.ui.theme.JackpotGold
import com.example.wmfunbett2026.ui.theme.PrimaryBlue
import com.example.wmfunbett2026.ui.theme.PrimaryText
import com.example.wmfunbett2026.ui.theme.SecondaryText
import com.example.wmfunbett2026.ui.theme.SurfaceDark

const val SAMPLE_DATA_NOTICE = "Offline mode · data saved in memory only"

val HierarchyListContentPadding = PaddingValues(
    start = 20.dp,
    end = 20.dp,
    top = 8.dp,
    bottom = MatchCenterBottomNavReservedHeight
)

fun hierarchyContentPadding(): PaddingValues {
    return PaddingValues(
        start = 20.dp,
        end = 20.dp,
        top = 12.dp,
        bottom = MatchCenterBottomNavReservedHeight
    )
}

@Composable
fun RegisterMatchCenterAddAction(action: (() -> Unit)?) {
    DisposableEffect(action) {
        MatchCenterAddAction.handler = action
        onDispose {
            if (MatchCenterAddAction.handler === action) {
                MatchCenterAddAction.handler = null
            }
        }
    }
}

@Composable
fun HierarchyScreenLayout(
    title: String,
    breadcrumbs: List<String>,
    onBackClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    onFabClick: (() -> Unit)? = null,
    fabContentDescription: String = "Add",
    showSearchIcon: Boolean = false,
    onSetResultClick: (() -> Unit)? = null,
    onWinnerShareSettingsClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    deleteEnabled: Boolean = true,
    content: @Composable (Modifier) -> Unit
) {
    RegisterMatchCenterAddAction(onFabClick)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDeep)
    ) {
        MatchCenterHeader(
            title = title,
            onBackClick = onBackClick,
            showSearchIcon = showSearchIcon,
            onSetResultClick = onSetResultClick,
            onWinnerShareSettingsClick = onWinnerShareSettingsClick,
            onDeleteClick = onDeleteClick,
            deleteEnabled = deleteEnabled
        )
        MatchCenterBreadcrumb(breadcrumbs = breadcrumbs)
        content(Modifier.fillMaxSize())
    }
}

@Composable
fun SampleDataNotice(modifier: Modifier = Modifier) {
    Text(
        text = SAMPLE_DATA_NOTICE,
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        style = MaterialTheme.typography.bodySmall,
        color = SecondaryText
    )
}

@Composable
fun HierarchySectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = PrimaryText
    )
}

@Composable
fun HierarchyBreadcrumb(
    breadcrumbs: List<String>,
    modifier: Modifier = Modifier
) {
    if (breadcrumbs.isEmpty()) return

    Text(
        text = breadcrumbs.joinToString("  ›  "),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        style = MaterialTheme.typography.labelMedium,
        color = SecondaryText,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun <T> HierarchyListContent(
    items: List<T>,
    emptyMessage: String,
    modifier: Modifier = Modifier,
    sectionTitle: String? = null,
    showSampleNotice: Boolean = true,
    key: (T) -> Any = { it.hashCode() },
    itemContent: @Composable (T) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = HierarchyListContentPadding,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (showSampleNotice) {
            item(key = "sample_notice") {
                SampleDataNotice()
            }
        }
        if (sectionTitle != null) {
            item(key = "section_header") {
                HierarchySectionHeader(title = sectionTitle)
            }
        }
        if (items.isEmpty()) {
            item(key = "empty_state") {
                PlaceholderCard(message = emptyMessage)
            }
        } else {
            items(items, key = key) { item ->
                itemContent(item)
            }
        }
    }
}

@Composable
fun DetailInlineAddButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = JackpotGold.copy(alpha = 0.18f),
            contentColor = JackpotGold
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun DetailStatChip(
    label: String,
    value: String,
    highlight: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = if (highlight) JackpotGold.copy(alpha = 0.12f) else SecondaryText.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = if (highlight) JackpotGold.copy(alpha = 0.35f) else SecondaryText.copy(alpha = 0.14f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
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

@Composable
fun TippGroupOverviewMiniCard(
    title: String,
    scopeLabel: String,
    peopleCount: Int,
    entryAmountLabel: String?,
    collectedLabel: String,
    statusLabel: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceDark.copy(alpha = 0.85f), RoundedCornerShape(14.dp))
            .border(1.dp, PrimaryBlue.copy(alpha = 0.18f), RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryText
            )
            DetailStatusChip(label = statusLabel)
        }
        Text(
            text = scopeLabel,
            style = MaterialTheme.typography.bodyMedium,
            color = SecondaryText
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            OverviewMiniRow(label = "Entries", value = "$peopleCount people")
            if (entryAmountLabel != null) {
                OverviewMiniRow(label = "Entry", value = "$entryAmountLabel / person")
            }
            OverviewMiniRow(label = "Collected", value = collectedLabel, highlight = true)
        }
    }
}

@Composable
private fun OverviewMiniRow(
    label: String,
    value: String,
    highlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = SecondaryText
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (highlight) JackpotGold else PrimaryText
        )
    }
}

@Composable
fun DetailStatusChip(
    label: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = label,
        modifier = modifier
            .background(
                color = SecondaryText.copy(alpha = 0.16f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Medium,
        color = PrimaryText
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TippGroupListCard(
    title: String,
    scopeLabel: String,
    peopleCount: Int,
    entryAmountLabel: String,
    totalAmountLabel: String,
    winnerStatusLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                DetailStatusChip(label = winnerStatusLabel)
            }
            Text(
                text = scopeLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryText
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$peopleCount people",
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText
                )
                Text(
                    text = "Entry $entryAmountLabel",
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText
                )
                Text(
                    text = totalAmountLabel,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = JackpotGold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavListCard(
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = SurfaceDark),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    modifier = Modifier.padding(top = 4.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryText
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryListCard(
    name: String,
    prediction: String,
    amount: String,
    statusLabel: String,
    note: String? = null,
    roundStakeLabel: String? = null,
    isWinner: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val cardColors = CardDefaults.cardColors(
        containerColor = if (isWinner) JackpotGold.copy(alpha = 0.12f) else SurfaceDark
    )
    val cardModifier = modifier
        .fillMaxWidth()
        .then(
            if (isWinner) {
                Modifier.border(
                    width = 1.5.dp,
                    color = JackpotGold.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(16.dp)
                )
            } else {
                Modifier.border(
                    width = 1.dp,
                    color = PrimaryBlue.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        )

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = cardModifier,
            shape = RoundedCornerShape(16.dp),
            colors = cardColors,
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            EntryListCardContent(
                name = name,
                prediction = prediction,
                amount = amount,
                statusLabel = statusLabel,
                note = note,
                roundStakeLabel = roundStakeLabel
            )
        }
    } else {
        Card(
            modifier = cardModifier,
            shape = RoundedCornerShape(16.dp),
            colors = cardColors,
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            EntryListCardContent(
                name = name,
                prediction = prediction,
                amount = amount,
                statusLabel = statusLabel,
                note = note,
                roundStakeLabel = roundStakeLabel
            )
        }
    }
}

@Composable
private fun EntryListCardContent(
    name: String,
    prediction: String,
    amount: String,
    statusLabel: String,
    note: String?,
    roundStakeLabel: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
                DetailStatusChip(label = statusLabel)
            }
            Text(
                text = prediction,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryText.copy(alpha = 0.92f)
            )
            if (roundStakeLabel != null) {
                Text(
                    text = roundStakeLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText
                )
            }
            if (!note.isNullOrBlank()) {
                Text(
                    text = note,
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText.copy(alpha = 0.85f)
                )
            }
        }
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = amount,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = JackpotGold,
            fontSize = 22.sp
        )
    }
}
