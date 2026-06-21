package com.example.wmfunbett2026.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.data.repository.FunBettRepository

object HierarchyLabels {
    @Composable
    private fun root(): String = stringResource(R.string.nav_tipps)

    fun roundName(roundId: String): String =
        FunBettRepository.getRound(roundId)?.name ?: roundId

    fun gameName(gameId: String): String =
        FunBettRepository.getGame(gameId)?.displayName ?: gameId

    fun tippGroupName(tippGroupId: String): String =
        FunBettRepository.getTippGroup(tippGroupId)?.title ?: tippGroupId

    @Composable
    fun forTournamentList(): List<String> = listOf(root())

    @Composable
    fun forTournamentDetail(roundId: String): List<String> =
        listOf(root(), roundName(roundId))

    @Composable
    fun forGameDetail(roundId: String, gameId: String): List<String> =
        listOf(root(), roundName(roundId), gameName(gameId))

    @Composable
    fun forTippGroupDetail(
        roundId: String,
        gameId: String,
        tippGroupId: String
    ): List<String> = listOf(
        root(),
        roundName(roundId),
        gameName(gameId),
        tippGroupName(tippGroupId)
    )
}
