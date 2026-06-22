package com.example.wmfunbett2026.data.tipp

import android.util.Log
import com.example.wmfunbett2026.data.model.Game
import com.example.wmfunbett2026.data.model.MatchStatus
import com.example.wmfunbett2026.data.model.MatchTippType
import com.example.wmfunbett2026.data.model.TimeScope
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

object TippScopeAvailability {

    private const val TAG = "FunBettTippScope"

    const val NO_MATCH_TIME_NOTE = "No match time set · all tipp types available"
    const val NONE_AVAILABLE_MESSAGE = "No Tipp type available for this game."

    private val dateFormatter = DateTimeFormatter.ofPattern("EEE d MMM yyyy", Locale.ENGLISH)
    private val timeFormatter = DateTimeFormatter.ofPattern("H:mm", Locale.ENGLISH)
    private val timeOnlyPattern = Regex("""^\d{1,2}:\d{2}$""")

    fun getAvailableMenuTippTypes(
        game: Game,
        now: LocalDateTime = LocalDateTime.now()
    ): List<MatchTippType> =
        MatchTippType.entries.filter { canCreateMenuTippType(game, it, now) }

    fun canCreateMenuTippType(
        game: Game,
        tippType: MatchTippType,
        now: LocalDateTime = LocalDateTime.now()
    ): Boolean {
        val scope = tippType.toTimeScope()
        if (game.tippGroups.any { it.timeScope == scope }) return false

        val matchStart = parseMatchStartSafe(game.dateTimeLabel, now)
        if (game.status == MatchStatus.NOT_STARTED && (matchStart == null || now.isBefore(matchStart))) {
            return true
        }
        if (matchStart == null) return true

        return isScopeOpenAtSafe(scope, now, matchStart)
    }

    fun getAvailableScopes(
        game: Game,
        now: LocalDateTime = LocalDateTime.now()
    ): List<TimeScope> {
        return runCatching {
            val existing = game.tippGroups.map { it.timeScope }.toSet()
            val matchStart = parseMatchStartSafe(game.dateTimeLabel, now)

            val timeEligible = if (matchStart == null) {
                TimeScope.entries
            } else {
                TimeScope.entries.filter { scope ->
                    isScopeOpenAtSafe(scope, now, matchStart)
                }
            }

            timeEligible.filter { it !in existing }
        }.getOrElse { error ->
            Log.e(TAG, "getAvailableScopes failed gameId=${game.id}", error)
            unusedScopesOnly(game)
        }
    }

    fun hasMatchTime(game: Game, now: LocalDateTime = LocalDateTime.now()): Boolean {
        return runCatching {
            parseMatchStartSafe(game.dateTimeLabel, now) != null
        }.getOrElse { error ->
            Log.e(TAG, "hasMatchTime failed gameId=${game.id}", error)
            false
        }
    }

    fun canAddEntryToGame(game: Game): Boolean =
        game.status == MatchStatus.NOT_STARTED

    private fun unusedScopesOnly(game: Game): List<TimeScope> {
        val existing = game.tippGroups.map { it.timeScope }.toSet()
        return TimeScope.entries.filter { it !in existing }
    }

    private fun isScopeOpenAtSafe(
        scope: TimeScope,
        now: LocalDateTime,
        matchStart: LocalDateTime
    ): Boolean {
        return runCatching {
            isScopeOpenAt(scope, now, matchStart)
        }.getOrDefault(false)
    }

    private fun isScopeOpenAt(
        scope: TimeScope,
        now: LocalDateTime,
        matchStart: LocalDateTime
    ): Boolean {
        val halfTimeStart = matchStart.plusMinutes(45)
        val secondHalfStart = matchStart.plusMinutes(60)

        return when (scope) {
            TimeScope.HALF_TIME, TimeScope.HALF_TIME_STOPPAGE -> now.isBefore(matchStart)
            TimeScope.SECOND_HALF -> !now.isBefore(halfTimeStart) && now.isBefore(secondHalfStart)
            TimeScope.FULL_TIME, TimeScope.FULL_TIME_STOPPAGE, TimeScope.FULL_TIME_PENALTIES ->
                now.isBefore(secondHalfStart)
        }
    }

    private fun parseMatchStartSafe(
        dateTimeLabel: String,
        reference: LocalDateTime
    ): LocalDateTime? {
        return runCatching {
            parseMatchStart(dateTimeLabel, reference)
        }.getOrElse { error ->
            Log.e(TAG, "schedule=$dateTimeLabel parse=error", error)
            null
        }
    }

    private fun parseMatchStart(
        dateTimeLabel: String,
        reference: LocalDateTime
    ): LocalDateTime? {
        val label = dateTimeLabel.trim()
        if (label.isBlank() || label.equals("TBD", ignoreCase = true)) return null

        val parts = label.split("·").map { it.trim() }.filter { it.isNotEmpty() }
        if (parts.isEmpty()) return null

        return when (parts.size) {
            1 -> {
                val part = parts.firstOrNull() ?: return null
                parseSinglePart(part, reference)
            }
            else -> {
                val datePart = parts.firstOrNull() ?: return null
                val date = parseDatePart(datePart, reference.toLocalDate()) ?: return null
                val time = parseTimePart(parts[1]) ?: DEFAULT_KICKOFF
                LocalDateTime.of(date, time)
            }
        }
    }

    private fun parseSinglePart(part: String, reference: LocalDateTime): LocalDateTime? {
        return when {
            timeOnlyPattern.matches(part) -> {
                val time = parseTimePart(part) ?: return null
                LocalDateTime.of(reference.toLocalDate(), time)
            }
            else -> {
                val date = parseDatePart(part, reference.toLocalDate()) ?: return null
                LocalDateTime.of(date, DEFAULT_KICKOFF)
            }
        }
    }

    private fun parseDatePart(dateStr: String, referenceDate: LocalDate): LocalDate? {
        if (timeOnlyPattern.matches(dateStr.trim())) return null
        return runCatching {
            LocalDate.parse("${dateStr.trim()} ${referenceDate.year}", dateFormatter)
        }.getOrElse { error ->
            if (error !is DateTimeParseException) {
                Log.e(TAG, "parseDatePart failed input=$dateStr", error)
            }
            null
        }
    }

    private fun parseTimePart(timeStr: String): LocalTime? {
        return runCatching {
            LocalTime.parse(timeStr.trim(), timeFormatter)
        }.getOrElse { error ->
            if (error !is DateTimeParseException) {
                Log.e(TAG, "parseTimePart failed input=$timeStr", error)
            }
            null
        }
    }

    private val DEFAULT_KICKOFF: LocalTime = LocalTime.of(20, 0)
}
