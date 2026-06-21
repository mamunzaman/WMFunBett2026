package com.example.wmfunbett2026.ui.screens.leagues

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.data.repository.FunBettRepository
import com.example.wmfunbett2026.ui.components.LeagueSummaryCard
import com.example.wmfunbett2026.ui.components.MatchCenterHeader
import com.example.wmfunbett2026.ui.components.hierarchyContentPadding
import com.example.wmfunbett2026.ui.matchcenter.loadLeagueSummaries
import com.example.wmfunbett2026.ui.theme.BackgroundDeep

@Composable
fun LeaguesScreen(
    onLeagueClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    FunBettRepository.dataVersion.intValue
    val leagues = remember(FunBettRepository.dataVersion.intValue) { loadLeagueSummaries() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDeep)
    ) {
        MatchCenterHeader(
            title = stringResource(R.string.screen_leagues),
            showSearchIcon = true
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = hierarchyContentPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(leagues, key = { it.id }) { league ->
                LeagueSummaryCard(
                    name = league.name,
                    matchCount = league.matchCount,
                    activeMatchCount = league.activeMatchCount,
                    tippGroupCount = league.tippGroupCount,
                    onClick = { onLeagueClick(league.id) }
                )
            }
        }
    }
}
