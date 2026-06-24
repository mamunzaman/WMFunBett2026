package com.example.wmfunbett2026.data.jackpot

import com.example.wmfunbett2026.data.model.EntryParticipation
import com.example.wmfunbett2026.data.model.Game
import com.example.wmfunbett2026.data.model.MatchStatus
import com.example.wmfunbett2026.data.model.Round
import com.example.wmfunbett2026.data.model.TippGroup
import com.example.wmfunbett2026.data.winner.TippGroupWinnerEngine
import com.example.wmfunbett2026.data.winner.TippGroupWinnerOutcome

/**
 * Jackpot chain calculator.
 *
 * **Legacy (wired):** [calculateIncomingJackpot], [calculateCarryOverSummary], [currentCollectedAmount].
 *
 * **Future (not wired):** entry-participation helpers in the region below.
 * Jackpot flow: Game 1 no jackpot winner → Game 2 receives → … → jackpot winner → next game €0.
 * Catch-up: missed jackpot rounds × entry amount (not player count or incoming pot size).
 */
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
        val settlement = JackpotV2SettlementBuilder.settle(round, game, tippGroup)
        val currentCollected = tippGroup.entries.sumOf { it.currentRoundAmount }
        val incomingJackpot = settlement.incomingJackpot
        val calc = settlement.calculation

        return when (settlement.phase) {
            TippGroupV2SettlementPhase.WAITING_RESULT,
            TippGroupV2SettlementPhase.NO_ENTRIES -> JackpotCarryOverSummary(
                incomingJackpot = incomingJackpot,
                currentCollected = currentCollected,
                totalPot = incomingJackpot + currentCollected,
                carriedOut = 0.0,
                hasWinner = false,
                winnerCount = 0,
                sharePerWinner = 0.0
            )
            TippGroupV2SettlementPhase.FINISHED_NO_WINNERS -> JackpotCarryOverSummary(
                incomingJackpot = incomingJackpot,
                currentCollected = currentCollected,
                totalPot = incomingJackpot + currentCollected,
                carriedOut = calc?.carryForwardJackpot ?: 0.0,
                hasWinner = false,
                winnerCount = 0,
                sharePerWinner = 0.0
            )
            TippGroupV2SettlementPhase.FINISHED_WINNERS -> JackpotCarryOverSummary(
                incomingJackpot = incomingJackpot,
                currentCollected = currentCollected,
                totalPot = incomingJackpot + currentCollected,
                carriedOut = 0.0,
                hasWinner = true,
                winnerCount = calc?.currentWinners?.size ?: 0,
                sharePerWinner = calc?.currentSharePerWinner ?: 0.0
            )
        }
    }

    fun calculateIncomingJackpot(
        round: Round,
        game: Game,
        tippGroup: TippGroup
    ): Double = JackpotV2SettlementBuilder.incomingJackpot(round, game, tippGroup)

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

    // region Future entry-participation helpers (not wired to repository or UI)

    /** Sum of [EntryParticipation.JACKPOT] current-round stakes only. */
    fun jackpotCurrentCollected(tippGroup: TippGroup): Double =
        tippGroup.entries
            .asSequence()
            .filter { it.participation == EntryParticipation.JACKPOT }
            .sumOf { it.currentRoundAmount }

    /** Sum of [EntryParticipation.LOCAL_ONLY] current-round stakes only. */
    fun localCurrentCollected(tippGroup: TippGroup): Double =
        tippGroup.entries
            .asSequence()
            .filter { it.participation == EntryParticipation.LOCAL_ONLY }
            .sumOf { it.currentRoundAmount }

    /**
     * Missed jackpot-chain round count for late join (see [JackpotV2SettlementBuilder.calculateCatchUp]).
     */
    fun calculateCatchUpSlots(
        round: Round,
        game: Game,
        tippGroup: TippGroup,
        friendId: String? = null
    ): Int = JackpotV2SettlementBuilder.calculateCatchUp(round, game, tippGroup, friendId).missedRoundSlots

    fun calculateCatchUpAmountForJoin(
        round: Round,
        game: Game,
        tippGroup: TippGroup,
        friendId: String? = null
    ): Double = JackpotV2SettlementBuilder.calculateCatchUp(round, game, tippGroup, friendId).amount

    /** @deprecated Use [calculateCatchUpAmountForJoin]; kept for slot×amount helper in tests. */
    fun calculateCatchUpAmount(slots: Int, entryAmount: Double): Double =
        if (slots <= 0 || entryAmount <= 0.0) 0.0 else slots * entryAmount

    /**
     * Future entry-aware incoming jackpot (not wired; legacy [calculateIncomingJackpot] unchanged).
     *
     * Planned rules:
     * - Only FINISHED games affect the chain
     * - Only JACKPOT entry money accumulates ([jackpotCurrentCollected])
     * - Chain breaks when a prior game has a jackpot winner (not any local winner)
     * - LOCAL_ONLY money never carries forward
     *
     * TODO Phase 3: implement; wire via repository when winner engine supports split outcomes.
     */
    fun calculateIncomingJackpotEntryAware(
        round: Round,
        game: Game,
        tippGroup: TippGroup
    ): Double = JackpotV2SettlementBuilder.incomingJackpot(round, game, tippGroup)

    /**
     * Future Add Entry breakdown for LOCAL_ONLY vs JACKPOT (not wired).
     * Uses slot-based catch-up, not [JackpotCarryItem] per-person amounts.
     */
    fun buildParticipationEntryJoinBreakdown(
        participation: EntryParticipation,
        catchUpAmount: Double,
        catchUpSlots: Int,
        entryAmount: Double
    ): ParticipationEntryJoinBreakdown {
        val resolvedCatchUp = when (participation) {
            EntryParticipation.LOCAL_ONLY -> 0.0
            EntryParticipation.JACKPOT -> catchUpAmount.coerceAtLeast(0.0)
        }
        val totalDue = when (participation) {
            EntryParticipation.LOCAL_ONLY -> entryAmount
            EntryParticipation.JACKPOT -> resolvedCatchUp + entryAmount
        }
        return ParticipationEntryJoinBreakdown(
            participation = participation,
            catchUpSlots = if (participation == EntryParticipation.JACKPOT) catchUpSlots else 0,
            catchUpAmount = resolvedCatchUp,
            currentRoundEntryAmount = entryAmount,
            totalDue = totalDue
        )
    }

    fun buildJackpotCatchUpContext(
        round: Round,
        game: Game,
        tippGroup: TippGroup,
        friendId: String? = null
    ): JackpotCatchUpContext {
        val entryAmount = tippGroup.entryAmount ?: 0.0
        val catchUp = JackpotV2SettlementBuilder.calculateCatchUp(round, game, tippGroup, friendId)
        return JackpotCatchUpContext(
            missedRoundSlots = catchUp.missedRoundSlots,
            entryAmount = entryAmount,
            catchUpAmount = catchUp.amount
        )
    }

    /**
     * Future split carry-over summary (not wired).
     *
     * [LocalPotSummary]: local winners share [localCurrentCollected] only; no auto carry.
     * [JackpotPotSummary]: jackpot winners share incoming + [jackpotCurrentCollected];
     * [JackpotPotSummary.carriedOut] when no jackpot winner.
     *
     * TODO Phase 3: integrate with split winner engine.
     */
    @Suppress("UNUSED_PARAMETER")
    fun previewSplitCarryOverSummary(
        round: Round,
        game: Game,
        tippGroup: TippGroup
    ): SplitTippGroupSettlementSummary? = null

    // endregion
}
