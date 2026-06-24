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
import org.junit.Test

class JackpotCatchUpTest {

    private companion object {
        const val DELTA = 0.001
        const val NEW_PERSON = "friend-new"
        const val OTHER_PERSON = "friend-other"
    }

    @Test
    fun noPreviousJackpot_catchUpZero() {
        val game = currentGame(entryAmount = 10.0)
        val round = roundOf(game)

        val catchUp = catchUpFor(round, game)

        assertEquals(0.0, catchUp.amount, DELTA)
        assertEquals(0, catchUp.missedRoundSlots)
    }

    @Test
    fun onePreviousJackpotTipp10NoWinner_catchUp10() {
        val prior = finishedGame(
            id = "game-1",
            score = "0-0",
            groups = listOf(jackpotGroup("tipp-ft", TimeScope.FULL_TIME, 10.0, "e1", OTHER_PERSON, "1-0"))
        )
        val current = currentGame(id = "game-2", entryAmount = 10.0)
        val round = roundOf(prior, current)

        val catchUp = catchUpFor(round, current)

        assertEquals(10.0, catchUp.amount, DELTA)
        assertEquals(1, catchUp.missedRoundSlots)
    }

    @Test
    fun previousGameTwoJackpotTipps10And10_catchUp20() {
        val prior = finishedGame(
            id = "game-1",
            score = "0-0",
            groups = listOf(
                jackpotGroup("tipp-ft", TimeScope.FULL_TIME, 10.0, "e1", OTHER_PERSON, "1-0"),
                jackpotGroup("tipp-ht", TimeScope.SECOND_HALF, 10.0, "e2", OTHER_PERSON, "1-0")
            )
        )
        val current = currentGame(id = "game-2", entryAmount = 10.0)
        val round = roundOf(prior, current)

        val catchUp = catchUpFor(round, current)

        assertEquals(20.0, catchUp.amount, DELTA)
        assertEquals(2, catchUp.missedRoundSlots)
    }

    @Test
    fun previousGameTwoJackpotTipps10And5_catchUp15() {
        val prior = finishedGame(
            id = "game-1",
            score = "0-0",
            groups = listOf(
                jackpotGroup("tipp-ft", TimeScope.FULL_TIME, 10.0, "e1", OTHER_PERSON, "1-0"),
                jackpotGroup("tipp-ht", TimeScope.SECOND_HALF, 5.0, "e2", OTHER_PERSON, "1-0")
            )
        )
        val current = currentGame(id = "game-2", entryAmount = 10.0)
        val round = roundOf(prior, current)

        val catchUp = catchUpFor(round, current)

        assertEquals(15.0, catchUp.amount, DELTA)
        assertEquals(2, catchUp.missedRoundSlots)
    }

    @Test
    fun previousGameLocalAndJackpotTipps_catchUp10Only() {
        val prior = finishedGame(
            id = "game-1",
            score = "0-0",
            groups = listOf(
                localGroup("tipp-local", TimeScope.FULL_TIME, 10.0, "e1", OTHER_PERSON, "0-0"),
                jackpotGroup("tipp-jp", TimeScope.SECOND_HALF, 10.0, "e2", OTHER_PERSON, "1-0")
            )
        )
        val current = currentGame(id = "game-2", entryAmount = 10.0)
        val round = roundOf(prior, current)

        val catchUp = catchUpFor(round, current)

        assertEquals(10.0, catchUp.amount, DELTA)
        assertEquals(1, catchUp.missedRoundSlots)
    }

    @Test
    fun previousJackpotWinnerSettled_catchUpZero() {
        val prior = finishedGame(
            id = "game-1",
            score = "1-0",
            groups = listOf(jackpotGroup("tipp-ft", TimeScope.FULL_TIME, 10.0, "e1", OTHER_PERSON, "1-0"))
        )
        val current = currentGame(id = "game-2", entryAmount = 10.0)
        val round = roundOf(prior, current)

        val catchUp = catchUpFor(round, current)

        assertEquals(0.0, catchUp.amount, DELTA)
    }

    @Test
    fun twoPreviousGamesEachJackpotTipp10_catchUp20() {
        val game1 = finishedGame(
            id = "game-1",
            score = "0-0",
            groups = listOf(jackpotGroup("tipp-g1", TimeScope.FULL_TIME, 10.0, "e1", OTHER_PERSON, "1-0"))
        )
        val game2 = finishedGame(
            id = "game-2",
            score = "0-0",
            groups = listOf(jackpotGroup("tipp-g2", TimeScope.FULL_TIME, 10.0, "e2", OTHER_PERSON, "1-0"))
        )
        val current = currentGame(id = "game-3", entryAmount = 10.0)
        val round = roundOf(game1, game2, current)

        val catchUp = catchUpFor(round, current)

        assertEquals(20.0, catchUp.amount, DELTA)
        assertEquals(2, catchUp.missedRoundSlots)
    }

    @Test
    fun currentGameTippGroups_notCountedInCatchUp() {
        val prior = finishedGame(
            id = "game-1",
            score = "0-0",
            groups = listOf(jackpotGroup("tipp-prior", TimeScope.FULL_TIME, 10.0, "e1", OTHER_PERSON, "1-0"))
        )
        val current = Game(
            id = "game-2",
            teamA = "Germany",
            teamB = "France",
            dateTimeLabel = "Sun 22 Jun · 20:00",
            status = MatchStatus.NOT_STARTED,
            tippGroups = listOf(
                TippGroup(
                    id = "tipp-current-ft",
                    title = "Full Time",
                    timeScope = TimeScope.FULL_TIME,
                    entries = emptyList(),
                    entryAmount = 10.0
                ),
                TippGroup(
                    id = "tipp-current-ht",
                    title = "Half Time",
                    timeScope = TimeScope.SECOND_HALF,
                    entries = listOf(entry("e-c", OTHER_PERSON, EntryParticipation.JACKPOT, "1-0", 10.0)),
                    entryAmount = 10.0
                )
            )
        )
        val round = roundOf(prior, current)

        val catchUp = catchUpFor(round, current)

        assertEquals(10.0, catchUp.amount, DELTA)
        assertEquals(1, catchUp.missedRoundSlots)
    }

    @Test
    fun localOnlyParticipation_totalIsCurrentAmountOnly() {
        val prior = finishedGame(
            id = "game-1",
            score = "0-0",
            groups = listOf(jackpotGroup("tipp-ft", TimeScope.FULL_TIME, 10.0, "e1", OTHER_PERSON, "1-0"))
        )
        val current = currentGame(id = "game-2", entryAmount = 10.0)
        val round = roundOf(prior, current)
        val catchUp = catchUpFor(round, current)

        val breakdown = JackpotChainCalculator.buildParticipationEntryJoinBreakdown(
            participation = EntryParticipation.LOCAL_ONLY,
            catchUpAmount = catchUp.amount,
            catchUpSlots = catchUp.missedRoundSlots,
            entryAmount = 10.0
        )

        assertEquals(0.0, breakdown.catchUpAmount, DELTA)
        assertEquals(10.0, breakdown.totalDue, DELTA)
    }

    @Test
    fun jackpotParticipation_totalIncludesCatchUp() {
        val prior = finishedGame(
            id = "game-1",
            score = "0-0",
            groups = listOf(
                jackpotGroup("tipp-ft", TimeScope.FULL_TIME, 10.0, "e1", OTHER_PERSON, "1-0"),
                jackpotGroup("tipp-ht", TimeScope.SECOND_HALF, 10.0, "e2", OTHER_PERSON, "1-0")
            )
        )
        val current = currentGame(id = "game-2", entryAmount = 10.0)
        val round = roundOf(prior, current)
        val catchUp = catchUpFor(round, current)

        val breakdown = JackpotChainCalculator.buildParticipationEntryJoinBreakdown(
            participation = EntryParticipation.JACKPOT,
            catchUpAmount = catchUp.amount,
            catchUpSlots = catchUp.missedRoundSlots,
            entryAmount = 10.0
        )

        assertEquals(20.0, breakdown.catchUpAmount, DELTA)
        assertEquals(10.0, breakdown.currentRoundEntryAmount, DELTA)
        assertEquals(30.0, breakdown.totalDue, DELTA)
    }

    private fun catchUpFor(round: Round, game: Game, friendId: String = NEW_PERSON) =
        JackpotV2SettlementBuilder.calculateCatchUp(
            round,
            game,
            game.tippGroups.first(),
            friendId
        )

    private fun roundOf(vararg games: Game): Round =
        Round(id = "round-1", name = "R", days = listOf(Day("day-1", "D", games.toList())))

    private fun currentGame(
        id: String = "game-current",
        entryAmount: Double
    ): Game = Game(
        id = id,
        teamA = "Germany",
        teamB = "France",
        dateTimeLabel = "Sun 22 Jun · 20:00",
        status = MatchStatus.NOT_STARTED,
        tippGroups = listOf(
            TippGroup(
                id = "tipp-$id",
                title = "Full Time",
                timeScope = TimeScope.FULL_TIME,
                entries = emptyList(),
                entryAmount = entryAmount
            )
        )
    )

    private fun finishedGame(
        id: String,
        score: String,
        groups: List<TippGroup>
    ): Game {
        val parts = score.split("-")
        return Game(
            id = id,
            teamA = "Germany",
            teamB = "France",
            dateTimeLabel = "Sat 21 Jun · 20:00",
            teamAScore = parts[0].toInt(),
            teamBScore = parts[1].toInt(),
            status = MatchStatus.FINISHED,
            tippGroups = groups
        )
    }

    private fun jackpotGroup(
        id: String,
        scope: TimeScope,
        entryAmount: Double,
        entryId: String,
        friendId: String,
        prediction: String
    ) = groupWithEntries(
        id,
        scope,
        entryAmount,
        listOf(entry(entryId, friendId, EntryParticipation.JACKPOT, prediction, entryAmount))
    )

    private fun localGroup(
        id: String,
        scope: TimeScope,
        entryAmount: Double,
        entryId: String,
        friendId: String,
        prediction: String
    ) = groupWithEntries(
        id,
        scope,
        entryAmount,
        listOf(entry(entryId, friendId, EntryParticipation.LOCAL_ONLY, prediction, entryAmount))
    )

    private fun groupWithEntries(
        id: String,
        scope: TimeScope,
        entryAmount: Double,
        entries: List<Entry>
    ) = TippGroup(
        id = id,
        title = scope.label,
        timeScope = scope,
        entries = entries,
        entryAmount = entryAmount
    )

    private fun entry(
        id: String,
        friendId: String,
        participation: EntryParticipation,
        prediction: String,
        amount: Double
    ) = Entry(
        id = id,
        friendId = friendId,
        friendName = "Player",
        prediction = prediction,
        amount = amount,
        currentRoundAmount = amount,
        participation = participation
    )
}
