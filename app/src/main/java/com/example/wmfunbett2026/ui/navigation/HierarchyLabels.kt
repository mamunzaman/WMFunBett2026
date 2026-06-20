package com.example.wmfunbett2026.ui.navigation

import com.example.wmfunbett2026.data.sample.SampleData

object HierarchyLabels {
    fun roundName(roundId: String): String =
        SampleData.getRound(roundId)?.name ?: roundId

    fun dayName(dayId: String): String =
        SampleData.getDay(dayId)?.name ?: dayId

    fun gameName(gameId: String): String =
        SampleData.getGame(gameId)?.displayName ?: gameId

    fun tippGroupName(tippGroupId: String): String =
        SampleData.getTippGroup(tippGroupId)?.name ?: tippGroupId

    fun forRoundsList(): List<String> = listOf("Rounds")

    fun forRoundDetail(roundId: String): List<String> =
        listOf("Rounds", roundName(roundId))

    fun forDayDetail(roundId: String, dayId: String): List<String> =
        listOf("Rounds", roundName(roundId), dayName(dayId))

    fun forGameDetail(roundId: String, dayId: String, gameId: String): List<String> =
        listOf("Rounds", roundName(roundId), dayName(dayId), gameName(gameId))

    fun forTippGroupDetail(
        roundId: String,
        dayId: String,
        gameId: String,
        tippGroupId: String
    ): List<String> = listOf(
        "Rounds",
        roundName(roundId),
        dayName(dayId),
        gameName(gameId),
        tippGroupName(tippGroupId)
    )

    fun forEntryList(
        roundId: String,
        dayId: String,
        gameId: String,
        tippGroupId: String
    ): List<String> = listOf(
        "Rounds",
        roundName(roundId),
        dayName(dayId),
        gameName(gameId),
        tippGroupName(tippGroupId),
        "Entries"
    )
}
