package com.example.wmfunbett2026.data.jackpot.v3

/**
 * Pure V3 jackpot calculations. See [docs/JACKPOT_RULES_V3.md].
 */
object JackpotV3Calculator {

    fun calculateMainJackpotAmount(sourceRounds: List<JackpotSourceRound>): Double =
        sourceRounds.sumOf { it.contributedAmount }

    fun calculateCatchUpAmount(
        sourceRounds: List<JackpotSourceRound>,
        alreadyPaidTippGroupIds: Set<String>
    ): JackpotJoinBreakdown {
        val missed = sourceRounds.filter { it.tippGroupId !in alreadyPaidTippGroupIds }
        val catchUp = missed.sumOf { it.entryAmount }
        return JackpotJoinBreakdown(
            currentAmount = 0.0,
            catchUpAmount = catchUp,
            totalAmount = catchUp,
            missedSourceRoundCount = missed.size
        )
    }

    fun canStartJackpotRound(status: JackpotRoundStatus): Boolean =
        when (status) {
            JackpotRoundStatus.AVAILABLE,
            JackpotRoundStatus.SETTLED -> true
            JackpotRoundStatus.ACTIVE -> false
        }

    fun settleJackpotWinner(
        currentRoundPot: Double,
        jackpotPot: Double,
        winnerCount: Int
    ): JackpotSettlementResult =
        JackpotSettlementResult(
            currentRoundPot = currentRoundPot,
            jackpotPot = jackpotPot,
            totalPayout = currentRoundPot + jackpotPot,
            winnerCount = winnerCount,
            jackpotResetRequired = true
        )
}
