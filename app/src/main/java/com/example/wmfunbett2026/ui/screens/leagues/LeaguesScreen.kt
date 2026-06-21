package com.example.wmfunbett2026.ui.screens.leagues

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.data.repository.FunBettRepository
import com.example.wmfunbett2026.ui.components.LeagueGridCard
import com.example.wmfunbett2026.ui.components.MatchCenterHeader
import com.example.wmfunbett2026.ui.components.screenContentPadding
import com.example.wmfunbett2026.ui.matchcenter.LeagueSummary
import com.example.wmfunbett2026.ui.matchcenter.loadLeagueSummaries
import com.example.wmfunbett2026.ui.theme.BackgroundDeep

private val LeagueGridRightColumnOffset = 56.dp
private val LeagueGridColumnSpacing = 12.dp
private val LeagueGridTallCardHeight = 205.dp
private val LeagueGridCompactCardHeight = 188.dp

@Composable
fun LeaguesScreen(
    onLeagueClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    FunBettRepository.dataVersion.intValue
    val leagues = remember(FunBettRepository.dataVersion.intValue) { loadLeagueSummaries() }
    val leftLeagues = remember(leagues) { leagues.filterIndexed { index, _ -> index % 2 == 0 } }
    val rightLeagues = remember(leagues) { leagues.filterIndexed { index, _ -> index % 2 == 1 } }

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
            contentPadding = screenContentPadding()
        ) {
            item(key = "league_staggered_grid") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(LeagueGridColumnSpacing),
                    verticalAlignment = Alignment.Top
                ) {
                    LeagueGridColumn(
                        leagues = leftLeagues,
                        onLeagueClick = onLeagueClick,
                        tallFirst = true,
                        staggerIndexOffset = 0,
                        modifier = Modifier.weight(1f)
                    )
                    LeagueGridColumn(
                        leagues = rightLeagues,
                        onLeagueClick = onLeagueClick,
                        tallFirst = false,
                        staggerIndexOffset = 1,
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = LeagueGridRightColumnOffset)
                    )
                }
            }
        }
    }
}

@Composable
private fun LeagueGridColumn(
    leagues: List<LeagueSummary>,
    onLeagueClick: (String) -> Unit,
    tallFirst: Boolean,
    staggerIndexOffset: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(LeagueGridColumnSpacing)
    ) {
        leagues.forEachIndexed { index, league ->
            val tallCard = if (tallFirst) index % 2 == 0 else index % 2 == 1
            LeagueGridCard(
                leagueId = league.id,
                name = league.name,
                matchCount = league.matchCount,
                activeMatchCount = league.activeMatchCount,
                tippGroupCount = league.tippGroupCount,
                onClick = { onLeagueClick(league.id) },
                cardHeight = if (tallCard) LeagueGridTallCardHeight else LeagueGridCompactCardHeight,
                staggerIndex = index * 2 + staggerIndexOffset
            )
        }
    }
}
