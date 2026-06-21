package com.example.wmfunbett2026.ui.navigation

object TournamentRoutes {
    const val TOURNAMENT_LIST = "tournament"
    const val TOURNAMENT_DETAIL = "tournament/{roundId}"
    const val GAME_DETAIL = "tournament/{roundId}/day/{dayId}/game/{gameId}"
    const val TIPP_GROUP_DETAIL = "tournament/{roundId}/day/{dayId}/game/{gameId}/tipp/{tippGroupId}"

    fun tournamentDetail(roundId: String) = "tournament/$roundId"
    fun gameDetail(roundId: String, dayId: String, gameId: String) =
        "tournament/$roundId/day/$dayId/game/$gameId"
    fun tippGroupDetail(roundId: String, dayId: String, gameId: String, tippGroupId: String) =
        "tournament/$roundId/day/$dayId/game/$gameId/tipp/$tippGroupId"
}
