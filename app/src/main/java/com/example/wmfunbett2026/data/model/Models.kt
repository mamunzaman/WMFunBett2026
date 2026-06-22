package com.example.wmfunbett2026.data.model

enum class MatchStatus(val label: String) {
    NOT_STARTED("Not Started"),
    LIVE("Live"),
    FINISHED("Finished")
}

enum class TimeScope(val label: String) {
    HALF_TIME("Half Time"),
    HALF_TIME_STOPPAGE("Half Time + Stoppage"),
    SECOND_HALF("Second Half"),
    FULL_TIME("Full Time"),
    FULL_TIME_STOPPAGE("Full Time + Stoppage"),
    FULL_TIME_PENALTIES("Full Time + Penalty Shootout");

    fun defaultTippTitle(): String = "$label Tipp"
}

enum class MatchTippType(val label: String) {
    HALF_TIME("Half Time"),
    SECOND_HALF("Second Half"),
    FULL_TIME("Full Time"),
    FULL_TIME_PENALTIES("Full Time + Penalty Shootout");

    fun toTimeScope(): TimeScope = when (this) {
        HALF_TIME -> TimeScope.HALF_TIME
        SECOND_HALF -> TimeScope.SECOND_HALF
        FULL_TIME -> TimeScope.FULL_TIME
        FULL_TIME_PENALTIES -> TimeScope.FULL_TIME_PENALTIES
    }

    fun defaultTippTitle(): String = toTimeScope().defaultTippTitle()
}

data class Friend(
    val id: String,
    val name: String,
    val note: String? = null,
    val createdAt: Long
)

data class FriendWithStats(
    val friend: Friend,
    val activeEntryCount: Int,
    val activeAmountTotal: Double
)

data class FriendFinancialSummary(
    val friend: Friend,
    val activeEntryCount: Int,
    val activeAmountTotal: Double
)

data class FriendEntryHistoryItem(
    val entryId: String,
    val leagueName: String,
    val matchName: String,
    val tippGroupName: String,
    val prediction: String,
    val amount: Double,
    val createdAtMs: Long
)

data class Entry(
    val id: String,
    val friendId: String,
    val friendName: String,
    val prediction: String,
    val amount: Double,
    val currentRoundAmount: Double,
    val note: String? = null
)

data class TippGroup(
    val id: String,
    val title: String,
    val timeScope: TimeScope,
    val entries: List<Entry>,
    val entryAmount: Double? = null,
    val note: String? = null
) {
    val totalAmount: Double get() = entries.sumOf { it.currentRoundAmount }
}

enum class TippGroupSettlementStatus {
    PENDING,
    NO_WINNERS,
    WINNERS
}

enum class TippGroupEntryBlockReason {
    MATCH_LIVE,
    MATCH_FINISHED,
    ALL_FRIENDS_JOINED
}

enum class TippGroupCreationBlockReason {
    MATCH_LIVE,
    MATCH_FINISHED
}

data class TippGroupSettlementSummary(
    val status: TippGroupSettlementStatus,
    val totalCollected: Double,
    val winnerCount: Int,
    val sharePerWinner: Double
)

data class Game(
    val id: String,
    val teamA: String,
    val teamB: String,
    val dateTimeLabel: String,
    val teamAScore: Int? = null,
    val teamBScore: Int? = null,
    val status: MatchStatus = MatchStatus.NOT_STARTED,
    val tippGroups: List<TippGroup>,
    val note: String? = null
) {
    val displayName: String get() = "$teamA vs $teamB"
    val totalKasse: Double get() = tippGroups.sumOf { it.totalAmount }
    val hasResult: Boolean get() = teamAScore != null && teamBScore != null

    fun resultDisplayText(): String {
        if (!hasResult) return "No result yet"
        return "$teamA $teamAScore : $teamBScore $teamB"
    }

    fun compactScoreOrNull(): String? {
        val scoreA = teamAScore ?: return null
        val scoreB = teamBScore ?: return null
        return "$scoreA:$scoreB"
    }
}

data class Day(
    val id: String,
    val name: String,
    val games: List<Game>
)

data class Round(
    val id: String,
    val name: String,
    val note: String? = null,
    val days: List<Day>
)

fun Double.toEuroLabel(): String =
    if (this % 1.0 == 0.0) "€${toInt()}" else "€%.2f".format(this)
