package com.example.wmfunbett2026.data.jackpot

import com.example.wmfunbett2026.data.model.Entry
import com.example.wmfunbett2026.data.model.EntryParticipation
import com.example.wmfunbett2026.data.model.Game
import com.example.wmfunbett2026.data.model.MatchStatus
import com.example.wmfunbett2026.data.model.Round
import com.example.wmfunbett2026.data.model.TippGroup
import com.example.wmfunbett2026.data.winner.TippGroupWinnerEngine

/**
 * Builds V2 settlement per [docs/JACKPOT_RULES_V2.md] for repository and UI.
 */
object JackpotV2SettlementBuilder {

    fun incomingJackpot(round: Round, game: Game, tippGroup: TippGroup): Double {
        val games = JackpotChainCalculator.gamesInChronologicalOrder(round)
        val currentIndex = games.indexOfFirst { it.id == game.id }
        if (currentIndex <= 0) return 0.0

        val scope = tippGroup.timeScope
        var carry = 0.0

        for (index in 0 until currentIndex) {
            val priorGame = games[index]
            val priorGroup = priorGame.tippGroups.find { it.timeScope == scope } ?: continue
            if (!priorGame.hasResult || priorGame.status != MatchStatus.FINISHED) continue
            if (priorGroup.entries.isEmpty()) continue

            val v2 = JackpotV2Calculator.calculate(
                JackpotV2RoundInput(
                    incomingJackpot = carry,
                    entries = toV2Inputs(priorGame, priorGroup)
                )
            )
            carry = v2.carryForwardJackpot
        }
        return carry
    }

    /**
     * Person-specific late jackpot join catch-up per [docs/JACKPOT_RULES_V2.md].
     *
     * Sums per-Tipp-Group buy-ins for active unsettled jackpot-enabled groups in prior
     * FINISHED games that [friendId] has **not** already joined as JACKPOT.
     * Local-only entries by the same person do not waive catch-up for that group.
     *
     * @param friendId Selected friend id; null/blank returns zero catch-up (UI before selection).
     */
    fun calculateCatchUp(
        round: Round,
        game: Game,
        @Suppress("UNUSED_PARAMETER") tippGroup: TippGroup,
        friendId: String?
    ): JackpotCatchUpResult {
        if (friendId.isNullOrBlank()) {
            return JackpotCatchUpResult(amount = 0.0, missedRoundSlots = 0)
        }

        val games = JackpotChainCalculator.gamesInChronologicalOrder(round)
        val currentIndex = games.indexOfFirst { it.id == game.id }
        if (currentIndex <= 0) {
            return JackpotCatchUpResult(amount = 0.0, missedRoundSlots = 0)
        }

        var catchUp = 0.0
        var missedSlots = 0

        for (index in 0 until currentIndex) {
            val priorGame = games[index]
            if (!priorGame.hasResult || priorGame.status != MatchStatus.FINISHED) continue

            for (priorGroup in priorGame.tippGroups) {
                if (!isJackpotEnabledGroup(priorGroup)) continue
                if (isJackpotSettledGroup(round, priorGame, priorGroup)) continue
                if (hasPersonJackpotEntryInGroup(friendId, priorGroup)) continue

                val roundBuyIn = perGroupBuyIn(priorGroup) ?: continue
                catchUp += roundBuyIn
                missedSlots++
            }
        }

        return JackpotCatchUpResult(amount = catchUp, missedRoundSlots = missedSlots)
    }

    private fun hasPersonJackpotEntryInGroup(friendId: String, tippGroup: TippGroup): Boolean =
        tippGroup.entries.any {
            it.friendId == friendId && it.participation == EntryParticipation.JACKPOT
        }

    private fun isJackpotEnabledGroup(tippGroup: TippGroup): Boolean =
        tippGroup.entries.any { it.participation == EntryParticipation.JACKPOT }

    private fun perGroupBuyIn(tippGroup: TippGroup): Double? {
        val buyIn = tippGroup.entryAmount
            ?: tippGroup.entries.firstOrNull()?.currentRoundAmount
        return buyIn?.takeIf { it > 0.0 }
    }

    private fun isJackpotSettledGroup(round: Round, game: Game, tippGroup: TippGroup): Boolean {
        if (tippGroup.entries.isEmpty()) return true
        val incoming = incomingJackpot(round, game, tippGroup)
        val v2 = JackpotV2Calculator.calculate(
            JackpotV2RoundInput(
                incomingJackpot = incoming,
                entries = toV2Inputs(game, tippGroup)
            )
        )
        return v2.jackpotWinners.isNotEmpty()
    }

    fun settle(round: Round, game: Game, tippGroup: TippGroup): TippGroupV2Settlement {
        val incoming = incomingJackpot(round, game, tippGroup)

        if (!game.hasResult || game.status != MatchStatus.FINISHED) {
            return TippGroupV2Settlement(
                phase = TippGroupV2SettlementPhase.WAITING_RESULT,
                incomingJackpot = incoming,
                calculation = null,
                winnerLines = emptyList()
            )
        }

        if (tippGroup.entries.isEmpty()) {
            return TippGroupV2Settlement(
                phase = TippGroupV2SettlementPhase.NO_ENTRIES,
                incomingJackpot = incoming,
                calculation = null,
                winnerLines = emptyList()
            )
        }

        val v2 = JackpotV2Calculator.calculate(
            JackpotV2RoundInput(
                incomingJackpot = incoming,
                entries = toV2Inputs(game, tippGroup)
            )
        )

        val winnerLines = buildWinnerLines(tippGroup, v2)
        val phase = if (v2.currentWinners.isEmpty()) {
            TippGroupV2SettlementPhase.FINISHED_NO_WINNERS
        } else {
            TippGroupV2SettlementPhase.FINISHED_WINNERS
        }

        return TippGroupV2Settlement(
            phase = phase,
            incomingJackpot = incoming,
            calculation = v2,
            winnerLines = winnerLines
        )
    }

    private fun toV2Inputs(game: Game, tippGroup: TippGroup): List<JackpotV2EntryInput> =
        tippGroup.entries.map { entry -> toV2Input(game, entry) }

    private fun toV2Input(game: Game, entry: Entry): JackpotV2EntryInput =
        JackpotV2EntryInput(
            entryId = entry.id,
            participation = entry.participation,
            currentRoundAmount = entry.currentRoundAmount,
            isCorrect = TippGroupWinnerEngine.isWinningEntry(game, entry)
        )

    private fun buildWinnerLines(
        tippGroup: TippGroup,
        v2: JackpotV2Result
    ): List<TippGroupV2WinnerLine> {
        val entryById = tippGroup.entries.associateBy { it.id }
        return v2.payoutsByEntryId.mapNotNull { (entryId, total) ->
            val entry = entryById[entryId] ?: return@mapNotNull null
            val jackpotShare = if (entry.participation == EntryParticipation.JACKPOT) {
                v2.jackpotSharePerWinner
            } else {
                0.0
            }
            TippGroupV2WinnerLine(
                entryId = entryId,
                name = entry.friendName,
                participation = entry.participation,
                currentShare = v2.currentSharePerWinner,
                jackpotShare = jackpotShare,
                totalPayout = total
            )
        }
    }
}
