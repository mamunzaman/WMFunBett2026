package com.example.wmfunbett2026.data.sample

import com.example.wmfunbett2026.data.model.Day
import com.example.wmfunbett2026.data.model.Entry
import com.example.wmfunbett2026.data.model.Game
import com.example.wmfunbett2026.data.model.Round
import com.example.wmfunbett2026.data.model.TippGroup

object SampleData {
    const val ROUND_ID = "round-1"
    const val DAY_ID = "day-1"
    const val GAME_ID = "game-1"
    const val TIPP_GROUP_ID = "tipp-1"

    private val round = Round(id = ROUND_ID, name = "Bundesliga Round 1")
    private val day = Day(id = DAY_ID, roundId = ROUND_ID, name = "Saturday")
    private val game = Game(id = GAME_ID, dayId = DAY_ID, homeTeam = "Germany", awayTeam = "France")
    private val tippGroup = TippGroup(id = TIPP_GROUP_ID, gameId = GAME_ID, name = "Correct Score")
    private val entries = listOf(
        Entry(id = "entry-1", tippGroupId = TIPP_GROUP_ID, playerName = "Alex", prediction = "2:1", stake = "€10"),
        Entry(id = "entry-2", tippGroupId = TIPP_GROUP_ID, playerName = "John", prediction = "1:1", stake = "€5")
    )

    fun getRounds(): List<Round> = listOf(round)

    fun getRound(roundId: String): Round? = getRounds().find { it.id == roundId }

    fun getDays(roundId: String): List<Day> =
        if (roundId == ROUND_ID) listOf(day) else emptyList()

    fun getDay(dayId: String): Day? = getDays(ROUND_ID).find { it.id == dayId }

    fun getGames(dayId: String): List<Game> =
        if (dayId == DAY_ID) listOf(game) else emptyList()

    fun getGame(gameId: String): Game? = getGames(DAY_ID).find { it.id == gameId }

    fun getTippGroups(gameId: String): List<TippGroup> =
        if (gameId == GAME_ID) listOf(tippGroup) else emptyList()

    fun getTippGroup(tippGroupId: String): TippGroup? =
        getTippGroups(GAME_ID).find { it.id == tippGroupId }

    fun getEntries(tippGroupId: String): List<Entry> =
        if (tippGroupId == TIPP_GROUP_ID) entries else emptyList()

    fun getEntry(entryId: String): Entry? = getEntries(TIPP_GROUP_ID).find { it.id == entryId }
}
