package com.example.wmfunbett2026.data.jackpot

import com.example.wmfunbett2026.data.model.Game
import com.example.wmfunbett2026.data.model.MatchStatus
import com.example.wmfunbett2026.data.model.Round
import com.example.wmfunbett2026.data.model.TippGroup
import com.example.wmfunbett2026.data.winner.TippGroupWinnerEngine
import com.example.wmfunbett2026.data.winner.TippGroupWinnerOutcome

object JackpotChainCalculator {

    fun calculateCarryItems(round: Round, currentGameId: String): List<JackpotCarryItem> {
        val gamesInOrder = gamesInTournamentOrder(round)
        val currentIndex = gamesInOrder.indexOfFirst { it.id == currentGameId }
        if (currentIndex <= 0) return emptyList()

        return gamesInOrder
            .take(currentIndex)
            .flatMap { game -> carryItemsForGame(game) }
    }

    fun buildEntryJoinBreakdown(
        carryItems: List<JackpotCarryItem>,
        currentRoundEntryAmount: Double
    ): EntryJoinBreakdown {
        val previousBuyIn = carryItems.sumOf { it.requiredAmountPerPerson }
        return EntryJoinBreakdown(
            carryItems = carryItems,
            previousJackpotBuyIn = previousBuyIn,
            currentRoundEntryAmount = currentRoundEntryAmount,
            totalRequired = previousBuyIn + currentRoundEntryAmount
        )
    }

    fun requiredPerPersonAmount(tippGroup: TippGroup): Double? =
        tippGroup.entries.firstOrNull()?.currentRoundAmount

    private fun gamesInTournamentOrder(round: Round): List<Game> =
        round.days.flatMap { it.games }

    private fun carryItemsForGame(game: Game): List<JackpotCarryItem> {
        if (game.status != MatchStatus.FINISHED || !game.hasResult) return emptyList()

        return game.tippGroups.mapNotNull { tippGroup ->
            toCarryItem(game, tippGroup)
        }
    }

    private fun toCarryItem(game: Game, tippGroup: TippGroup): JackpotCarryItem? {
        if (tippGroup.entries.isEmpty()) return null

        val outcome = TippGroupWinnerEngine.calculate(game, tippGroup)
        if (outcome !is TippGroupWinnerOutcome.NoWinner) return null

        return JackpotCarryItem(
            sourceGameId = game.id,
            sourceGameLabel = game.displayName,
            sourceTippGroupId = tippGroup.id,
            sourceTippGroupTitle = tippGroup.title,
            requiredAmountPerPerson = tippGroup.entries.first().currentRoundAmount
        )
    }
}
