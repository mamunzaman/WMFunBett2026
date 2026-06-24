package com.example.wmfunbett2026.data.jackpot.v3

/**
 * V3 main pot state for a round. See [docs/JACKPOT_RULES_V3.md].
 */
data class MainJackpotPot(
    val amount: Double,
    val sourceRounds: List<JackpotSourceRound>,
    val isActive: Boolean,
    val activeJackpotTippGroupId: String?
)

/**
 * One source Tipp Round that contributed to or created jackpot obligation.
 */
data class JackpotSourceRound(
    val gameId: String,
    val tippGroupId: String,
    val tippGroupName: String,
    val entryAmount: Double,
    val contributedAmount: Double
)

enum class JackpotRoundStatus {
    AVAILABLE,
    ACTIVE,
    SETTLED
}

/**
 * Payment breakdown when joining a Jackpot Round.
 */
data class JackpotJoinBreakdown(
    val currentAmount: Double,
    val catchUpAmount: Double,
    val totalAmount: Double,
    val missedSourceRoundCount: Int
)

/**
 * Settlement outcome for a Jackpot Round.
 */
data class JackpotSettlementResult(
    val currentRoundPot: Double,
    val jackpotPot: Double,
    val totalPayout: Double,
    val winnerCount: Int,
    val jackpotResetRequired: Boolean
)
