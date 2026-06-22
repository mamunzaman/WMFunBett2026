package com.example.wmfunbett2026.data.jackpot

import com.example.wmfunbett2026.data.model.Game
import com.example.wmfunbett2026.data.model.MatchStatus
import com.example.wmfunbett2026.data.model.Round
import com.example.wmfunbett2026.data.model.TippGroup
import com.example.wmfunbett2026.data.winner.TippGroupWinnerEngine
import com.example.wmfunbett2026.data.winner.TippGroupWinnerOutcome

object JackpotChainCalculator {

    fun calculateCarryItems(round: Round, currentGameId: String): List<JackpotCarryItem> {
        val gamesInOrder = gamesInChronologicalOrder(round)
        val currentIndex = gamesInOrder.indexOfFirst { it.id == currentGameId }
        if (currentIndex <= 0) return emptyList()

        return gamesInOrder
            .take(currentIndex)
            .flatMap { game -> carryItemsForGame(game) }
    }

    fun calculateCarryOverSummary(
        round: Round,
        game: Game,
        tippGroup: TippGroup
    ): JackpotCarryOverSummary {
        val incomingJackpot = calculateIncomingJackpot(round, game, tippGroup)
        val currentCollected = currentCollectedAmount(tippGroup)
        val totalPot = incomingJackpot + currentCollected
        val hasResult = game.hasResult && game.status == MatchStatus.FINISHED
        val winnerCount = if (hasResult) {
            TippGroupWinnerEngine.winningEntries(game, tippGroup).size
        } else {
            0
        }
        val hasWinner = winnerCount > 0
        val carriedOut = if (hasResult && !hasWinner) totalPot else 0.0
        val sharePerWinner = if (hasWinner) totalPot / winnerCount else 0.0

        return JackpotCarryOverSummary(
            incomingJackpot = incomingJackpot,
            currentCollected = currentCollected,
            totalPot = totalPot,
            carriedOut = carriedOut,
            hasWinner = hasWinner,
            winnerCount = winnerCount,
            sharePerWinner = sharePerWinner
        )
    }

    fun calculateIncomingJackpot(
        round: Round,
        game: Game,
        tippGroup: TippGroup
    ): Double {
        val games = gamesInChronologicalOrder(round)
        val currentIndex = games.indexOfFirst { it.id == game.id }
        if (currentIndex <= 0) return 0.0

        val scope = tippGroup.timeScope
        var incoming = 0.0

        for (index in currentIndex - 1 downTo 0) {
            val previousGame = games[index]
            val previousGroup = previousGame.tippGroups.find { it.timeScope == scope } ?: continue
            if (!previousGame.hasResult || previousGame.status != MatchStatus.FINISHED) continue

            when (TippGroupWinnerEngine.calculate(previousGame, previousGroup)) {
                is TippGroupWinnerOutcome.Winners -> return incoming
                TippGroupWinnerOutcome.NoWinner -> incoming += currentCollectedAmount(previousGroup)
                TippGroupWinnerOutcome.Pending -> Unit
            }
        }

        return incoming
    }

    fun maxIncomingJackpotForGame(round: Round, game: Game): Double =
        game.tippGroups.maxOfOrNull { calculateIncomingJackpot(round, game, it) } ?: 0.0

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
        tippGroup.entries.firstOrNull()?.currentRoundAmount ?: tippGroup.entryAmount

    fun currentCollectedAmount(tippGroup: TippGroup): Double {
        val entryAmount = tippGroup.entryAmount ?: return tippGroup.totalAmount
        return tippGroup.entries.size * entryAmount
    }

    fun gamesInChronologicalOrder(round: Round): List<Game> =
        round.days.flatMap { day ->
            day.games.sortedWith(
                compareBy<Game> { gameSortKey(it) }.thenBy { it.id }
            )
        }

    private fun gameSortKey(game: Game): String = game.dateTimeLabel.trim().lowercase()

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
