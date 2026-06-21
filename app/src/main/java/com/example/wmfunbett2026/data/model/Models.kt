package com.example.wmfunbett2026.data.model

enum class TimeScope(val label: String) {
    HALF_TIME("Half Time"),
    HALF_TIME_STOPPAGE("Half Time + Stoppage"),
    SECOND_HALF("Second Half"),
    FULL_TIME("Full Time"),
    FULL_TIME_STOPPAGE("Full Time + Stoppage");

    fun defaultTippTitle(): String = "$label Tipp"
}

data class Entry(
    val id: String,
    val name: String,
    val prediction: String,
    val amount: Double,
    val note: String? = null
)

data class TippGroup(
    val id: String,
    val title: String,
    val timeScope: TimeScope,
    val entries: List<Entry>
) {
    val totalAmount: Double get() = entries.sumOf { it.amount }
}

data class Game(
    val id: String,
    val teamA: String,
    val teamB: String,
    val dateTimeLabel: String,
    val resultPlaceholder: String,
    val tippGroups: List<TippGroup>
) {
    val displayName: String get() = "$teamA vs $teamB"
    val totalKasse: Double get() = tippGroups.sumOf { it.totalAmount }
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
