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

class JackpotCatchUpPersonTest {

    private companion object {
        const val DELTA = 0.001
        const val THOMAS = "friend-thomas"
        const val NEW_PERSON = "friend-new"
        const val OTHER = "friend-other"
    }

    @Test
    fun newPerson_missedAllThreePreviousJackpotGroups_catchUp30() {
        val prior = finishedGame(
            id = "game-1",
            score = "0-0",
            groups = listOf(
                jackpotGroup("tipp-ht", TimeScope.SECOND_HALF, 10.0, OTHER, "e1", "1-0"),
                jackpotGroup("tipp-ft", TimeScope.FULL_TIME, 10.0, OTHER, "e2", "1-0"),
                jackpotGroup("tipp-score", TimeScope.FULL_TIME_PENALTIES, 10.0, OTHER, "e3", "1-0")
            )
        )
        val current = currentGame(entryAmount = 10.0)
        val round = roundOf(prior, current)

        val catchUp = catchUpFor(round, current, NEW_PERSON)

        assertEquals(30.0, catchUp.amount, DELTA)
        assertEquals(40.0, totalDue(10.0, catchUp.amount), DELTA)
    }

    @Test
    fun thomas_joinedTwoOfThreePrevious_catchUp10() {
        val prior = finishedGame(
            id = "game-1",
            score = "0-0",
            groups = listOf(
                groupWithEntries(
                    "tipp-ht",
                    TimeScope.SECOND_HALF,
                    10.0,
                    listOf(jackpotEntry("e1", THOMAS, "Thomas", "1-0", 10.0))
                ),
                groupWithEntries(
                    "tipp-ft",
                    TimeScope.FULL_TIME,
                    10.0,
                    listOf(jackpotEntry("e2", THOMAS, "Thomas", "1-0", 10.0))
                ),
                jackpotGroup("tipp-score", TimeScope.FULL_TIME_PENALTIES, 10.0, OTHER, "e3", "1-0")
            )
        )
        val current = currentGame(entryAmount = 10.0)
        val round = roundOf(prior, current)

        val catchUp = catchUpFor(round, current, THOMAS)

        assertEquals(10.0, catchUp.amount, DELTA)
        assertEquals(20.0, totalDue(10.0, catchUp.amount), DELTA)
    }

    @Test
    fun thomas_joinedAllPreviousJackpotGroups_catchUpZero() {
        val prior = finishedGame(
            id = "game-1",
            score = "0-0",
            groups = listOf(
                jackpotGroup("tipp-ht", TimeScope.SECOND_HALF, 10.0, THOMAS, "e1", "1-0"),
                jackpotGroup("tipp-ft", TimeScope.FULL_TIME, 10.0, THOMAS, "e2", "1-0"),
                jackpotGroup("tipp-score", TimeScope.FULL_TIME_PENALTIES, 10.0, THOMAS, "e3", "1-0")
            )
        )
        val current = currentGame(entryAmount = 10.0)
        val round = roundOf(prior, current)

        val catchUp = catchUpFor(round, current, THOMAS)

        assertEquals(0.0, catchUp.amount, DELTA)
        assertEquals(10.0, totalDue(10.0, catchUp.amount), DELTA)
    }

    @Test
    fun thomas_localOnlyInPreviousJackpotGroup_stillCountsAsMissed() {
        val prior = finishedGame(
            id = "game-1",
            score = "0-0",
            groups = listOf(
                groupWithEntries(
                    "tipp-mixed",
                    TimeScope.FULL_TIME,
                    10.0,
                    listOf(
                        localEntry("e1", THOMAS, "Thomas", "0-0", 10.0),
                        jackpotEntry("e2", OTHER, "Other", "1-0", 10.0)
                    )
                )
            )
        )
        val current = currentGame(entryAmount = 10.0)
        val round = roundOf(prior, current)

        val catchUp = catchUpFor(round, current, THOMAS)

        assertEquals(10.0, catchUp.amount, DELTA)
    }

    @Test
    fun otherPersonPaidPreviousGroup_doesNotReduceThomasCatchUp() {
        val prior = finishedGame(
            id = "game-1",
            score = "0-0",
            groups = listOf(
                jackpotGroup("tipp-ft", TimeScope.FULL_TIME, 10.0, OTHER, "e1", "1-0")
            )
        )
        val current = currentGame(entryAmount = 10.0)
        val round = roundOf(prior, current)

        val catchUp = catchUpFor(round, current, THOMAS)

        assertEquals(10.0, catchUp.amount, DELTA)
    }

    @Test
    fun settledPreviousJackpotGroup_doesNotCount() {
        val prior = finishedGame(
            id = "game-1",
            score = "1-0",
            groups = listOf(jackpotGroup("tipp-ft", TimeScope.FULL_TIME, 10.0, OTHER, "e1", "1-0"))
        )
        val current = currentGame(entryAmount = 10.0)
        val round = roundOf(prior, current)

        val catchUp = catchUpFor(round, current, NEW_PERSON)

        assertEquals(0.0, catchUp.amount, DELTA)
    }

    @Test
    fun currentGameTippGroups_notCountedInCatchUp() {
        val prior = finishedGame(
            id = "game-1",
            score = "0-0",
            groups = listOf(jackpotGroup("tipp-prior", TimeScope.FULL_TIME, 10.0, OTHER, "e1", "1-0"))
        )
        val current = Game(
            id = "game-2",
            teamA = "Germany",
            teamB = "France",
            dateTimeLabel = "Sun 22 Jun · 20:00",
            status = MatchStatus.NOT_STARTED,
            tippGroups = listOf(
                TippGroup(
                    id = "tipp-current",
                    title = "Full Time",
                    timeScope = TimeScope.FULL_TIME,
                    entries = listOf(jackpotEntry("e-c", THOMAS, "Thomas", "1-0", 10.0)),
                    entryAmount = 10.0
                )
            )
        )
        val round = roundOf(prior, current)

        val catchUp = catchUpFor(round, current, THOMAS)

        assertEquals(10.0, catchUp.amount, DELTA)
        assertEquals(1, catchUp.missedRoundSlots)
    }

    @Test
    fun noFriendSelected_catchUpZero() {
        val prior = finishedGame(
            id = "game-1",
            score = "0-0",
            groups = listOf(jackpotGroup("tipp-ft", TimeScope.FULL_TIME, 10.0, OTHER, "e1", "1-0"))
        )
        val current = currentGame(entryAmount = 10.0)
        val round = roundOf(prior, current)

        val catchUp = catchUpFor(round, current, friendId = null)

        assertEquals(0.0, catchUp.amount, DELTA)
    }

    private fun catchUpFor(round: Round, game: Game, friendId: String?) =
        JackpotV2SettlementBuilder.calculateCatchUp(
            round,
            game,
            game.tippGroups.first(),
            friendId
        )

    private fun totalDue(current: Double, catchUp: Double) = current + catchUp

    private fun roundOf(vararg games: Game): Round =
        Round(id = "round-1", name = "R", days = listOf(Day("day-1", "D", games.toList())))

    private fun currentGame(entryAmount: Double) = Game(
        id = "game-current",
        teamA = "Germany",
        teamB = "France",
        dateTimeLabel = "Sun 22 Jun · 20:00",
        status = MatchStatus.NOT_STARTED,
        tippGroups = listOf(
            TippGroup(
                id = "tipp-current",
                title = "Full Time",
                timeScope = TimeScope.FULL_TIME,
                entries = emptyList(),
                entryAmount = entryAmount
            )
        )
    )

    private fun finishedGame(id: String, score: String, groups: List<TippGroup>): Game {
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
        friendId: String,
        entryId: String,
        prediction: String
    ) = groupWithEntries(
        id,
        scope,
        entryAmount,
        listOf(jackpotEntry(entryId, friendId, "Player", prediction, entryAmount))
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

    private fun jackpotEntry(
        id: String,
        friendId: String,
        name: String,
        prediction: String,
        amount: Double
    ) = Entry(
        id = id,
        friendId = friendId,
        friendName = name,
        prediction = prediction,
        amount = amount,
        currentRoundAmount = amount,
        participation = EntryParticipation.JACKPOT
    )

    private fun localEntry(
        id: String,
        friendId: String,
        name: String,
        prediction: String,
        amount: Double
    ) = Entry(
        id = id,
        friendId = friendId,
        friendName = name,
        prediction = prediction,
        amount = amount,
        currentRoundAmount = amount,
        participation = EntryParticipation.LOCAL_ONLY
    )
}
