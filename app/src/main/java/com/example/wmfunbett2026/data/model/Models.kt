package com.example.wmfunbett2026.data.model

data class Round(
    val id: String,
    val name: String
)

data class Day(
    val id: String,
    val roundId: String,
    val name: String
)

data class Game(
    val id: String,
    val dayId: String,
    val homeTeam: String,
    val awayTeam: String
) {
    val displayName: String get() = "$homeTeam vs $awayTeam"
}

data class TippGroup(
    val id: String,
    val gameId: String,
    val name: String
)

data class Entry(
    val id: String,
    val tippGroupId: String,
    val playerName: String,
    val prediction: String,
    val stake: String
) {
    val displayText: String get() = "$playerName - $prediction - $stake"
}
