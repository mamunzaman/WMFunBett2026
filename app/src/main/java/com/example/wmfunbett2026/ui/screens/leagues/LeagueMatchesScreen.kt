package com.example.wmfunbett2026.ui.screens.leagues

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.data.repository.FunBettRepository
import com.example.wmfunbett2026.ui.components.MatchListScreenContent
import com.example.wmfunbett2026.ui.matchcenter.FlatGameItem
import com.example.wmfunbett2026.ui.matchcenter.flattenGamesForRound
import com.example.wmfunbett2026.ui.matchcenter.loadLeagueSummaries

@Composable
fun LeagueMatchesScreen(
    leagueId: String,
    onGameClick: (FlatGameItem) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FunBettRepository.dataVersion.intValue
    val league = remember(leagueId, FunBettRepository.dataVersion.intValue) {
        loadLeagueSummaries().find { it.id == leagueId }
    }
    val games = remember(league, FunBettRepository.dataVersion.intValue) {
        flattenGamesForRound(league?.roundId)
    }

    MatchListScreenContent(
        title = league?.name ?: stringResource(R.string.screen_leagues),
        games = games,
        onGameClick = onGameClick,
        onBackClick = onBackClick,
        modifier = modifier,
        emptyTitle = stringResource(R.string.league_matches_empty_title),
        emptyMessage = stringResource(R.string.league_matches_empty_message)
    )
}
