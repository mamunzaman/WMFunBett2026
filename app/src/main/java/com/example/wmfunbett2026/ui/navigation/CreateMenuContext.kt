package com.example.wmfunbett2026.ui.navigation

enum class CreateMenuContext {
    MatchesMain,
    LeaguesMain,
    LeagueDetail,
    GameDetail
}

data class CreateNavigationState(
    val context: CreateMenuContext,
    val preselectedLeagueId: String? = null,
    val activeGameId: String? = null
)

private val gameDetailRouteRegex =
    Regex("""^tournament/([^/]+)/day/([^/]+)/game/([^/]+)(?:/tipp/[^/]+)?$""")

private val leagueMatchesRouteRegex =
    Regex("""^leagues/([^/]+)/matches$""")

fun resolveCreateNavigationState(
    selectedScreen: AppScreen,
    activeRoute: String?
): CreateNavigationState {
    activeRoute?.let { route ->
        gameDetailRouteRegex.matchEntire(route)?.let { match ->
            return CreateNavigationState(
                context = CreateMenuContext.GameDetail,
                activeGameId = match.groupValues[3]
            )
        }
        leagueMatchesRouteRegex.matchEntire(route)?.let { match ->
            val leagueId = match.groupValues[1]
            return CreateNavigationState(
                context = CreateMenuContext.LeagueDetail,
                preselectedLeagueId = leagueId
            )
        }
    }

    return when (selectedScreen) {
        AppScreen.Matches -> CreateNavigationState(CreateMenuContext.MatchesMain)
        AppScreen.Leagues -> CreateNavigationState(CreateMenuContext.LeaguesMain)
        AppScreen.Friends, AppScreen.Settings -> CreateNavigationState(CreateMenuContext.MatchesMain)
    }
}
