package com.example.wmfunbett2026.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.wmfunbett2026.ui.screens.tournament.GameDetailScreen
import com.example.wmfunbett2026.ui.screens.tournament.TippGroupDetailScreen
import com.example.wmfunbett2026.ui.screens.tournament.TournamentDetailScreen
import com.example.wmfunbett2026.ui.screens.tournament.TournamentScreen

@Composable
fun TournamentNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = TournamentRoutes.TOURNAMENT_LIST,
        modifier = modifier
    ) {
        composable(TournamentRoutes.TOURNAMENT_LIST) {
            TournamentScreen(
                onTournamentClick = { roundId ->
                    navController.navigate(TournamentRoutes.tournamentDetail(roundId))
                },
                onGameClick = { roundId, dayId, gameId ->
                    navController.navigate(TournamentRoutes.gameDetail(roundId, dayId, gameId))
                }
            )
        }

        composable(
            route = TournamentRoutes.TOURNAMENT_DETAIL,
            arguments = listOf(navArgument("roundId") { type = NavType.StringType })
        ) { backStackEntry ->
            val roundId = backStackEntry.arguments?.getString("roundId").orEmpty()
            TournamentDetailScreen(
                roundId = roundId,
                onBackClick = { navController.popBackStack() },
                onGameClick = { dayId, gameId ->
                    navController.navigate(TournamentRoutes.gameDetail(roundId, dayId, gameId))
                },
                onDeleted = {
                    navController.popBackStack(
                        route = TournamentRoutes.TOURNAMENT_LIST,
                        inclusive = false
                    )
                }
            )
        }

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
                onDeleted = {
                    navController.popBackStack(
                        route = TournamentRoutes.tournamentDetail(roundId),
                        inclusive = false
                    )
                }
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
}
