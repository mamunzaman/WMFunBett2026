package com.example.wmfunbett2026.data.jackpot

import com.example.wmfunbett2026.data.model.EntryParticipation

/**
 * Pure V2 jackpot/current-pot calculator per [docs/JACKPOT_RULES_V2.md].
 *
 * Not wired to UI or repository — unit-tested in isolation first.
 */
object JackpotV2Calculator {

    fun calculate(input: JackpotV2RoundInput): JackpotV2Result {
        val currentPot = input.entries.sumOf { it.currentRoundAmount }
        val jackpotPot = input.incomingJackpot

        val correctEntries = input.entries.filter { it.isCorrect }
        val currentWinners = correctEntries.map { it.entryId }

        val jackpotQualifiedCorrect = correctEntries.filter {
            it.participation == EntryParticipation.JACKPOT
        }
        val jackpotWinners = jackpotQualifiedCorrect.map { it.entryId }

        val jackpotParticipantMoney = input.entries
            .asSequence()
            .filter { it.participation == EntryParticipation.JACKPOT }
            .sumOf { it.currentRoundAmount }

        val localMoney = input.entries
            .asSequence()
            .filter { it.participation == EntryParticipation.LOCAL_ONLY }
            .sumOf { it.currentRoundAmount }

        val currentSharePerWinner = if (currentWinners.isNotEmpty()) {
            currentPot / currentWinners.size
        } else {
            0.0
        }

        val jackpotSharePerWinner = if (jackpotWinners.isNotEmpty()) {
            jackpotPot / jackpotWinners.size
        } else {
            0.0
        }

        val payoutsByEntryId = LinkedHashMap<String, Double>(correctEntries.size)
        for (entry in correctEntries) {
            var payout = currentSharePerWinner
            if (entry.participation == EntryParticipation.JACKPOT) {
                payout += jackpotSharePerWinner
            }
            payoutsByEntryId[entry.entryId] = payout
        }

        val carryForwardJackpot = when {
            jackpotWinners.isNotEmpty() -> 0.0
            currentWinners.isNotEmpty() -> input.incomingJackpot
            else -> input.incomingJackpot + jackpotParticipantMoney
        }

        val localReturnedAmount = if (currentWinners.isEmpty()) localMoney else 0.0

        return JackpotV2Result(
            currentPot = currentPot,
            jackpotPot = jackpotPot,
            currentWinners = currentWinners,
            jackpotWinners = jackpotWinners,
            currentSharePerWinner = currentSharePerWinner,
            jackpotSharePerWinner = jackpotSharePerWinner,
            payoutsByEntryId = payoutsByEntryId,
            carryForwardJackpot = carryForwardJackpot,
            localReturnedAmount = localReturnedAmount
        )
    }
}
