package com.example.wmfunbett2026.data.repository

import androidx.compose.runtime.mutableIntStateOf
import com.example.wmfunbett2026.data.model.Day
import com.example.wmfunbett2026.data.model.Entry
import com.example.wmfunbett2026.data.model.Game
import com.example.wmfunbett2026.data.model.MatchStatus
import com.example.wmfunbett2026.data.model.MatchTippType
import com.example.wmfunbett2026.data.model.Round
import com.example.wmfunbett2026.data.model.TimeScope
import com.example.wmfunbett2026.data.model.TippGroup
import com.example.wmfunbett2026.data.tipp.TippScopeAvailability
import java.time.LocalDate

object FunBettRepository {

    const val ROUND_ID = "round-1"
    const val DAY_ID = "day-1"
    const val GAME_ID = "game-1"
    const val GAME_ID_2 = "game-2"
    const val TIPP_GROUP_ID = "tipp-1"

    val dataVersion = mutableIntStateOf(0)

    private var roundsInternal: List<Round> = listOf(buildSampleRound())
    private val sampleLeagueRoundIds = mutableMapOf<String, String>()

    fun getSampleLeagueRoundId(leagueId: String): String? = sampleLeagueRoundIds[leagueId]

    fun resolveRoundIdForLeague(leagueId: String, leagueName: String): String? {
        when (leagueId) {
            "wc2026" -> return ROUND_ID
            "custom-league" -> return null
        }
        if (getRound(leagueId) != null) return leagueId
        sampleLeagueRoundIds[leagueId]?.let { return it }
        if (leagueId.startsWith("round-")) return leagueId.takeIf { getRound(it) != null }

        val roundId = "sample-$leagueId"
        if (getRound(roundId) == null) {
            roundsInternal = roundsInternal + Round(
                id = roundId,
                name = leagueName,
                days = emptyList()
            )
            sampleLeagueRoundIds[leagueId] = roundId
            notifyChanged()
        }
        return roundId
    }

    fun getRounds(): List<Round> = roundsInternal.toList()

    fun getRound(roundId: String): Round? =
        if (roundId.isBlank()) null else roundsInternal.find { it.id == roundId }

    fun getDays(roundId: String): List<Day> =
        getRound(roundId)?.days?.toList().orEmpty()

    fun getDay(dayId: String): Day? =
        if (dayId.isBlank()) null else roundsInternal.flatMap { it.days }.find { it.id == dayId }

    fun getGames(dayId: String): List<Game> =
        getDay(dayId)?.games?.toList().orEmpty()

    fun getGame(gameId: String): Game? =
        if (gameId.isBlank()) null else getAllGames().find { it.id == gameId }

    fun getGameInDay(dayId: String, gameId: String): Game? =
        if (dayId.isBlank() || gameId.isBlank()) null else getDay(dayId)?.games?.find { it.id == gameId }

    fun getTippGroups(gameId: String): List<TippGroup> =
        getGame(gameId)?.tippGroups?.toList().orEmpty()

    fun getTippGroup(tippGroupId: String): TippGroup? =
        if (tippGroupId.isBlank()) null else getAllGames().flatMap { it.tippGroups }.find { it.id == tippGroupId }

    fun getTippGroupInGame(gameId: String, tippGroupId: String): TippGroup? =
        if (gameId.isBlank() || tippGroupId.isBlank()) null else getGame(gameId)?.tippGroups?.find { it.id == tippGroupId }

    fun getEntries(tippGroupId: String): List<Entry> =
        getTippGroup(tippGroupId)?.entries?.toList().orEmpty()

    fun getTotalGameCount(): Int = getAllGames().size

    fun getTotalEntryCount(): Int =
        getAllGames().flatMap { it.tippGroups }.sumOf { it.entries.size }

    fun getTotalKassePreview(): Double =
        getAllGames().sumOf { it.totalKasse }

    fun isMatchDateAllowed(date: LocalDate, reference: LocalDate = LocalDate.now()): Boolean =
        !date.isBefore(reference)

    fun addRound(name: String, note: String?): Round {
        val round = Round(
            id = "round-${System.currentTimeMillis()}",
            name = name.trim(),
            note = note?.trim()?.takeIf { it.isNotEmpty() },
            days = emptyList()
        )
        roundsInternal = roundsInternal + round
        notifyChanged()
        return round
    }

    fun addGame(
        roundId: String,
        dayLabel: String,
        teamA: String,
        teamB: String,
        dateLabel: String?,
        timeLabel: String?,
        note: String? = null
    ): Game? {
        if (getRound(roundId) == null) return null

        val game = Game(
            id = "game-${System.currentTimeMillis()}",
            teamA = teamA.trim(),
            teamB = teamB.trim(),
            dateTimeLabel = buildDateTimeLabel(dateLabel, timeLabel),
            tippGroups = emptyList(),
            note = note?.trim()?.takeIf { it.isNotEmpty() }
        )
        val normalizedDayLabel = dayLabel.trim()

        roundsInternal = roundsInternal.map { round ->
            if (round.id != roundId) return@map round

            val existingDay = round.days.find { it.name.equals(normalizedDayLabel, ignoreCase = true) }
            if (existingDay != null) {
                round.copy(
                    days = round.days.map { day ->
                        if (day.id == existingDay.id) {
                            day.copy(games = day.games + game)
                        } else {
                            day
                        }
                    }
                )
            } else {
                val newDay = Day(
                    id = "day-${System.currentTimeMillis()}",
                    name = normalizedDayLabel,
                    games = listOf(game)
                )
                round.copy(days = round.days + newDay)
            }
        }
        notifyChanged()
        return game
    }

    fun addTippGroup(
        gameId: String,
        tippType: MatchTippType,
        entryAmount: Double,
        note: String? = null
    ): TippGroup? {
        val game = getGame(gameId) ?: return null
        if (entryAmount <= 0.0) return null
        if (!TippScopeAvailability.canCreateMenuTippType(game, tippType)) return null

        val tippGroup = TippGroup(
            id = "tipp-${System.currentTimeMillis()}-${tippType.name}",
            title = tippType.defaultTippTitle(),
            timeScope = tippType.toTimeScope(),
            entries = emptyList(),
            entryAmount = entryAmount,
            note = note?.trim()?.takeIf { it.isNotEmpty() }
        )

        roundsInternal = roundsInternal.map { round ->
            round.copy(
                days = round.days.map { day ->
                    day.copy(
                        games = day.games.map { gameItem ->
                            if (gameItem.id == gameId) {
                                gameItem.copy(tippGroups = gameItem.tippGroups + tippGroup)
                            } else {
                                gameItem
                            }
                        }
                    )
                }
            )
        }
        notifyChanged()
        return tippGroup
    }

    fun availableMenuTippTypes(gameId: String): List<MatchTippType> {
        val game = getGame(gameId) ?: return emptyList()
        return TippScopeAvailability.getAvailableMenuTippTypes(game)
    }

    fun addEntry(
        tippGroupId: String,
        name: String,
        prediction: String,
        totalPaid: Double,
        currentRoundAmount: Double,
        note: String?
    ): Entry? {
        if (getTippGroup(tippGroupId) == null) return null

        val newEntry = Entry(
            id = "entry-${System.currentTimeMillis()}",
            name = name.trim(),
            prediction = prediction.trim(),
            amount = totalPaid,
            currentRoundAmount = currentRoundAmount,
            note = note?.trim()?.takeIf { it.isNotEmpty() }
        )

        roundsInternal = roundsInternal.map { round ->
            round.copy(
                days = round.days.map { day ->
                    day.copy(
                        games = day.games.map { game ->
                            game.copy(
                                tippGroups = game.tippGroups.map { group ->
                                    if (group.id == tippGroupId) {
                                        group.copy(entries = group.entries + newEntry)
                                    } else {
                                        group
                                    }
                                }
                            )
                        }
                    )
                }
            )
        }
        notifyChanged()
        return newEntry
    }

    fun addEntryToTippGroup(
        tippGroupId: String,
        name: String,
        prediction: String,
        amount: Double,
        note: String?
    ): Entry? {
        val trimmedName = name.trim()
        val trimmedPrediction = prediction.trim()
        if (trimmedName.isEmpty() || trimmedPrediction.isEmpty() || amount <= 0.0) return null
        return addEntry(
            tippGroupId = tippGroupId,
            name = trimmedName,
            prediction = trimmedPrediction,
            totalPaid = amount,
            currentRoundAmount = amount,
            note = note
        )
    }

    fun updateGameResult(
        gameId: String,
        teamAScore: Int?,
        teamBScore: Int?,
        status: MatchStatus
    ): Boolean {
        if (gameId.isBlank() || getGame(gameId) == null) return false

        roundsInternal = roundsInternal.map { round ->
            round.copy(
                days = round.days.map { day ->
                    day.copy(
                        games = day.games.map { game ->
                            if (game.id != gameId) return@map game
                            game.copy(
                                teamAScore = teamAScore,
                                teamBScore = teamBScore,
                                status = status
                            )
                        }
                    )
                }
            )
        }
        notifyChanged()
        return true
    }

    fun deleteRound(roundId: String): Boolean {
        if (roundId.isBlank()) return false
        val before = roundsInternal.size
        roundsInternal = roundsInternal.filterNot { it.id == roundId }
        if (roundsInternal.size == before) return false
        notifyChanged()
        return true
    }

    fun deleteGame(gameId: String): Boolean {
        if (gameId.isBlank()) return false
        var removed = false
        roundsInternal = roundsInternal.map { round ->
            round.copy(
                days = round.days.mapNotNull { day ->
                    val games = day.games.filterNot { it.id == gameId }
                    if (games.size != day.games.size) removed = true
                    if (games.isEmpty()) null else day.copy(games = games)
                }
            )
        }
        if (!removed) return false
        notifyChanged()
        return true
    }

    fun deleteTippGroup(tippGroupId: String): Boolean {
        if (tippGroupId.isBlank()) return false
        var removed = false
        roundsInternal = roundsInternal.map { round ->
            round.copy(
                days = round.days.map { day ->
                    day.copy(
                        games = day.games.map { game ->
                            val groups = game.tippGroups.filterNot { it.id == tippGroupId }
                            if (groups.size != game.tippGroups.size) removed = true
                            game.copy(tippGroups = groups)
                        }
                    )
                }
            )
        }
        if (!removed) return false
        notifyChanged()
        return true
    }

    fun deleteEntry(tippGroupId: String, entryId: String): Boolean {
        if (tippGroupId.isBlank() || entryId.isBlank()) return false
        var removed = false
        roundsInternal = roundsInternal.map { round ->
            round.copy(
                days = round.days.map { day ->
                    day.copy(
                        games = day.games.map { game ->
                            game.copy(
                                tippGroups = game.tippGroups.map { group ->
                                    if (group.id != tippGroupId) return@map group
                                    val entries = group.entries.filterNot { it.id == entryId }
                                    if (entries.size != group.entries.size) removed = true
                                    group.copy(entries = entries)
                                }
                            )
                        }
                    )
                }
            )
        }
        if (!removed) return false
        notifyChanged()
        return true
    }

    private fun buildDateTimeLabel(dateLabel: String?, timeLabel: String?): String {
        val date = dateLabel?.trim()?.takeIf { it.isNotEmpty() }
        val time = timeLabel?.trim()?.takeIf { it.isNotEmpty() }
        return when {
            date != null && time != null -> "$date · $time"
            date != null -> date
            time != null -> time
            else -> "TBD"
        }
    }

    private fun notifyChanged() {
        dataVersion.intValue++
    }

    private fun getAllGames(): List<Game> =
        roundsInternal.flatMap { it.days }.flatMap { it.games }

    private fun buildSampleRound(): Round {
        val correctScoreGroup = TippGroup(
            id = TIPP_GROUP_ID,
            title = "Correct Score",
            timeScope = TimeScope.FULL_TIME,
            entries = listOf(
                Entry(
                    id = "entry-1",
                    name = "Alex",
                    prediction = "2:1",
                    amount = 10.0,
                    currentRoundAmount = 10.0,
                    note = "Confident home win"
                ),
                Entry(
                    id = "entry-2",
                    name = "John",
                    prediction = "1:1",
                    amount = 10.0,
                    currentRoundAmount = 10.0
                )
            )
        )

        val game = Game(
            id = GAME_ID,
            teamA = "Germany",
            teamB = "France",
            dateTimeLabel = "Sat 21 Jun · 20:00",
            tippGroups = listOf(correctScoreGroup)
        )

        val brazilSpainGroup = TippGroup(
            id = "tipp-2",
            title = "Match Winner",
            timeScope = TimeScope.FULL_TIME,
            entries = emptyList()
        )

        val game2 = Game(
            id = GAME_ID_2,
            teamA = "Brazil",
            teamB = "Spain",
            dateTimeLabel = "Sat 21 Jun · 17:00",
            tippGroups = listOf(brazilSpainGroup)
        )

        val day = Day(
            id = DAY_ID,
            name = "Matchday 1",
            games = listOf(game, game2)
        )

        return Round(
            id = ROUND_ID,
            name = "World Cup Round",
            note = "Sample tournament",
            days = listOf(day)
        )
    }
}
