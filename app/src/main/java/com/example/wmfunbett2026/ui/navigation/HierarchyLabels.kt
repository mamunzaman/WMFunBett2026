package com.example.wmfunbett2026.ui.navigation

import com.example.wmfunbett2026.data.repository.FunBettRepository

object HierarchyLabels {
    private const val ROOT = "Tipps"

    fun roundName(roundId: String): String =
        FunBettRepository.getRound(roundId)?.name ?: roundId

    fun gameName(gameId: String): String =
        FunBettRepository.getGame(gameId)?.displayName ?: gameId

    fun tippGroupName(tippGroupId: String): String =
        FunBettRepository.getTippGroup(tippGroupId)?.title ?: tippGroupId

    fun forTournamentList(): List<String> = listOf(ROOT)

    fun forTournamentDetail(roundId: String): List<String> =
        listOf(ROOT, roundName(roundId))

    fun forGameDetail(roundId: String, gameId: String): List<String> =
        listOf(ROOT, roundName(roundId), gameName(gameId))

    fun forTippGroupDetail(
        roundId: String,
        gameId: String,
        tippGroupId: String
    ): List<String> = listOf(
        ROOT,
        roundName(roundId),
        gameName(gameId),
        tippGroupName(tippGroupId)
    )
}
