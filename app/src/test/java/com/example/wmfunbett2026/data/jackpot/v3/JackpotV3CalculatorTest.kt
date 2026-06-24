package com.example.wmfunbett2026.data.jackpot.v3

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class JackpotV3CalculatorTest {

    private companion object {
        const val DELTA = 0.001
    }

    @Test
    fun calculateMainJackpotAmount_sumsContributedAmounts() {
        val rounds = listOf(
            sourceRound("game-1", "tipp-ht", entry = 10.0, contributed = 30.0),
            sourceRound("game-2", "tipp-ft", entry = 10.0, contributed = 20.0),
            sourceRound("game-4", "tipp-pen", entry = 10.0, contributed = 40.0)
        )

        val amount = JackpotV3Calculator.calculateMainJackpotAmount(rounds)

        assertEquals(90.0, amount, DELTA)
    }

    @Test
    fun calculateCatchUpAmount_newPlayerMissedAllSourceRounds() {
        val rounds = listOf(
            sourceRound("game-1", "tipp-ht", entry = 10.0, contributed = 30.0),
            sourceRound("game-2", "tipp-ft", entry = 10.0, contributed = 20.0),
            sourceRound("game-4", "tipp-pen", entry = 10.0, contributed = 40.0)
        )

        val breakdown = JackpotV3Calculator.calculateCatchUpAmount(
            sourceRounds = rounds,
            alreadyPaidTippGroupIds = emptySet()
        )

        assertEquals(0.0, breakdown.currentAmount, DELTA)
        assertEquals(30.0, breakdown.catchUpAmount, DELTA)
        assertEquals(30.0, breakdown.totalAmount, DELTA)
        assertEquals(3, breakdown.missedSourceRoundCount)
    }

    @Test
    fun calculateCatchUpAmount_partialCatchUp_oneMissedRound() {
        val rounds = listOf(
            sourceRound("game-1", "tipp-ht", entry = 10.0, contributed = 30.0),
            sourceRound("game-2", "tipp-ft", entry = 10.0, contributed = 20.0),
            sourceRound("game-4", "tipp-pen", entry = 10.0, contributed = 40.0)
        )

        val breakdown = JackpotV3Calculator.calculateCatchUpAmount(
            sourceRounds = rounds,
            alreadyPaidTippGroupIds = setOf("tipp-ht", "tipp-ft")
        )

        assertEquals(10.0, breakdown.catchUpAmount, DELTA)
        assertEquals(10.0, breakdown.totalAmount, DELTA)
        assertEquals(1, breakdown.missedSourceRoundCount)
    }

    @Test
    fun calculateCatchUpAmount_fullyPaid_noCatchUp() {
        val rounds = listOf(
            sourceRound("game-1", "tipp-ht", entry = 10.0, contributed = 30.0),
            sourceRound("game-2", "tipp-ft", entry = 10.0, contributed = 20.0),
            sourceRound("game-4", "tipp-pen", entry = 10.0, contributed = 40.0)
        )

        val breakdown = JackpotV3Calculator.calculateCatchUpAmount(
            sourceRounds = rounds,
            alreadyPaidTippGroupIds = setOf("tipp-ht", "tipp-ft", "tipp-pen")
        )

        assertEquals(0.0, breakdown.catchUpAmount, DELTA)
        assertEquals(0.0, breakdown.totalAmount, DELTA)
        assertEquals(0, breakdown.missedSourceRoundCount)
    }

    @Test
    fun calculateCatchUpAmount_usesEntryAmountNotContributedAmount() {
        val rounds = listOf(
            sourceRound("game-1", "tipp-ft", entry = 10.0, contributed = 30.0)
        )

        val breakdown = JackpotV3Calculator.calculateCatchUpAmount(
            sourceRounds = rounds,
            alreadyPaidTippGroupIds = emptySet()
        )

        assertEquals(10.0, breakdown.catchUpAmount, DELTA)
    }

    @Test
    fun canStartJackpotRound_activeLock() {
        assertTrue(JackpotV3Calculator.canStartJackpotRound(JackpotRoundStatus.AVAILABLE))
        assertFalse(JackpotV3Calculator.canStartJackpotRound(JackpotRoundStatus.ACTIVE))
        assertTrue(JackpotV3Calculator.canStartJackpotRound(JackpotRoundStatus.SETTLED))
    }

    @Test
    fun settleJackpotWinner_totalsPotsAndRequiresReset() {
        val result = JackpotV3Calculator.settleJackpotWinner(
            currentRoundPot = 30.0,
            jackpotPot = 130.0,
            winnerCount = 2
        )

        assertEquals(30.0, result.currentRoundPot, DELTA)
        assertEquals(130.0, result.jackpotPot, DELTA)
        assertEquals(160.0, result.totalPayout, DELTA)
        assertEquals(2, result.winnerCount)
        assertTrue(result.jackpotResetRequired)
    }

    private fun sourceRound(
        gameId: String,
        tippGroupId: String,
        entry: Double,
        contributed: Double
    ) = JackpotSourceRound(
        gameId = gameId,
        tippGroupId = tippGroupId,
        tippGroupName = tippGroupId,
        entryAmount = entry,
        contributedAmount = contributed
    )
}
