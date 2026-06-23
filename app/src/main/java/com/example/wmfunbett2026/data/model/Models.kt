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

fun parsePersonName(storedName: String): Pair<String, String> {
    val trimmed = storedName.trim()
    if (trimmed.isEmpty()) return "" to ""
    val parts = trimmed.split(Regex("\\s+")).filter { it.isNotEmpty() }
    return when {
        parts.size == 1 -> parts[0] to ""
        else -> parts.first() to parts.drop(1).joinToString(" ")
    }
}

fun formatPersonFullName(firstName: String, lastName: String = ""): String {
    val first = firstName.trim()
    val last = lastName.trim()
    return when {
        first.isEmpty() && last.isEmpty() -> ""
        first.isEmpty() -> last
        last.isEmpty() -> first
        else -> "$first $last"
    }
}

data class Friend(
    val id: String,
    val firstName: String,
    val lastName: String = "",
    val note: String? = null,
    val createdAt: Long
) {
    val name: String get() = formatPersonFullName(firstName, lastName)

    companion object {
        fun fromStoredName(
            id: String,
            storedName: String,
            note: String?,
            createdAt: Long
        ): Friend {
            val (first, last) = parsePersonName(storedName)
            return Friend(id, first, last, note, createdAt)
        }
    }
}

data class FriendWithStats(
    val friend: Friend,
    val activeEntryCount: Int,
    val activeAmountTotal: Double,
    val totalTipps: Int,
    val winCount: Int = 0
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

data class EntryUpdateRequest(
    val friendId: String,
    val prediction: String,
    val amount: Double,
    val note: String?
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

data class ScorePredictionParts(
    val scoreA: String,
    val scoreB: String
)

fun parseScorePrediction(prediction: String): ScorePredictionParts? {
    val normalized = prediction.trim().replace(Regex("\\s+"), "")
    if (normalized.isEmpty()) return null

    val separatorIndex = normalized.indexOfFirst { it == ':' || it == '-' }
    if (separatorIndex <= 0 || separatorIndex >= normalized.lastIndex) return null

    val scoreA = normalized.substring(0, separatorIndex)
    val scoreB = normalized.substring(separatorIndex + 1)
    if (scoreA.isEmpty() || scoreB.isEmpty()) return null
    if (!scoreA.all { it.isDigit() } || !scoreB.all { it.isDigit() }) return null
    if (scoreA.length > 2 || scoreB.length > 2) return null

    return ScorePredictionParts(scoreA, scoreB)
}

fun formatScorePrediction(scoreA: String, scoreB: String): String? {
    val a = scoreA.trim()
    val b = scoreB.trim()
    if (a.isEmpty() || b.isEmpty()) return null
    if (!a.all { it.isDigit() } || !b.all { it.isDigit() }) return null
    return "$a:$b"
}

fun Game.matchPreviewTimeOrNull(): String? {
    val label = dateTimeLabel.trim()
    if (label.isBlank()) return null

    val timeAfterDot = label.substringAfter('·', missingDelimiterValue = "").trim()
    if (timeAfterDot.isNotEmpty() && timeAfterDot.contains(':')) return timeAfterDot

    val timeMatch = Regex("""\b\d{1,2}:\d{2}\b""").find(label)?.value
    return timeMatch
}
