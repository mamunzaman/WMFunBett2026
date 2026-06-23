package com.example.wmfunbett2026.data.repository

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import com.example.wmfunbett2026.data.local.FunBettDatabase
import com.example.wmfunbett2026.data.local.FunBettLocalStore
import com.example.wmfunbett2026.data.model.Day
import com.example.wmfunbett2026.data.model.Entry
import com.example.wmfunbett2026.data.model.EntryParticipation
import com.example.wmfunbett2026.data.model.EntryPaymentSnapshot
import com.example.wmfunbett2026.data.model.EntryUpdateRequest
import com.example.wmfunbett2026.data.model.Friend
import com.example.wmfunbett2026.data.model.formatPersonFullName
import com.example.wmfunbett2026.data.model.FriendEntryHistoryItem
import com.example.wmfunbett2026.data.model.FriendFinancialSummary
import com.example.wmfunbett2026.data.model.FriendWithStats
import com.example.wmfunbett2026.data.model.Game
import com.example.wmfunbett2026.data.model.MatchStatus
import com.example.wmfunbett2026.data.model.MatchTippType
import com.example.wmfunbett2026.data.model.Round
import com.example.wmfunbett2026.data.model.TimeScope
import com.example.wmfunbett2026.data.model.TippGroup
import com.example.wmfunbett2026.data.model.TippGroupCreationBlockReason
import com.example.wmfunbett2026.data.model.TippGroupEntryBlockReason
import com.example.wmfunbett2026.data.model.TippGroupSettlementSummary
import com.example.wmfunbett2026.data.tipp.TippScopeAvailability
import com.example.wmfunbett2026.data.jackpot.JackpotCarryOverSummary
import com.example.wmfunbett2026.data.jackpot.JackpotChainCalculator
import com.example.wmfunbett2026.data.jackpot.ParticipationEntryJoinBreakdown
import com.example.wmfunbett2026.data.winner.TippGroupWinnerEngine
import java.time.LocalDate

object FunBettRepository {

    const val ROUND_ID = "round-1"
    const val DAY_ID = "day-1"
    const val GAME_ID = "game-1"
    const val GAME_ID_2 = "game-2"
    const val TIPP_GROUP_ID = "tipp-1"
    const val FRIEND_ID_ALEX = "friend-alex"
    const val FRIEND_ID_JOHN = "friend-john"

    val dataVersion = mutableIntStateOf(0)

    private var roundsInternal: List<Round> = emptyList()
    private var friendsInternal: List<Friend> = emptyList()
    private lateinit var localStore: FunBettLocalStore
    private var initialized = false
    private val sampleLeagueRoundIds = mutableMapOf<String, String>()

    fun initialize(context: Context) {
        if (initialized) return
        val database = FunBettDatabase.getInstance(context)
        localStore = FunBettLocalStore(
            leagueDao = database.leagueDao(),
            matchDao = database.matchDao(),
            tippGroupDao = database.tippGroupDao(),
            entryDao = database.entryDao(),
            friendDao = database.friendDao()
        )
        roundsInternal = if (localStore.isEmpty()) {
            val sampleFriends = buildSampleFriends()
            localStore.persistFriends(sampleFriends)
            friendsInternal = sampleFriends
            val sample = listOf(buildSampleRound())
            localStore.persistAll(sample)
            sample
        } else {
            friendsInternal = localStore.loadFriends()
            localStore.loadRounds()
        }
        initialized = true
    }

    fun getFriends(): List<Friend> = friendsInternal.toList()

    fun getFriendsWithStats(): List<FriendWithStats> =
        friendsInternal.mapNotNull { friend ->
            getFriendFinancialSummary(friend.id)?.let { summary ->
                FriendWithStats(
                    friend = summary.friend,
                    activeEntryCount = summary.activeEntryCount,
                    activeAmountTotal = summary.activeAmountTotal,
                    totalTipps = summary.activeEntryCount,
                    winCount = 0
                )
            }
        }.sortedBy { it.friend.name.lowercase() }

    fun getFriendEntries(friendId: String): List<FriendEntryHistoryItem> {
        if (friendId.isBlank()) return emptyList()

        val history = mutableListOf<FriendEntryHistoryItem>()
        roundsInternal.forEach { round ->
            round.days.forEach { day ->
                day.games.forEach { game ->
                    game.tippGroups.forEach { group ->
                        group.entries
                            .filter { it.friendId == friendId }
                            .forEach { entry ->
                                history.add(
                                    FriendEntryHistoryItem(
                                        entryId = entry.id,
                                        leagueName = round.name,
                                        matchName = game.displayName,
                                        tippGroupName = group.title,
                                        prediction = entry.prediction,
                                        amount = entry.amount,
                                        createdAtMs = parseEntrySortKey(entry.id)
                                    )
                                )
                            }
                    }
                }
            }
        }
        return history.sortedWith(
            compareByDescending<FriendEntryHistoryItem> { it.createdAtMs }
                .thenByDescending { it.entryId }
        )
    }

    fun getFriendFinancialSummary(friendId: String): FriendFinancialSummary? {
        val friend = getFriend(friendId) ?: return null
        val entries = getFriendEntries(friendId)
        return FriendFinancialSummary(
            friend = friend,
            activeEntryCount = entries.size,
            activeAmountTotal = entries.sumOf { it.amount }
        )
    }

    private fun parseEntrySortKey(entryId: String): Long =
        entryId.removePrefix("entry-").toLongOrNull() ?: 0L

    fun getFriendIdsInTippGroup(tippGroupId: String): Set<String> =
        getTippGroup(tippGroupId)
            ?.entries
            ?.map { it.friendId }
            ?.toSet()
            .orEmpty()

    fun isFriendInTippGroup(tippGroupId: String, friendId: String): Boolean =
        friendId.isNotBlank() && getFriendIdsInTippGroup(tippGroupId).contains(friendId)

    fun hasAvailableFriendsForTippGroup(tippGroupId: String): Boolean {
        if (tippGroupId.isBlank()) return false
        val friends = getFriends()
        if (friends.isEmpty()) return false
        val joined = getFriendIdsInTippGroup(tippGroupId)
        return friends.any { it.id !in joined }
    }

    fun getGameForTippGroup(tippGroupId: String): Game? {
        if (tippGroupId.isBlank()) return null
        return getAllGames().find { game -> game.tippGroups.any { it.id == tippGroupId } }
    }

    fun getTippGroupEntryBlockReason(tippGroupId: String): TippGroupEntryBlockReason? {
        if (tippGroupId.isBlank()) return null
        val game = getGameForTippGroup(tippGroupId) ?: return null
        return when {
            game.status == MatchStatus.LIVE -> TippGroupEntryBlockReason.MATCH_LIVE
            game.status == MatchStatus.FINISHED -> TippGroupEntryBlockReason.MATCH_FINISHED
            !hasAvailableFriendsForTippGroup(tippGroupId) ->
                TippGroupEntryBlockReason.ALL_FRIENDS_JOINED
            else -> null
        }
    }

    fun canAddEntryToTippGroup(tippGroupId: String): Boolean =
        getTippGroupEntryBlockReason(tippGroupId) == null

    fun getTippGroupCreationBlockReason(gameId: String): TippGroupCreationBlockReason? {
        if (gameId.isBlank()) return null
        val game = getGame(gameId) ?: return null
        return TippScopeAvailability.getTippGroupCreationBlockReason(game)
    }

    fun canCreateTippGroupForGame(gameId: String): Boolean {
        if (gameId.isBlank()) return false
        val game = getGame(gameId) ?: return false
        return TippScopeAvailability.canCreateTippGroupForGame(game)
    }

    fun allMenuTippTypesCreatedForGame(gameId: String): Boolean {
        if (gameId.isBlank()) return false
        val game = getGame(gameId) ?: return false
        return TippScopeAvailability.allMenuTippTypesCreated(game)
    }

    fun getFriend(friendId: String): Friend? =
        if (friendId.isBlank()) null else friendsInternal.find { it.id == friendId }

    fun friendNameExists(
        firstName: String,
        lastName: String = "",
        excludeFriendId: String? = null
    ): Boolean {
        val full = formatPersonFullName(firstName, lastName)
        if (full.isEmpty()) return false
        return friendsInternal.any {
            it.name.equals(full, ignoreCase = true) && it.id != excludeFriendId
        }
    }

    fun addFriend(firstName: String, lastName: String, note: String?): Friend? {
        val trimmedFirst = firstName.trim()
        val trimmedLast = lastName.trim()
        if (trimmedFirst.isEmpty() || friendNameExists(trimmedFirst, trimmedLast)) return null

        val friend = Friend(
            id = "friend-${System.currentTimeMillis()}",
            firstName = trimmedFirst,
            lastName = trimmedLast,
            note = note?.trim()?.takeIf { it.isNotEmpty() },
            createdAt = System.currentTimeMillis()
        )
        friendsInternal = friendsInternal + friend
        localStore.persistFriend(friend)
        notifyChanged()
        return friend
    }

    fun updateFriend(friendId: String, firstName: String, lastName: String, note: String?): Friend? {
        if (friendId.isBlank()) return null
        val trimmedFirst = firstName.trim()
        val trimmedLast = lastName.trim()
        if (trimmedFirst.isEmpty() || friendNameExists(trimmedFirst, trimmedLast, excludeFriendId = friendId)) {
            return null
        }

        val existing = getFriend(friendId) ?: return null
        val fullName = formatPersonFullName(trimmedFirst, trimmedLast)
        val updatedFriend = existing.copy(
            firstName = trimmedFirst,
            lastName = trimmedLast,
            note = note?.trim()?.takeIf { it.isNotEmpty() }
        )

        friendsInternal = friendsInternal.map { friend ->
            if (friend.id == friendId) updatedFriend else friend
        }

        roundsInternal = roundsInternal.map { round ->
            round.copy(
                days = round.days.map { day ->
                    day.copy(
                        games = day.games.map { game ->
                            game.copy(
                                tippGroups = game.tippGroups.map { group ->
                                    group.copy(
                                        entries = group.entries.map { entry ->
                                            if (entry.friendId == friendId) {
                                                entry.copy(friendName = fullName)
                                            } else {
                                                entry
                                            }
                                        }
                                    )
                                }
                            )
                        }
                    )
                }
            )
        }

        localStore.persistFriend(updatedFriend)
        roundsInternal.forEach { round ->
            round.days.forEach { day ->
                day.games.forEach { game ->
                    game.tippGroups.forEach { group ->
                        group.entries
                            .filter { it.friendId == friendId }
                            .forEach { entry -> localStore.persistEntry(group.id, entry) }
                    }
                }
            }
        }
        notifyChanged()
        return updatedFriend
    }

    fun deleteFriend(friendId: String): Boolean {
        if (friendId.isBlank() || getFriend(friendId) == null) return false
        friendsInternal = friendsInternal.filterNot { it.id == friendId }
        localStore.deleteFriend(friendId)
        notifyChanged()
        return true
    }

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
            val round = Round(
                id = roundId,
                name = leagueName,
                days = emptyList()
            )
            roundsInternal = roundsInternal + round
            localStore.persistRound(round)
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

    fun getTippGroupSettlementSummary(
        roundId: String,
        gameId: String,
        tippGroupId: String
    ): TippGroupSettlementSummary? {
        val game = getGame(gameId) ?: return null
        val tippGroup = getTippGroupInGame(gameId, tippGroupId) ?: return null
        val base = TippGroupWinnerEngine.settlementSummary(game, tippGroup)
        val round = getRound(roundId) ?: return base
        val jackpot = JackpotChainCalculator.calculateCarryOverSummary(round, game, tippGroup)
        return base.copy(sharePerWinner = jackpot.sharePerWinner)
    }

    fun getJackpotCarryOverSummary(
        roundId: String,
        gameId: String,
        tippGroupId: String
    ): JackpotCarryOverSummary? {
        val round = getRound(roundId) ?: return null
        val game = getGame(gameId) ?: return null
        val tippGroup = getTippGroupInGame(gameId, tippGroupId) ?: return null
        return JackpotChainCalculator.calculateCarryOverSummary(round, game, tippGroup)
    }

    fun getGameIncomingJackpotMax(roundId: String, gameId: String): Double {
        val round = getRound(roundId) ?: return 0.0
        val game = getGame(gameId) ?: return 0.0
        return JackpotChainCalculator.maxIncomingJackpotForGame(round, game)
    }

    fun getEntryJoinBreakdown(
        roundId: String,
        gameId: String,
        tippGroupId: String,
        participation: EntryParticipation
    ): ParticipationEntryJoinBreakdown? {
        if (roundId.isBlank() || gameId.isBlank() || tippGroupId.isBlank()) return null
        val round = getRound(roundId) ?: return null
        val game = getGame(gameId) ?: return null
        val tippGroup = getTippGroupInGame(gameId, tippGroupId) ?: return null
        val entryAmount = tippGroup.entryAmount ?: return null
        if (entryAmount <= 0.0) return null

        val catchUpSlots = when (participation) {
            EntryParticipation.LOCAL_ONLY -> 0
            EntryParticipation.JACKPOT ->
                JackpotChainCalculator.buildJackpotCatchUpContext(round, game, tippGroup).missedRoundSlots
        }

        return JackpotChainCalculator.buildParticipationEntryJoinBreakdown(
            participation = participation,
            catchUpSlots = catchUpSlots,
            entryAmount = entryAmount
        )
    }

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
        localStore.persistRound(round)
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
        var targetDay: Day? = null

        roundsInternal = roundsInternal.map { round ->
            if (round.id != roundId) return@map round

            val existingDay = round.days.find { it.name.equals(normalizedDayLabel, ignoreCase = true) }
            if (existingDay != null) {
                targetDay = existingDay
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
                targetDay = newDay
                round.copy(days = round.days + newDay)
            }
        }
        targetDay?.let { day -> localStore.persistGame(roundId, day, game) }
        notifyChanged()
        return game
    }

    fun updateGame(
        gameId: String,
        roundId: String,
        dayLabel: String,
        teamA: String,
        teamB: String,
        dateLabel: String?,
        timeLabel: String?,
        note: String? = null
    ): Game? {
        if (gameId.isBlank() || roundId.isBlank()) return null
        if (getRound(roundId) == null) return null
        val location = findGameLocation(gameId) ?: return null
        val (currentRoundId, _, currentGame) = location
        if (currentRoundId != roundId) return null

        val updatedGame = currentGame.copy(
            teamA = teamA.trim(),
            teamB = teamB.trim(),
            dateTimeLabel = buildDateTimeLabel(dateLabel, timeLabel),
            note = note?.trim()?.takeIf { it.isNotEmpty() }
        )
        val normalizedDayLabel = dayLabel.trim()
        if (normalizedDayLabel.isEmpty()) return null

        var persistDay: Day? = null
        roundsInternal = roundsInternal.map { round ->
            if (round.id != roundId) return@map round

            val cleanedDays = round.days.map { day ->
                day.copy(games = day.games.filterNot { it.id == gameId })
            }
            val existingDayIndex = cleanedDays.indexOfFirst { day ->
                day.name.equals(normalizedDayLabel, ignoreCase = true)
            }
            val updatedDays = if (existingDayIndex >= 0) {
                val existingDay = cleanedDays[existingDayIndex]
                persistDay = existingDay.copy(games = existingDay.games + updatedGame)
                cleanedDays.mapIndexed { index, day ->
                    if (index == existingDayIndex) persistDay!! else day
                }
            } else {
                val newDay = Day(
                    id = "day-${System.currentTimeMillis()}",
                    name = normalizedDayLabel,
                    games = listOf(updatedGame)
                )
                persistDay = newDay
                cleanedDays + newDay
            }

            round.copy(days = updatedDays.filter { it.games.isNotEmpty() })
        }

        val day = persistDay ?: return null
        localStore.updateGame(updatedGame, roundId, day)
        notifyChanged()
        return updatedGame
    }

    fun addTippGroup(
        gameId: String,
        tippType: MatchTippType,
        entryAmount: Double,
        note: String? = null
    ): TippGroup? {
        val game = getGame(gameId) ?: return null
        if (entryAmount <= 0.0) return null
        if (!TippScopeAvailability.canCreateTippGroupForGame(game)) return null
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
        localStore.persistTippGroup(gameId, tippGroup)
        notifyChanged()
        return tippGroup
    }

    fun availableMenuTippTypes(gameId: String): List<MatchTippType> {
        val game = getGame(gameId) ?: return emptyList()
        return TippScopeAvailability.getAvailableMenuTippTypes(game)
    }

    fun addEntry(
        tippGroupId: String,
        friendId: String,
        friendName: String,
        prediction: String,
        totalPaid: Double,
        currentRoundAmount: Double,
        note: String?,
        participation: EntryParticipation = EntryParticipation.LOCAL_ONLY,
        jackpotCatchUpAmount: Double = 0.0
    ): Entry? {
        if (getTippGroup(tippGroupId) == null) return null

        val newEntry = Entry(
            id = "entry-${System.currentTimeMillis()}",
            friendId = friendId,
            friendName = friendName.trim(),
            prediction = prediction.trim(),
            amount = totalPaid,
            currentRoundAmount = currentRoundAmount,
            note = note?.trim()?.takeIf { it.isNotEmpty() },
            participation = participation,
            jackpotCatchUpAmount = jackpotCatchUpAmount
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
        localStore.persistEntry(tippGroupId, newEntry)
        notifyChanged()
        return newEntry
    }

    fun addEntryToTippGroup(
        tippGroupId: String,
        friendId: String,
        prediction: String,
        note: String?,
        participation: EntryParticipation = EntryParticipation.LOCAL_ONLY
    ): Entry? {
        val game = getGameForTippGroup(tippGroupId) ?: return null
        if (!TippScopeAvailability.canAddEntryToGame(game)) return null
        val tippGroup = getTippGroup(tippGroupId) ?: return null
        val friend = getFriend(friendId) ?: return null
        if (isFriendInTippGroup(tippGroupId, friendId)) return null
        val entryAmount = tippGroup.entryAmount ?: return null
        if (entryAmount <= 0.0) return null

        val trimmedPrediction = prediction.trim()
        if (trimmedPrediction.isEmpty()) return null

        val round = findGameLocation(game.id)?.let { (roundId, _, _) -> getRound(roundId) }
        val payment = buildEntryPaymentSnapshot(
            tippGroup = tippGroup,
            participation = participation,
            round = round,
            game = game
        ) ?: return null

        return addEntry(
            tippGroupId = tippGroupId,
            friendId = friend.id,
            friendName = friend.name,
            prediction = trimmedPrediction,
            totalPaid = payment.totalPaid,
            currentRoundAmount = payment.currentRoundAmount,
            note = note,
            participation = payment.participation,
            jackpotCatchUpAmount = payment.jackpotCatchUpAmount
        )
    }

    fun getEntry(tippGroupId: String, entryId: String): Entry? {
        if (tippGroupId.isBlank() || entryId.isBlank()) return null
        return getTippGroup(tippGroupId)?.entries?.find { it.id == entryId }
    }

    fun updateEntry(
        tippGroupId: String,
        entryId: String,
        request: EntryUpdateRequest
    ): Entry? {
        if (tippGroupId.isBlank() || entryId.isBlank()) return null
        val trimmedPrediction = request.prediction.trim()
        if (trimmedPrediction.isEmpty()) return null
        if (request.amount <= 0.0) return null

        val tippGroup = getTippGroup(tippGroupId) ?: return null
        val existing = tippGroup.entries.find { it.id == entryId } ?: return null
        val friend = getFriend(request.friendId) ?: return null

        val friendTaken = tippGroup.entries.any {
            it.id != entryId && it.friendId == friend.id
        }
        if (friendTaken) return null

        val updatedEntry = when (existing.participation) {
            EntryParticipation.LOCAL_ONLY -> existing.copy(
                friendId = friend.id,
                friendName = friend.name,
                prediction = trimmedPrediction,
                amount = request.amount,
                currentRoundAmount = request.amount,
                participation = EntryParticipation.LOCAL_ONLY,
                jackpotCatchUpAmount = 0.0,
                note = request.note?.trim()?.takeIf { it.isNotEmpty() }
            )
            EntryParticipation.JACKPOT -> existing.copy(
                friendId = friend.id,
                friendName = friend.name,
                prediction = trimmedPrediction,
                note = request.note?.trim()?.takeIf { it.isNotEmpty() }
            )
        }

        roundsInternal = roundsInternal.map { round ->
            round.copy(
                days = round.days.map { day ->
                    day.copy(
                        games = day.games.map { game ->
                            game.copy(
                                tippGroups = game.tippGroups.map { group ->
                                    if (group.id != tippGroupId) return@map group
                                    group.copy(
                                        entries = group.entries.map { entry ->
                                            if (entry.id == entryId) updatedEntry else entry
                                        }
                                    )
                                }
                            )
                        }
                    )
                }
            )
        }
        localStore.persistEntry(tippGroupId, updatedEntry)
        notifyChanged()
        return updatedEntry
    }

    fun updateGameResult(
        gameId: String,
        teamAScore: Int?,
        teamBScore: Int?,
        status: MatchStatus
    ): Boolean {
        if (gameId.isBlank() || getGame(gameId) == null) return false
        val location = findGameLocation(gameId) ?: return false
        val (roundId, day, game) = location
        val updatedGame = game.copy(
            teamAScore = teamAScore,
            teamBScore = teamBScore,
            status = status
        )

        roundsInternal = roundsInternal.map { round ->
            round.copy(
                days = round.days.map { dayItem ->
                    dayItem.copy(
                        games = dayItem.games.map { gameItem ->
                            if (gameItem.id != gameId) return@map gameItem
                            updatedGame
                        }
                    )
                }
            )
        }
        localStore.updateGame(updatedGame, roundId, day)
        notifyChanged()
        return true
    }

    fun deleteRound(roundId: String): Boolean {
        if (roundId.isBlank()) return false
        val before = roundsInternal.size
        roundsInternal = roundsInternal.filterNot { it.id == roundId }
        if (roundsInternal.size == before) return false
        localStore.deleteLeague(roundId)
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
        localStore.deleteMatch(gameId)
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
        localStore.deleteTippGroup(tippGroupId)
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
        localStore.deleteEntry(tippGroupId, entryId)
        notifyChanged()
        return true
    }

    fun deleteEntries(tippGroupId: String, entryIds: Collection<String>): Int {
        if (tippGroupId.isBlank()) return 0
        val ids = entryIds.map { it.trim() }.filter { it.isNotEmpty() }.toSet()
        if (ids.isEmpty()) return 0

        var removedCount = 0
        roundsInternal = roundsInternal.map { round ->
            round.copy(
                days = round.days.map { day ->
                    day.copy(
                        games = day.games.map { game ->
                            game.copy(
                                tippGroups = game.tippGroups.map { group ->
                                    if (group.id != tippGroupId) return@map group
                                    val remaining = group.entries.filterNot { it.id in ids }
                                    removedCount += group.entries.size - remaining.size
                                    group.copy(entries = remaining)
                                }
                            )
                        }
                    )
                }
            )
        }
        if (removedCount == 0) return 0
        ids.forEach { entryId -> localStore.deleteEntry(tippGroupId, entryId) }
        notifyChanged()
        return removedCount
    }

    private fun findGameLocation(gameId: String): Triple<String, Day, Game>? {
        roundsInternal.forEach { round ->
            round.days.forEach { day ->
                day.games.find { it.id == gameId }?.let { game ->
                    return Triple(round.id, day, game)
                }
            }
        }
        return null
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

    private fun buildEntryPaymentSnapshot(
        tippGroup: TippGroup,
        participation: EntryParticipation,
        round: Round? = null,
        game: Game? = null
    ): EntryPaymentSnapshot? {
        val entryAmount = tippGroup.entryAmount ?: return null
        if (entryAmount <= 0.0) return null

        return when (participation) {
            EntryParticipation.LOCAL_ONLY -> EntryPaymentSnapshot(
                participation = EntryParticipation.LOCAL_ONLY,
                currentRoundAmount = entryAmount,
                jackpotCatchUpAmount = 0.0,
                totalPaid = entryAmount
            )
            EntryParticipation.JACKPOT -> {
                val resolvedRound = round ?: return null
                val resolvedGame = game ?: return null
                val context = JackpotChainCalculator.buildJackpotCatchUpContext(
                    resolvedRound,
                    resolvedGame,
                    tippGroup
                )
                EntryPaymentSnapshot(
                    participation = EntryParticipation.JACKPOT,
                    currentRoundAmount = entryAmount,
                    jackpotCatchUpAmount = context.catchUpAmount,
                    totalPaid = entryAmount + context.catchUpAmount
                )
            }
        }
    }

    private fun getAllGames(): List<Game> =
        roundsInternal.flatMap { it.days }.flatMap { it.games }

    private fun buildSampleFriends(): List<Friend> = listOf(
        Friend(id = FRIEND_ID_ALEX, firstName = "Alex", createdAt = 0L),
        Friend(id = FRIEND_ID_JOHN, firstName = "John", createdAt = 0L)
    )

    private fun buildSampleRound(): Round {
        val correctScoreGroup = TippGroup(
            id = TIPP_GROUP_ID,
            title = "Correct Score",
            timeScope = TimeScope.FULL_TIME,
            entries = listOf(
                Entry(
                    id = "entry-1",
                    friendId = FRIEND_ID_ALEX,
                    friendName = "Alex",
                    prediction = "2:1",
                    amount = 10.0,
                    currentRoundAmount = 10.0,
                    note = "Confident home win"
                ),
                Entry(
                    id = "entry-2",
                    friendId = FRIEND_ID_JOHN,
                    friendName = "John",
                    prediction = "1:1",
                    amount = 10.0,
                    currentRoundAmount = 10.0
                )
            ),
            entryAmount = 10.0
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
