package com.example.wmfunbett2026.data.jackpot

import com.example.wmfunbett2026.data.model.EntryParticipation
import com.example.wmfunbett2026.data.model.Game
import com.example.wmfunbett2026.data.model.MatchStatus
import com.example.wmfunbett2026.data.model.Round
import com.example.wmfunbett2026.data.model.TippGroup
import com.example.wmfunbett2026.data.model.TippGroupSettlementStatus
import com.example.wmfunbett2026.data.winner.SplitTippGroupWinnerOutcome
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
     * Missed jackpot rounds for catch-up when joining as JACKPOT at [game].
     *
     * Walks FINISHED prior games in chronological order only (no TimeScope linking).
     * Each prior game with any jackpot-active tipp group = one slot.
     * Stops when a prior FINISHED game has a JACKPOT winner in any tipp group.
     *
     * Ignores LIVE / NOT_STARTED games, player count, pot size, and payout amounts.
     * [tippGroup] is kept for API compatibility; slot count is game-level, not scope-level.
     */
    @Suppress("UNUSED_PARAMETER")
    fun calculateCatchUpSlots(
        round: Round,
        game: Game,
        tippGroup: TippGroup
    ): Int {
        val games = gamesInChronologicalOrder(round)
        val currentIndex = games.indexOfFirst { it.id == game.id }
        if (currentIndex <= 0) return 0

        var slots = 0

        for (index in currentIndex - 1 downTo 0) {
            val previousGame = games[index]
            if (!isFinishedGame(previousGame)) continue

            if (gameHasJackpotWinner(previousGame)) {
                return slots
            }

            if (gameHasJackpotActiveTippGroup(previousGame)) {
                slots++
            }
        }

        return slots
    }

    /** Catch-up = missed jackpot rounds × current round amount. */
    fun calculateCatchUpAmount(slots: Int, entryAmount: Double): Double =
        if (slots <= 0 || entryAmount <= 0.0) 0.0 else slots * entryAmount

    /**
     * Entry-aware incoming jackpot for [game] (not wired; legacy [calculateIncomingJackpot] unchanged).
     *
     * Walks FINISHED prior games in chronological order (no TimeScope).
     * Accumulates JACKPOT entry money only: currentRoundAmount + jackpotCatchUpAmount per entry.
     * Stops when a prior game has a JACKPOT winner in any tipp group.
     */
    @Suppress("UNUSED_PARAMETER")
    fun calculateIncomingJackpotEntryAware(
        round: Round,
        game: Game,
        tippGroup: TippGroup
    ): Double {
        val games = gamesInChronologicalOrder(round)
        val currentIndex = games.indexOfFirst { it.id == game.id }
        if (currentIndex <= 0) return 0.0

        var incoming = 0.0

        for (index in currentIndex - 1 downTo 0) {
            val previousGame = games[index]
            if (!isFinishedGame(previousGame)) continue

            if (gameHasJackpotWinner(previousGame)) {
                return incoming
            }

            if (gameHasJackpotActiveTippGroup(previousGame)) {
                incoming += gameJackpotContribution(previousGame)
            }
        }

        return incoming
    }

    /**
     * Future Add Entry breakdown for LOCAL_ONLY vs JACKPOT (not wired).
     * Uses slot-based catch-up, not [JackpotCarryItem] per-person amounts.
     */
    fun buildParticipationEntryJoinBreakdown(
        participation: EntryParticipation,
        catchUpSlots: Int,
        entryAmount: Double
    ): ParticipationEntryJoinBreakdown {
        val catchUpAmount = when (participation) {
            EntryParticipation.LOCAL_ONLY -> 0.0
            EntryParticipation.JACKPOT -> calculateCatchUpAmount(catchUpSlots, entryAmount)
        }
        val totalDue = when (participation) {
            EntryParticipation.LOCAL_ONLY -> entryAmount
            EntryParticipation.JACKPOT -> catchUpAmount + entryAmount
        }
        return ParticipationEntryJoinBreakdown(
            participation = participation,
            catchUpSlots = catchUpSlots,
            catchUpAmount = catchUpAmount,
            currentRoundEntryAmount = entryAmount,
            totalDue = totalDue
        )
    }

    fun buildJackpotCatchUpContext(
        round: Round,
        game: Game,
        tippGroup: TippGroup
    ): JackpotCatchUpContext {
        val entryAmount = tippGroup.entryAmount ?: 0.0
        val slots = calculateCatchUpSlots(round, game, tippGroup)
        return JackpotCatchUpContext(
            missedRoundSlots = slots,
            entryAmount = entryAmount,
            catchUpAmount = calculateCatchUpAmount(slots, entryAmount)
        )
    }

    private fun isFinishedGame(game: Game): Boolean =
        game.hasResult && game.status == MatchStatus.FINISHED

    private fun gameHasJackpotActiveTippGroup(game: Game): Boolean =
        game.tippGroups.any { group ->
            group.entries.any { it.participation == EntryParticipation.JACKPOT }
        }

    /** JACKPOT winner in any tipp group on this game resets the missed-round chain. */
    private fun gameHasJackpotWinner(game: Game): Boolean =
        game.tippGroups.any { group ->
            TippGroupWinnerEngine.winningEntriesByParticipation(
                game,
                group,
                EntryParticipation.JACKPOT
            ).isNotEmpty()
        }

    /** Sum of JACKPOT entry stakes (current round + catch-up) across all tipp groups in one game. */
    private fun gameJackpotContribution(game: Game): Double =
        game.tippGroups.sumOf { group -> jackpotEntryContribution(group) }

    private fun jackpotEntryContribution(tippGroup: TippGroup): Double =
        tippGroup.entries
            .asSequence()
            .filter { it.participation == EntryParticipation.JACKPOT }
            .sumOf { it.currentRoundAmount + it.jackpotCatchUpAmount }

    /**
     * Split carry-over summary for entry-participation settlement (not wired to repository or UI).
     *
     * Local pot never carries; jackpot [JackpotPotSummary.carriedOut] when no JACKPOT winner.
     * Only JACKPOT winners reset the chain for the next game.
     */
    fun previewSplitCarryOverSummary(
        round: Round,
        game: Game,
        tippGroup: TippGroup
    ): SplitTippGroupSettlementSummary? {
        if (!game.hasResult || game.status != MatchStatus.FINISHED) {
            return null
        }

        val incoming = calculateIncomingJackpotEntryAware(round, game, tippGroup)
        val split = TippGroupWinnerEngine.calculateSplit(game, tippGroup, incoming)
        if (split !is SplitTippGroupWinnerOutcome.Resolved) {
            return null
        }

        val localSummary = LocalPotSummary(
            pot = split.localPot,
            winnerCount = split.localWinners.size,
            sharePerWinner = split.localSharePerWinner,
            closed = split.localWinners.isEmpty() && split.localPot > 0.0
        )

        val jackpotCarriedOut = if (split.jackpotWinners.isEmpty()) {
            split.jackpotTotalPot
        } else {
            0.0
        }

        val jackpotSummary = JackpotPotSummary(
            incomingJackpot = split.incomingJackpot,
            currentCollected = split.jackpotCurrentCollected,
            totalPot = split.jackpotTotalPot,
            carriedOut = jackpotCarriedOut,
            winnerCount = split.jackpotWinners.size,
            sharePerWinner = split.jackpotSharePerWinner
        )

        val status = when {
            split.localWinners.isNotEmpty() || split.jackpotWinners.isNotEmpty() ->
                TippGroupSettlementStatus.WINNERS
            else -> TippGroupSettlementStatus.NO_WINNERS
        }

        return SplitTippGroupSettlementSummary(
            status = status,
            local = localSummary,
            jackpot = jackpotSummary
        )
    }

    // endregion
}
