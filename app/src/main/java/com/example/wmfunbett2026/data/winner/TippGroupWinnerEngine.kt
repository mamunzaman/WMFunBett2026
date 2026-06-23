package com.example.wmfunbett2026.data.winner

import com.example.wmfunbett2026.data.jackpot.JackpotChainBreakSignal
import com.example.wmfunbett2026.data.jackpot.JackpotV2Calculator
import com.example.wmfunbett2026.data.jackpot.JackpotV2EntryInput
import com.example.wmfunbett2026.data.jackpot.JackpotV2RoundInput
import com.example.wmfunbett2026.data.model.Entry
import com.example.wmfunbett2026.data.model.EntryParticipation
import com.example.wmfunbett2026.data.model.EntryWinPayout
import com.example.wmfunbett2026.data.model.Game
import com.example.wmfunbett2026.data.model.JackpotWinnerPool
import com.example.wmfunbett2026.data.model.LocalWinnerPool
import com.example.wmfunbett2026.data.model.MatchStatus
import com.example.wmfunbett2026.data.model.TippGroup
import com.example.wmfunbett2026.data.model.TippGroupSettlementStatus
import com.example.wmfunbett2026.data.model.TippGroupSettlementSummary
import com.example.wmfunbett2026.data.model.TippGroupSplitPayouts

/** Legacy single-pot outcome used by repository, calculator, and UI. */
sealed class TippGroupWinnerOutcome {
    data object Pending : TippGroupWinnerOutcome()
    data object NoWinner : TippGroupWinnerOutcome()

    data class Winners(
        val winningEntries: List<Entry>,
        val sharePerWinner: Double,
        val tippTotal: Double
    ) : TippGroupWinnerOutcome()
}

/**
 * Future split outcome: local and jackpot pots resolved independently.
 * Not wired; legacy [TippGroupWinnerOutcome] remains active.
 */
sealed class SplitTippGroupWinnerOutcome {
    data object Pending : SplitTippGroupWinnerOutcome()

    data class Resolved(
        val localWinners: List<Entry>,
        val jackpotWinners: List<Entry>,
        val localPot: Double,
        val localSharePerWinner: Double,
        val incomingJackpot: Double,
        val jackpotCurrentCollected: Double,
        val jackpotTotalPot: Double,
        val jackpotSharePerWinner: Double,
        val payoutsByEntryId: Map<String, EntryWinPayout>
    ) : SplitTippGroupWinnerOutcome()
}

/**
 * Winner engine.
 *
 * **Legacy (wired):** [calculate], [winningEntries], [settlementSummary].
 *
 * **Future (not wired):** [calculateSplit], [previewSplitPayouts], participation filters.
 */
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

    fun winningEntries(game: Game, tippGroup: TippGroup): List<Entry> {
        if (!game.hasResult) return emptyList()
        val resultKey = formatScoreKey(game.teamAScore!!, game.teamBScore!!)
        return tippGroup.entries.filter { entry ->
            normalizePrediction(entry.prediction) == resultKey
        }
    }

    fun isWinningEntry(game: Game, entry: Entry): Boolean {
        if (!game.hasResult) return false
        val resultKey = formatScoreKey(game.teamAScore!!, game.teamBScore!!)
        return normalizePrediction(entry.prediction) == resultKey
    }

    fun settlementSummary(game: Game, tippGroup: TippGroup): TippGroupSettlementSummary {
        val totalCollected = theoreticalTotalCollected(tippGroup)
        if (!game.hasResult) {
            return TippGroupSettlementSummary(
                status = TippGroupSettlementStatus.PENDING,
                totalCollected = totalCollected,
                winnerCount = 0,
                sharePerWinner = 0.0
            )
        }

        val winnerCount = winningEntries(game, tippGroup).size
        val sharePerWinner = if (winnerCount > 0) totalCollected / winnerCount else 0.0
        val status = if (winnerCount > 0) {
            TippGroupSettlementStatus.WINNERS
        } else {
            TippGroupSettlementStatus.NO_WINNERS
        }

        return TippGroupSettlementSummary(
            status = status,
            totalCollected = totalCollected,
            winnerCount = winnerCount,
            sharePerWinner = sharePerWinner
        )
    }

    private fun theoreticalTotalCollected(tippGroup: TippGroup): Double {
        val entryAmount = tippGroup.entryAmount ?: return tippGroup.totalAmount
        return tippGroup.entries.size * entryAmount
    }

    private fun formatScoreKey(teamAScore: Int, teamBScore: Int): String = "$teamAScore-$teamBScore"

    private fun normalizePrediction(prediction: String): String =
        prediction.trim().replace(':', '-').replace(Regex("\\s+"), "")

    // region Future split participation winners (not wired to repository or UI)

    fun winningEntriesByParticipation(
        game: Game,
        tippGroup: TippGroup,
        participation: EntryParticipation
    ): List<Entry> =
        winningEntries(game, tippGroup).filter { it.participation == participation }

    fun isJackpotWinningEntry(game: Game, entry: Entry): Boolean =
        entry.participation == EntryParticipation.JACKPOT && isWinningEntry(game, entry)

    fun isLocalWinningEntry(game: Game, entry: Entry): Boolean =
        entry.participation == EntryParticipation.LOCAL_ONLY && isWinningEntry(game, entry)

    /**
     * Future split calculation (not wired).
     *
     * Planned rules:
     * - LOCAL_ONLY winners share [localPot] only
     * - JACKPOT winners share incoming + jackpot current pot
     * - Both winner types can coexist in the same tipp group
     * - Only jackpot winners break the carry chain
     *
     * TODO Phase 4: implement using [JackpotChainCalculator.jackpotCurrentCollected] and
     * [JackpotChainCalculator.localCurrentCollected].
     */
    fun calculateSplit(
        game: Game,
        tippGroup: TippGroup,
        incomingJackpot: Double
    ): SplitTippGroupWinnerOutcome {
        if (!game.hasResult || game.status != MatchStatus.FINISHED) {
            return SplitTippGroupWinnerOutcome.Pending
        }

        val entries = tippGroup.entries.map { entry ->
            JackpotV2EntryInput(
                entryId = entry.id,
                participation = entry.participation,
                currentRoundAmount = entry.currentRoundAmount,
                isCorrect = isWinningEntry(game, entry)
            )
        }

        val v2 = JackpotV2Calculator.calculate(
            JackpotV2RoundInput(incomingJackpot = incomingJackpot, entries = entries)
        )

        val localWinners = tippGroup.entries.filter {
            it.participation == EntryParticipation.LOCAL_ONLY && isWinningEntry(game, it)
        }
        val jackpotWinners = tippGroup.entries.filter {
            it.participation == EntryParticipation.JACKPOT && isWinningEntry(game, it)
        }

        val payoutsByEntryId = v2.payoutsByEntryId.mapValues { (entryId, amount) ->
            val entry = tippGroup.entries.first { it.id == entryId }
            EntryWinPayout(
                entryId = entryId,
                participation = entry.participation,
                winAmount = amount
            )
        }

        return SplitTippGroupWinnerOutcome.Resolved(
            localWinners = localWinners,
            jackpotWinners = jackpotWinners,
            localPot = v2.currentPot,
            localSharePerWinner = v2.currentSharePerWinner,
            incomingJackpot = incomingJackpot,
            jackpotCurrentCollected = tippGroup.entries
                .filter { it.participation == EntryParticipation.JACKPOT }
                .sumOf { it.currentRoundAmount },
            jackpotTotalPot = incomingJackpot + tippGroup.entries
                .filter { it.participation == EntryParticipation.JACKPOT }
                .sumOf { it.currentRoundAmount },
            jackpotSharePerWinner = v2.jackpotSharePerWinner,
            payoutsByEntryId = payoutsByEntryId
        )
    }

    fun buildEntryPayoutMap(resolved: SplitTippGroupWinnerOutcome.Resolved): Map<String, EntryWinPayout> =
        resolved.payoutsByEntryId

    fun toLocalWinnerPool(resolved: SplitTippGroupWinnerOutcome.Resolved): LocalWinnerPool? {
        if (resolved.localWinners.isEmpty()) return null
        return LocalWinnerPool(
            winners = resolved.localWinners,
            pot = resolved.localPot,
            sharePerWinner = resolved.localSharePerWinner
        )
    }

    fun toJackpotWinnerPool(resolved: SplitTippGroupWinnerOutcome.Resolved): JackpotWinnerPool? {
        if (resolved.jackpotWinners.isEmpty()) return null
        return JackpotWinnerPool(
            winners = resolved.jackpotWinners,
            incomingJackpot = resolved.incomingJackpot,
            currentCollected = resolved.jackpotCurrentCollected,
            totalPot = resolved.jackpotTotalPot,
            sharePerWinner = resolved.jackpotSharePerWinner
        )
    }

    fun toSplitPayouts(resolved: SplitTippGroupWinnerOutcome.Resolved): TippGroupSplitPayouts =
        TippGroupSplitPayouts(
            local = toLocalWinnerPool(resolved),
            jackpot = toJackpotWinnerPool(resolved),
            payoutsByEntryId = buildEntryPayoutMap(resolved)
        )

    /**
     * Only jackpot winners break the jackpot chain; local winners do not.
     */
    fun jackpotChainBreakSignal(outcome: SplitTippGroupWinnerOutcome): JackpotChainBreakSignal =
        when (outcome) {
            SplitTippGroupWinnerOutcome.Pending -> JackpotChainBreakSignal(hasJackpotWinner = false)
            is SplitTippGroupWinnerOutcome.Resolved -> JackpotChainBreakSignal(
                hasJackpotWinner = outcome.jackpotWinners.isNotEmpty(),
                jackpotWinnerEntryIds = outcome.jackpotWinners.map { it.id }
            )
        }

    fun compactSplitLabel(outcome: SplitTippGroupWinnerOutcome): String = when (outcome) {
        SplitTippGroupWinnerOutcome.Pending -> "Pending"
        is SplitTippGroupWinnerOutcome.Resolved -> {
            val local = outcome.localWinners.size
            val jackpot = outcome.jackpotWinners.size
            when {
                local > 0 && jackpot > 0 -> "$local local, $jackpot jackpot"
                local > 0 -> if (local == 1) "1 local winner" else "$local local winners"
                jackpot > 0 -> if (jackpot == 1) "1 jackpot winner" else "$jackpot jackpot winners"
                else -> "No winner"
            }
        }
    }

    /**
     * Future split settlement status (not wired).
     * TODO Phase 4: map to [SplitTippGroupSettlementSummary] via jackpot calculator.
     */
    fun previewSplitPayouts(
        game: Game,
        tippGroup: TippGroup,
        incomingJackpot: Double
    ): TippGroupSplitPayouts? {
        val outcome = calculateSplit(game, tippGroup, incomingJackpot)
        return if (outcome is SplitTippGroupWinnerOutcome.Resolved) {
            toSplitPayouts(outcome)
        } else {
            null
        }
    }

    // endregion
}
