package com.example.wmfunbett2026.data.jackpot.v3

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class JackpotV3ModelsTest {

    private companion object {
        const val DELTA = 0.001
    }

    @Test
    fun mainJackpotPot_initializesCorrectly() {
        val sourceRound = JackpotSourceRound(
            gameId = "game-1",
            tippGroupId = "tipp-ft",
            tippGroupName = "Full Time",
            entryAmount = 10.0,
            contributedAmount = 30.0
        )
        val pot = MainJackpotPot(
            amount = 30.0,
            sourceRounds = listOf(sourceRound),
            isActive = true,
            activeJackpotTippGroupId = "tipp-ft-game-8"
        )

        assertEquals(30.0, pot.amount, DELTA)
        assertEquals(1, pot.sourceRounds.size)
        assertEquals(sourceRound, pot.sourceRounds.first())
        assertTrue(pot.isActive)
        assertEquals("tipp-ft-game-8", pot.activeJackpotTippGroupId)
    }

    @Test
    fun mainJackpotPot_inactiveHasNoActiveTippGroup() {
        val pot = MainJackpotPot(
            amount = 90.0,
            sourceRounds = emptyList(),
            isActive = false,
            activeJackpotTippGroupId = null
        )

        assertEquals(90.0, pot.amount, DELTA)
        assertTrue(pot.sourceRounds.isEmpty())
        assertFalse(pot.isActive)
        assertNull(pot.activeJackpotTippGroupId)
    }

    @Test
    fun jackpotSourceRound_storesValuesCorrectly() {
        val round = JackpotSourceRound(
            gameId = "game-2",
            tippGroupId = "tipp-ht",
            tippGroupName = "Half Time",
            entryAmount = 10.0,
            contributedAmount = 20.0
        )

        assertEquals("game-2", round.gameId)
        assertEquals("tipp-ht", round.tippGroupId)
        assertEquals("Half Time", round.tippGroupName)
        assertEquals(10.0, round.entryAmount, DELTA)
        assertEquals(20.0, round.contributedAmount, DELTA)
    }

    @Test
    fun jackpotJoinBreakdown_totalAmountWorks() {
        val breakdown = JackpotJoinBreakdown(
            currentAmount = 10.0,
            catchUpAmount = 30.0,
            totalAmount = 40.0,
            missedSourceRoundCount = 3
        )

        assertEquals(10.0, breakdown.currentAmount, DELTA)
        assertEquals(30.0, breakdown.catchUpAmount, DELTA)
        assertEquals(40.0, breakdown.totalAmount, DELTA)
        assertEquals(10.0 + 30.0, breakdown.totalAmount, DELTA)
        assertEquals(3, breakdown.missedSourceRoundCount)
    }

    @Test
    fun jackpotRoundStatus_valuesExist() {
        assertEquals(3, JackpotRoundStatus.entries.size)
        assertEquals(JackpotRoundStatus.AVAILABLE, JackpotRoundStatus.valueOf("AVAILABLE"))
        assertEquals(JackpotRoundStatus.ACTIVE, JackpotRoundStatus.valueOf("ACTIVE"))
        assertEquals(JackpotRoundStatus.SETTLED, JackpotRoundStatus.valueOf("SETTLED"))
    }

    @Test
    fun jackpotSettlementResult_storesPayoutValues() {
        val result = JackpotSettlementResult(
            currentRoundPot = 30.0,
            jackpotPot = 130.0,
            totalPayout = 160.0,
            winnerCount = 2,
            jackpotResetRequired = true
        )

        assertEquals(30.0, result.currentRoundPot, DELTA)
        assertEquals(130.0, result.jackpotPot, DELTA)
        assertEquals(160.0, result.totalPayout, DELTA)
        assertEquals(2, result.winnerCount)
        assertTrue(result.jackpotResetRequired)
    }

    @Test
    fun jackpotSettlementResult_noWinnerDoesNotRequireReset() {
        val result = JackpotSettlementResult(
            currentRoundPot = 30.0,
            jackpotPot = 120.0,
            totalPayout = 0.0,
            winnerCount = 0,
            jackpotResetRequired = false
        )

        assertEquals(0.0, result.totalPayout, DELTA)
        assertEquals(0, result.winnerCount)
        assertFalse(result.jackpotResetRequired)
    }
}
