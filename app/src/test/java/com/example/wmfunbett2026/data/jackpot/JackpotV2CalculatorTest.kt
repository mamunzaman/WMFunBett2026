package com.example.wmfunbett2026.data.jackpot

import com.example.wmfunbett2026.data.model.EntryParticipation
import org.junit.Assert.assertEquals
import org.junit.Test

class JackpotV2CalculatorTest {

    private companion object {
        const val DELTA = 0.001
        const val STAKE = 5.0
    }

    private fun jackpotEntry(id: String, correct: Boolean = false) = JackpotV2EntryInput(
        entryId = id,
        participation = EntryParticipation.JACKPOT,
        currentRoundAmount = STAKE,
        isCorrect = correct
    )

    private fun localEntry(id: String, correct: Boolean = false) = JackpotV2EntryInput(
        entryId = id,
        participation = EntryParticipation.LOCAL_ONLY,
        currentRoundAmount = STAKE,
        isCorrect = correct
    )

    @Test
    fun scenario1_jackpotStarts_noWinner_carry15() {
        val result = JackpotV2Calculator.calculate(
            JackpotV2RoundInput(
                incomingJackpot = 0.0,
                entries = listOf(
                    jackpotEntry("ole"),
                    jackpotEntry("thomas"),
                    jackpotEntry("bello")
                )
            )
        )

        assertEquals(15.0, result.currentPot, DELTA)
        assertEquals(0.0, result.jackpotPot, DELTA)
        assertEquals(0, result.currentWinners.size)
        assertEquals(0, result.jackpotWinners.size)
        assertEquals(15.0, result.carryForwardJackpot, DELTA)
        assertEquals(0.0, result.localReturnedAmount, DELTA)
    }

    @Test
    fun scenario2_jackpotGrows_noWinner_carry30() {
        val result = JackpotV2Calculator.calculate(
            JackpotV2RoundInput(
                incomingJackpot = 15.0,
                entries = listOf(
                    jackpotEntry("ole"),
                    jackpotEntry("thomas"),
                    jackpotEntry("bello")
                )
            )
        )

        assertEquals(15.0, result.currentPot, DELTA)
        assertEquals(15.0, result.jackpotPot, DELTA)
        assertEquals(30.0, result.carryForwardJackpot, DELTA)
        assertEquals(0.0, result.localReturnedAmount, DELTA)
    }

    @Test
    fun scenario3_localAndJackpotWinners() {
        val result = JackpotV2Calculator.calculate(
            JackpotV2RoundInput(
                incomingJackpot = 30.0,
                entries = listOf(
                    jackpotEntry("ole", correct = true),
                    jackpotEntry("thomas", correct = false),
                    localEntry("mamun", correct = true)
                )
            )
        )

        assertEquals(15.0, result.currentPot, DELTA)
        assertEquals(30.0, result.jackpotPot, DELTA)
        assertEquals(7.5, result.currentSharePerWinner, DELTA)
        assertEquals(30.0, result.jackpotSharePerWinner, DELTA)
        assertEquals(37.5, result.payoutsByEntryId["ole"]!!, DELTA)
        assertEquals(7.5, result.payoutsByEntryId["mamun"]!!, DELTA)
        assertEquals(0.0, result.carryForwardJackpot, DELTA)
        assertEquals(0.0, result.localReturnedAmount, DELTA)
    }

    @Test
    fun scenario4_localWinnerOnly() {
        val result = JackpotV2Calculator.calculate(
            JackpotV2RoundInput(
                incomingJackpot = 30.0,
                entries = listOf(
                    jackpotEntry("ole", correct = false),
                    jackpotEntry("thomas", correct = false),
                    localEntry("mamun", correct = true)
                )
            )
        )

        assertEquals(15.0, result.currentPot, DELTA)
        assertEquals(15.0, result.payoutsByEntryId["mamun"]!!, DELTA)
        assertEquals(30.0, result.carryForwardJackpot, DELTA)
        assertEquals(0.0, result.localReturnedAmount, DELTA)
    }

    @Test
    fun scenario5_jackpotWinnerOnly() {
        val result = JackpotV2Calculator.calculate(
            JackpotV2RoundInput(
                incomingJackpot = 30.0,
                entries = listOf(
                    jackpotEntry("ole", correct = true),
                    jackpotEntry("thomas", correct = false),
                    localEntry("mamun", correct = false)
                )
            )
        )

        assertEquals(15.0, result.currentPot, DELTA)
        assertEquals(45.0, result.payoutsByEntryId["ole"]!!, DELTA)
        assertEquals(0.0, result.carryForwardJackpot, DELTA)
    }

    @Test
    fun scenario6_mixedNoWinner() {
        val result = JackpotV2Calculator.calculate(
            JackpotV2RoundInput(
                incomingJackpot = 30.0,
                entries = listOf(
                    jackpotEntry("ole"),
                    jackpotEntry("thomas"),
                    localEntry("mamun")
                )
            )
        )

        assertEquals(15.0, result.currentPot, DELTA)
        assertEquals(40.0, result.carryForwardJackpot, DELTA)
        assertEquals(5.0, result.localReturnedAmount, DELTA)
        assertEquals(0, result.payoutsByEntryId.size)
    }
}
