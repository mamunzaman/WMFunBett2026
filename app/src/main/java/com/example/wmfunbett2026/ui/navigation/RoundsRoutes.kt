package com.example.wmfunbett2026.ui.navigation

object RoundsRoutes {
    const val ROUNDS_LIST = "rounds"
    const val ROUND_DETAIL = "round/{roundId}"
    const val DAY_DETAIL = "round/{roundId}/day/{dayId}"
    const val GAME_DETAIL = "round/{roundId}/day/{dayId}/game/{gameId}"
    const val TIPP_GROUP_DETAIL = "round/{roundId}/day/{dayId}/game/{gameId}/tipp/{tippGroupId}"
    const val ENTRY_LIST = "round/{roundId}/day/{dayId}/game/{gameId}/tipp/{tippGroupId}/entries"

    fun roundDetail(roundId: String) = "round/$roundId"
    fun dayDetail(roundId: String, dayId: String) = "round/$roundId/day/$dayId"
    fun gameDetail(roundId: String, dayId: String, gameId: String) =
        "round/$roundId/day/$dayId/game/$gameId"
    fun tippGroupDetail(roundId: String, dayId: String, gameId: String, tippGroupId: String) =
        "round/$roundId/day/$dayId/game/$gameId/tipp/$tippGroupId"
    fun entryList(roundId: String, dayId: String, gameId: String, tippGroupId: String) =
        "round/$roundId/day/$dayId/game/$gameId/tipp/$tippGroupId/entries"
}
