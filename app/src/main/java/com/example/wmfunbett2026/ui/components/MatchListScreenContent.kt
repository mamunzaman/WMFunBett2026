package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.ui.matchcenter.FlatGameItem
import com.example.wmfunbett2026.ui.matchcenter.MatchdayFilter
import com.example.wmfunbett2026.ui.matchcenter.groupMatchesBySection
import com.example.wmfunbett2026.ui.matchcenter.matchesFilter
import com.example.wmfunbett2026.ui.theme.BackgroundDeep
import com.example.wmfunbett2026.ui.theme.TextPrimary

@Composable
fun MatchListScreenContent(
    title: String,
    games: List<FlatGameItem>,
    onGameClick: (FlatGameItem) -> Unit,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    emptyTitle: String = stringResource(R.string.empty_matches_title),
    emptyMessage: String = stringResource(R.string.empty_matches_message)
) {
    var filter by remember { mutableStateOf(MatchdayFilter.ALL) }
    var filterMenuExpanded by remember { mutableStateOf(false) }
    val filterOptions = remember { MatchdayFilter.entries.toList() }
    val filterLabels = filterOptions.map { stringResource(it.labelRes) }

    val filtered = remember(games, filter) {
        games.filter { it.matchesFilter(filter) }
    }
    val grouped = remember(filtered) { groupMatchesBySection(filtered) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDeep)
    ) {
        MatchCenterHeader(
            title = title,
            onBackClick = onBackClick,
            showSearchIcon = true,
            matchdayFilterLabel = stringResource(filter.labelRes),
            onMatchdayFilterClick = { filterMenuExpanded = true },
            matchdayFilterMenuExpanded = filterMenuExpanded,
            onMatchdayFilterDismiss = { filterMenuExpanded = false },
            matchdayFilterOptions = filterLabels,
            onMatchdayFilterOptionSelected = { index ->
                filter = filterOptions[index]
                filterMenuExpanded = false
            }
        )

        if (filtered.isEmpty()) {
            MatchCenterEmptyState(
                title = emptyTitle,
                message = emptyMessage,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = hierarchyContentPadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                grouped.forEach { (section, sectionGames) ->
                    item(key = "header-${section.name}") {
                        Text(
                            text = stringResource(section.titleRes),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                        )
                    }
                    items(sectionGames, key = { "${it.roundId}-${it.dayId}-${it.game.id}" }) { item ->
                        MatchCenterCard(
                            game = item.game,
                            matchdayLabel = item.dayName,
                            onClick = { onGameClick(item) }
                        )
                    }
                }
            }
        }
    }
}
