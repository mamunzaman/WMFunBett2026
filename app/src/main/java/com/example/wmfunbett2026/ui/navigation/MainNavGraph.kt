package com.example.wmfunbett2026.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.wmfunbett2026.ui.screens.leagues.LeagueMatchesScreen
import com.example.wmfunbett2026.ui.screens.leagues.LeaguesScreen
import com.example.wmfunbett2026.ui.screens.matches.MatchesScreen
import com.example.wmfunbett2026.ui.screens.tournament.GameDetailScreen
import com.example.wmfunbett2026.ui.screens.tournament.TippGroupDetailScreen

@Composable
fun MatchesNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = MainRoutes.MATCHES,
        modifier = modifier
    ) {
        composable(MainRoutes.MATCHES) {
            MatchesScreen(
                navController = navController,
                onGameClick = { item ->
                    navController.navigate(
                        TournamentRoutes.gameDetail(item.roundId, item.dayId, item.game.id)
                    )
                }
            )
        }
        gameDetailRoutes(navController)
    }
}

@Composable
fun LeaguesNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = MainRoutes.LEAGUES,
        modifier = modifier
    ) {
        composable(MainRoutes.LEAGUES) {
            LeaguesScreen(
                onLeagueClick = { leagueId ->
                    navController.navigate(MainRoutes.leagueMatches(leagueId))
                }
            )
        }
        composable(
            route = MainRoutes.LEAGUE_MATCHES,
            arguments = listOf(navArgument("leagueId") { type = NavType.StringType })
        ) { backStackEntry ->
            val leagueId = backStackEntry.arguments?.getString("leagueId").orEmpty()
            LeagueMatchesScreen(
                leagueId = leagueId,
                navController = navController,
                onBackClick = { navController.popBackStack() },
                onGameClick = { item ->
                    navController.navigate(
                        TournamentRoutes.gameDetail(item.roundId, item.dayId, item.game.id)
                    )
                }
            )
        }
        gameDetailRoutes(navController)
    }
}

private fun NavGraphBuilder.gameDetailRoutes(navController: NavHostController) {
    composable(
        route = TournamentRoutes.GAME_DETAIL,
        arguments = listOf(
            navArgument("roundId") { type = NavType.StringType },
            navArgument("dayId") { type = NavType.StringType },
            navArgument("gameId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val roundId = backStackEntry.arguments?.getString("roundId").orEmpty()
        val dayId = backStackEntry.arguments?.getString("dayId").orEmpty()
        val gameId = backStackEntry.arguments?.getString("gameId").orEmpty()
        GameDetailScreen(
            roundId = roundId,
            dayId = dayId,
            gameId = gameId,
            onBackClick = { navController.popBackStack() },
            onTippGroupClick = { tippGroupId ->
                navController.navigate(
                    TournamentRoutes.tippGroupDetail(roundId, dayId, gameId, tippGroupId)
                )
            },
            onDeleted = { navController.popBackStack() }
        )
    }

    composable(
        route = TournamentRoutes.TIPP_GROUP_DETAIL,
        arguments = listOf(
            navArgument("roundId") { type = NavType.StringType },
            navArgument("dayId") { type = NavType.StringType },
            navArgument("gameId") { type = NavType.StringType },
            navArgument("tippGroupId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val roundId = backStackEntry.arguments?.getString("roundId").orEmpty()
        val dayId = backStackEntry.arguments?.getString("dayId").orEmpty()
        val gameId = backStackEntry.arguments?.getString("gameId").orEmpty()
        val tippGroupId = backStackEntry.arguments?.getString("tippGroupId").orEmpty()
        TippGroupDetailScreen(
            roundId = roundId,
            dayId = dayId,
            gameId = gameId,
            tippGroupId = tippGroupId,
            onBackClick = { navController.popBackStack() },
            onDeleted = {
                navController.popBackStack(
                    route = TournamentRoutes.gameDetail(roundId, dayId, gameId),
                    inclusive = false
                )
            }
        )
    }
}
