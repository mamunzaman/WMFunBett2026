package com.example.wmfunbett2026.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.wmfunbett2026.ui.screens.rounds.DayDetailScreen
import com.example.wmfunbett2026.ui.screens.rounds.EntryListScreen
import com.example.wmfunbett2026.ui.screens.rounds.GameDetailScreen
import com.example.wmfunbett2026.ui.screens.rounds.RoundDetailScreen
import com.example.wmfunbett2026.ui.screens.rounds.RoundsScreen
import com.example.wmfunbett2026.ui.screens.rounds.TippGroupDetailScreen

@Composable
fun RoundsNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = RoundsRoutes.ROUNDS_LIST,
        modifier = modifier
    ) {
        composable(RoundsRoutes.ROUNDS_LIST) {
            RoundsScreen(
                onRoundClick = { roundId ->
                    navController.navigate(RoundsRoutes.roundDetail(roundId))
                }
            )
        }

        composable(
            route = RoundsRoutes.ROUND_DETAIL,
            arguments = listOf(navArgument("roundId") { type = NavType.StringType })
        ) { backStackEntry ->
            val roundId = backStackEntry.arguments?.getString("roundId").orEmpty()
            RoundDetailScreen(
                roundId = roundId,
                onBackClick = { navController.popBackStack() },
                onDayClick = { dayId ->
                    navController.navigate(RoundsRoutes.dayDetail(roundId, dayId))
                }
            )
        }

        composable(
            route = RoundsRoutes.DAY_DETAIL,
            arguments = listOf(
                navArgument("roundId") { type = NavType.StringType },
                navArgument("dayId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val roundId = backStackEntry.arguments?.getString("roundId").orEmpty()
            val dayId = backStackEntry.arguments?.getString("dayId").orEmpty()
            DayDetailScreen(
                roundId = roundId,
                dayId = dayId,
                onBackClick = { navController.popBackStack() },
                onGameClick = { gameId ->
                    navController.navigate(RoundsRoutes.gameDetail(roundId, dayId, gameId))
                }
            )
        }

        composable(
            route = RoundsRoutes.GAME_DETAIL,
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
                        RoundsRoutes.tippGroupDetail(roundId, dayId, gameId, tippGroupId)
                    )
                }
            )
        }

        composable(
            route = RoundsRoutes.TIPP_GROUP_DETAIL,
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
                onViewEntriesClick = {
                    navController.navigate(
                        RoundsRoutes.entryList(roundId, dayId, gameId, tippGroupId)
                    )
                }
            )
        }

        composable(
            route = RoundsRoutes.ENTRY_LIST,
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
            EntryListScreen(
                roundId = roundId,
                dayId = dayId,
                gameId = gameId,
                tippGroupId = tippGroupId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
