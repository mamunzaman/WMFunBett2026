package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wmfunbett2026.ui.matchcenter.MatchLeagueQuickFilter
import com.example.wmfunbett2026.ui.matchcenter.MatchTimeQuickFilter
import com.example.wmfunbett2026.ui.matchcenter.matchLeagueQuickFilters
import com.example.wmfunbett2026.ui.theme.BackgroundDeep
import com.example.wmfunbett2026.ui.theme.DangerRed
import com.example.wmfunbett2026.ui.theme.DarkNavy
import com.example.wmfunbett2026.ui.theme.GlassBorder
import com.example.wmfunbett2026.ui.theme.PrimaryBlue
import com.example.wmfunbett2026.ui.theme.PrimaryBlueBright
import com.example.wmfunbett2026.ui.theme.TextMuted
import com.example.wmfunbett2026.ui.theme.TextPrimary
import com.example.wmfunbett2026.ui.theme.TextSecondary

private val FilterRowHeight = 72.dp
private val FilterTileWidth = 70.dp
private val FilterIconSlotSize = 40.dp
private val FilterIconSize = 20.dp
private val EdgeFadeWidth = 24.dp

private sealed class MatchQuickFilterItem(val key: String) {
    data class Time(val filter: MatchTimeQuickFilter) : MatchQuickFilterItem("time-${filter.name}")
    data class League(val filter: MatchLeagueQuickFilter) : MatchQuickFilterItem("league-${filter.id}")
}

private val matchQuickFilterItems: List<MatchQuickFilterItem> =
    MatchTimeQuickFilter.entries.map { MatchQuickFilterItem.Time(it) } +
        matchLeagueQuickFilters.map { MatchQuickFilterItem.League(it) }

@Composable
fun MatchQuickFilterRail(
    selectedTimeFilter: MatchTimeQuickFilter?,
    selectedLeagueFilterId: String?,
    onTimeFilterClick: (MatchTimeQuickFilter) -> Unit,
    onLeagueFilterClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(FilterRowHeight)
    ) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = ScreenContentHorizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(matchQuickFilterItems, key = { it.key }) { item ->
                when (item) {
                    is MatchQuickFilterItem.Time -> {
                        QuickFilterTile(
                            label = stringResource(item.filter.labelRes),
                            selected = selectedTimeFilter == item.filter,
                            isLive = item.filter == MatchTimeQuickFilter.LIVE,
                            onClick = { onTimeFilterClick(item.filter) }
                        ) {
                            TimeFilterIcon(
                                filter = item.filter,
                                selected = selectedTimeFilter == item.filter
                            )
                        }
                    }
                    is MatchQuickFilterItem.League -> {
                        QuickFilterTile(
                            label = stringResource(item.filter.labelRes),
                            selected = selectedLeagueFilterId == item.filter.id,
                            isLive = false,
                            onClick = { onLeagueFilterClick(item.filter.id) }
                        ) {
                            Text(
                                text = item.filter.emoji,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .width(EdgeFadeWidth)
                .fillMaxHeight()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(BackgroundDeep, BackgroundDeep.copy(alpha = 0f))
                    )
                )
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(EdgeFadeWidth)
                .fillMaxHeight()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(BackgroundDeep.copy(alpha = 0f), BackgroundDeep)
                    )
                )
        )
    }
}

@Composable
private fun QuickFilterTile(
    label: String,
    selected: Boolean,
    isLive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit
) {
    val accent = if (isLive) DangerRed else PrimaryBlue
    val iconCircleFill = when {
        selected && isLive -> Brush.radialGradient(
            colors = listOf(DangerRed.copy(alpha = 0.38f), DangerRed.copy(alpha = 0.22f))
        )
        selected -> Brush.radialGradient(
            colors = listOf(PrimaryBlue.copy(alpha = 0.42f), PrimaryBlue.copy(alpha = 0.24f))
        )
        isLive -> Brush.radialGradient(
            colors = listOf(DangerRed.copy(alpha = 0.16f), DarkNavy.copy(alpha = 0.58f))
        )
        else -> Brush.radialGradient(
            colors = listOf(PrimaryBlue.copy(alpha = 0.16f), DarkNavy.copy(alpha = 0.58f))
        )
    }
    val circleBorderColor = when {
        selected && isLive -> DangerRed.copy(alpha = 0.55f)
        selected -> PrimaryBlueBright.copy(alpha = 0.45f)
        isLive -> DangerRed.copy(alpha = 0.22f)
        else -> GlassBorder
    }
    val labelColor = when {
        selected && isLive -> DangerRed
        selected -> TextPrimary
        else -> TextMuted
    }

    Column(
        modifier = modifier
            .width(FilterTileWidth)
            .height(FilterRowHeight)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(FilterIconSlotSize)
                .clip(CircleShape)
                .background(iconCircleFill)
                .border(width = 1.dp, color = circleBorderColor, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                lineHeight = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = labelColor,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.width(FilterTileWidth - 2.dp)
            )
            if (selected) {
                Box(
                    modifier = Modifier
                        .width(14.dp)
                        .height(2.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(accent.copy(alpha = 0.9f))
                )
            } else {
                Box(modifier = Modifier.height(2.dp))
            }
        }
    }
}

@Composable
private fun TimeFilterIcon(
    filter: MatchTimeQuickFilter,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    val tint = when {
        filter == MatchTimeQuickFilter.LIVE -> DangerRed.copy(alpha = if (selected) 1f else 0.82f)
        selected -> TextPrimary
        else -> TextSecondary
    }

    when (filter) {
        MatchTimeQuickFilter.LIVE -> {
            Icon(
                imageVector = Icons.Default.FiberManualRecord,
                contentDescription = null,
                tint = tint,
                modifier = modifier.size(16.dp)
            )
        }
        MatchTimeQuickFilter.TODAY -> {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = tint,
                modifier = modifier.size(FilterIconSize)
            )
        }
        MatchTimeQuickFilter.THREE_HOURS -> {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = null,
                tint = tint,
                modifier = modifier.size(FilterIconSize)
            )
        }
        MatchTimeQuickFilter.FORTY_EIGHT_HOURS -> {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = tint,
                modifier = modifier.size(FilterIconSize)
            )
        }
    }
}
