package com.example.wmfunbett2026.data.winner

import com.example.wmfunbett2026.data.model.Entry
import com.example.wmfunbett2026.data.model.Game
import com.example.wmfunbett2026.data.model.MatchStatus
import com.example.wmfunbett2026.data.model.TippGroup

sealed class TippGroupWinnerOutcome {
    data object Pending : TippGroupWinnerOutcome()
    data object NoWinner : TippGroupWinnerOutcome()

    data class Winners(
        val winningEntries: List<Entry>,
        val sharePerWinner: Double,
        val tippTotal: Double
    ) : TippGroupWinnerOutcome()
}

object TippGroupWinnerEngine {

    fun calculate(game: Game, tippGroup: TippGroup): TippGroupWinnerOutcome {
        if (!game.hasResult || game.status != MatchStatus.FINISHED) {
            return TippGroupWinnerOutcome.Pending
        }

        val resultKey = formatScoreKey(game.teamAScore!!, game.teamBScore!!)
        val winners = tippGroup.entries.filter { entry ->
            normalizePrediction(entry.prediction) == resultKey
        }

        if (winners.isEmpty()) {
            return TippGroupWinnerOutcome.NoWinner
        }

        val tippTotal = tippGroup.totalAmount
        val sharePerWinner = tippTotal / winners.size

        return TippGroupWinnerOutcome.Winners(
            winningEntries = winners,
            sharePerWinner = sharePerWinner,
            tippTotal = tippTotal
        )
    }

    fun compactLabel(outcome: TippGroupWinnerOutcome): String = when (outcome) {
        TippGroupWinnerOutcome.Pending -> "Pending"
        TippGroupWinnerOutcome.NoWinner -> "No winner"
        is TippGroupWinnerOutcome.Winners -> {
            if (outcome.winningEntries.size == 1) "1 winner" else "${outcome.winningEntries.size} winners"
        }
    }

    fun isWinningEntry(outcome: TippGroupWinnerOutcome, entryId: String): Boolean {
        return outcome is TippGroupWinnerOutcome.Winners &&
            outcome.winningEntries.any { it.id == entryId }
    }

    private fun formatScoreKey(teamAScore: Int, teamBScore: Int): String = "$teamAScore-$teamBScore"

    private fun normalizePrediction(prediction: String): String =
        prediction.trim().replace(':', '-').replace(Regex("\\s+"), "")
}
