package com.example.wmfunbett2026.data.jackpot

import com.example.wmfunbett2026.data.model.Day
import com.example.wmfunbett2026.data.model.Entry
import com.example.wmfunbett2026.data.model.EntryParticipation
import com.example.wmfunbett2026.data.model.Game
import com.example.wmfunbett2026.data.model.MatchStatus
import com.example.wmfunbett2026.data.model.Round
import com.example.wmfunbett2026.data.model.TimeScope
import com.example.wmfunbett2026.data.model.TippGroup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class JackpotV2SettlementBuilderTest {

    private companion object {
        const val DELTA = 0.001
    }

    @Test
    fun notFinishedGame_waitingResult_noCalculation() {
        val round = sampleRound(
            finishedGame = null,
            currentGame = sampleGame(
                id = "game-2",
                status = MatchStatus.NOT_STARTED,
                scoreA = null,
                scoreB = null
            )
        )
        val game = round.days.first().games.last()
        val group = game.tippGroups.first()

        val settlement = JackpotV2SettlementBuilder.settle(round, game, group)

        assertEquals(TippGroupV2SettlementPhase.WAITING_RESULT, settlement.phase)
        assertNull(settlement.calculation)
    }

    @Test
    fun liveGame_waitingResult_noCalculation() {
        val game = sampleGame(
            id = "game-live",
            status = MatchStatus.LIVE,
            scoreA = 1,
            scoreB = 0
        )
        val group = game.tippGroups.first()
        val round = Round(id = "round-1", name = "R", days = listOf(Day("day-1", "D", listOf(game))))

        val settlement = JackpotV2SettlementBuilder.settle(round, game, group)

        assertEquals(TippGroupV2SettlementPhase.WAITING_RESULT, settlement.phase)
        assertNull(settlement.calculation)
    }

    @Test
    fun incomingJackpot_accumulatesAcrossNoWinnerRounds() {
        val finished = sampleGame(
            id = "game-1",
            status = MatchStatus.FINISHED,
            scoreA = 0,
            scoreB = 0,
            entries = listOf(
                entry("e1", "Ole", EntryParticipation.JACKPOT, "1-0"),
                entry("e2", "Thomas", EntryParticipation.JACKPOT, "2-0")
            )
        )
        val current = sampleGame(
            id = "game-2",
            status = MatchStatus.NOT_STARTED,
            scoreA = null,
            scoreB = null,
            entries = listOf(
                entry("e3", "Ole", EntryParticipation.JACKPOT, "1-0")
            )
        )
        val round = Round(
            id = "round-1",
            name = "R",
            days = listOf(Day("day-1", "D", listOf(finished, current)))
        )
        val group = current.tippGroups.first()

        val incoming = JackpotV2SettlementBuilder.incomingJackpot(round, current, group)

        assertEquals(10.0, incoming, DELTA)
    }

    private fun sampleRound(finishedGame: Game?, currentGame: Game): Round {
        val games = buildList {
            finishedGame?.let { add(it) }
            add(currentGame)
        }
        return Round(
            id = "round-1",
            name = "R",
            days = listOf(Day("day-1", "D", games))
        )
    }

    private fun sampleGame(
        id: String,
        status: MatchStatus,
        scoreA: Int?,
        scoreB: Int?,
        entries: List<Entry> = listOf(
            entry("e1", "Ole", EntryParticipation.JACKPOT, "1-0")
        )
    ): Game = Game(
        id = id,
        teamA = "Germany",
        teamB = "France",
        dateTimeLabel = "Sat 21 Jun · 20:00",
        teamAScore = scoreA,
        teamBScore = scoreB,
        status = status,
        tippGroups = listOf(
            TippGroup(
                id = "tipp-$id",
                title = "Full Time",
                timeScope = TimeScope.FULL_TIME,
                entries = entries,
                entryAmount = 5.0
            )
        )
    )

    private fun entry(
        id: String,
        name: String,
        participation: EntryParticipation,
        prediction: String
    ) = Entry(
        id = id,
        friendId = "friend-$id",
        friendName = name,
        prediction = prediction,
        amount = 5.0,
        currentRoundAmount = 5.0,
        participation = participation
    )
}
