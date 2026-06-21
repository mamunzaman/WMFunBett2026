package com.example.wmfunbett2026.ui.screens.matches

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.data.repository.FunBettRepository
import com.example.wmfunbett2026.ui.components.MatchListScreenContent
import com.example.wmfunbett2026.ui.matchcenter.FlatGameItem
import com.example.wmfunbett2026.ui.matchcenter.loadFlatGames
import com.example.wmfunbett2026.ui.navigation.MainRoutes

@Composable
fun MatchesScreen(
    onGameClick: (FlatGameItem) -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    FunBettRepository.dataVersion.intValue
    val games = remember(FunBettRepository.dataVersion.intValue) { loadFlatGames() }
    var entranceSession by remember { mutableIntStateOf(0) }

    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            if (destination.route == MainRoutes.MATCHES) {
                entranceSession++
            }
        }
        navController.addOnDestinationChangedListener(listener)
        if (navController.currentDestination?.route == MainRoutes.MATCHES) {
            entranceSession++
        }
        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }

    MatchListScreenContent(
        title = stringResource(R.string.screen_matches),
        games = games,
        onGameClick = onGameClick,
        showLiveAction = true,
        showQuickFilters = true,
        animateEntrance = true,
        entranceSession = entranceSession,
        modifier = modifier
    )
}
