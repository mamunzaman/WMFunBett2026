package com.example.wmfunbett2026.ui.navigation

object MainRoutes {
    const val MATCHES = "matches"
    const val LEAGUES = "leagues"
    const val LEAGUE_MATCHES = "leagues/{leagueId}/matches"

    fun leagueMatches(leagueId: String) = "leagues/$leagueId/matches"
}
