package com.example.wmfunbett2026.data.winner

import com.example.wmfunbett2026.data.jackpot.JackpotChainBreakSignal
import com.example.wmfunbett2026.data.jackpot.JackpotChainCalculator
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
 * **Split (not wired to UI):** [calculateSplit], [previewSplitPayouts], participation filters.
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
     * Split local vs jackpot winner calculation (not wired to repository or UI).
     *
     * LOCAL_ONLY winners share [localPot] only.
     * JACKPOT winners share [incomingJackpot] + [jackpotCurrentCollected] (current round stakes only).
     * Only jackpot winners break the carry chain ([jackpotChainBreakSignal]).
     */
    fun calculateSplit(
        game: Game,
        tippGroup: TippGroup,
        incomingJackpot: Double
    ): SplitTippGroupWinnerOutcome {
        if (!game.hasResult || game.status != MatchStatus.FINISHED) {
            return SplitTippGroupWinnerOutcome.Pending
        }

        val localWinners = winningEntriesByParticipation(
            game,
            tippGroup,
            EntryParticipation.LOCAL_ONLY
        )
        val jackpotWinners = winningEntriesByParticipation(
            game,
            tippGroup,
            EntryParticipation.JACKPOT
        )

        val localPot = JackpotChainCalculator.localCurrentCollected(tippGroup)
        val jackpotCurrentCollected = JackpotChainCalculator.jackpotCurrentCollected(tippGroup)
        val jackpotTotalPot = incomingJackpot + jackpotCurrentCollected

        val localSharePerWinner = if (localWinners.isNotEmpty()) {
            localPot / localWinners.size
        } else {
            0.0
        }
        val jackpotSharePerWinner = if (jackpotWinners.isNotEmpty()) {
            jackpotTotalPot / jackpotWinners.size
        } else {
            0.0
        }

        val resolved = SplitTippGroupWinnerOutcome.Resolved(
            localWinners = localWinners,
            jackpotWinners = jackpotWinners,
            localPot = localPot,
            localSharePerWinner = localSharePerWinner,
            incomingJackpot = incomingJackpot,
            jackpotCurrentCollected = jackpotCurrentCollected,
            jackpotTotalPot = jackpotTotalPot,
            jackpotSharePerWinner = jackpotSharePerWinner,
            payoutsByEntryId = emptyMap()
        )

        return resolved.copy(payoutsByEntryId = buildEntryPayoutMap(resolved))
    }

    fun buildEntryPayoutMap(resolved: SplitTippGroupWinnerOutcome.Resolved): Map<String, EntryWinPayout> {
        val payouts = LinkedHashMap<String, EntryWinPayout>(resolved.payoutsByEntryId.size)
        resolved.localWinners.forEach { entry ->
            payouts[entry.id] = EntryWinPayout(
                entryId = entry.id,
                participation = EntryParticipation.LOCAL_ONLY,
                winAmount = resolved.localSharePerWinner
            )
        }
        resolved.jackpotWinners.forEach { entry ->
            payouts[entry.id] = EntryWinPayout(
                entryId = entry.id,
                participation = EntryParticipation.JACKPOT,
                winAmount = resolved.jackpotSharePerWinner
            )
        }
        return payouts
    }

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

    fun previewSplitPayouts(
        game: Game,
        tippGroup: TippGroup,
        incomingJackpot: Double
    ): TippGroupSplitPayouts? =
        when (val outcome = calculateSplit(game, tippGroup, incomingJackpot)) {
            is SplitTippGroupWinnerOutcome.Resolved -> toSplitPayouts(outcome)
            SplitTippGroupWinnerOutcome.Pending -> null
        }

    // endregion
}
