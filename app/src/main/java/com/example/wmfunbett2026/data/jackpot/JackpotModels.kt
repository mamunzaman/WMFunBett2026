package com.example.wmfunbett2026.data.jackpot

data class JackpotCarryItem(
    val sourceGameId: String,
    val sourceGameLabel: String,
    val sourceTippGroupId: String,
    val sourceTippGroupTitle: String,
    val requiredAmountPerPerson: Double
)

data class EntryJoinBreakdown(
    val carryItems: List<JackpotCarryItem>,
    val previousJackpotBuyIn: Double,
    val currentRoundEntryAmount: Double,
    val totalRequired: Double
)

data class JackpotCarryOverSummary(
    val incomingJackpot: Double,
    val currentCollected: Double,
    val totalPot: Double,
    val carriedOut: Double,
    val hasWinner: Boolean,
    val winnerCount: Int,
    val sharePerWinner: Double
)
