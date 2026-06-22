package com.example.wmfunbett2026.ui.navigation

import androidx.navigation.NavBackStackEntry

enum class CreateMenuContext {
    MatchesMain,
    LeaguesMain,
    LeagueDetail,
    GameDetail,
    TippGroupDetail
}

data class CreateNavigationState(
    val context: CreateMenuContext,
    val preselectedLeagueId: String? = null,
    val activeGameId: String? = null,
    val activeTippGroupId: String? = null
)

fun resolveCreateNavigationState(
    selectedScreen: AppScreen,
    backStackEntry: NavBackStackEntry?
): CreateNavigationState {
    val args = backStackEntry?.arguments
    args?.getString("tippGroupId")?.takeIf { it.isNotBlank() }?.let { tippGroupId ->
        return CreateNavigationState(
            context = CreateMenuContext.TippGroupDetail,
            activeGameId = args.getString("gameId"),
            activeTippGroupId = tippGroupId
        )
    }
    args?.getString("gameId")?.takeIf { it.isNotBlank() }?.let { gameId ->
        return CreateNavigationState(
            context = CreateMenuContext.GameDetail,
            activeGameId = gameId
        )
    }
    args?.getString("leagueId")?.takeIf { it.isNotBlank() }?.let { leagueId ->
        return CreateNavigationState(
            context = CreateMenuContext.LeagueDetail,
            preselectedLeagueId = leagueId
        )
    }

    return when (selectedScreen) {
        AppScreen.Matches -> CreateNavigationState(CreateMenuContext.MatchesMain)
        AppScreen.Leagues -> CreateNavigationState(CreateMenuContext.LeaguesMain)
        AppScreen.Friends, AppScreen.Settings -> CreateNavigationState(CreateMenuContext.MatchesMain)
    }
}
