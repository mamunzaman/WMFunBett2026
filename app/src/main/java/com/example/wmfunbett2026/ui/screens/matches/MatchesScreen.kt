package com.example.wmfunbett2026.ui.screens.matches

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.data.repository.FunBettRepository
import com.example.wmfunbett2026.ui.components.MatchListScreenContent
import com.example.wmfunbett2026.ui.matchcenter.FlatGameItem
import com.example.wmfunbett2026.ui.matchcenter.loadFlatGames

@Composable
fun MatchesScreen(
    onGameClick: (FlatGameItem) -> Unit,
    modifier: Modifier = Modifier
) {
    FunBettRepository.dataVersion.intValue
    val games = remember(FunBettRepository.dataVersion.intValue) { loadFlatGames() }

    MatchListScreenContent(
        title = stringResource(R.string.screen_matches),
        games = games,
        onGameClick = onGameClick,
        modifier = modifier
    )
}
