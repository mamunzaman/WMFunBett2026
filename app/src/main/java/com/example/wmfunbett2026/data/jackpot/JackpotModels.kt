package com.example.wmfunbett2026.data.jackpot

import com.example.wmfunbett2026.data.model.EntryParticipation
import com.example.wmfunbett2026.data.model.TippGroupSettlementStatus

/**
 * Legacy carry item used by current carry-item list (per-person buy-in model).
 * Future catch-up uses [JackpotCatchUpContext] slot count instead of pot size / player count.
 */
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

/**
 * Future Add Entry breakdown for [EntryParticipation].
 * Catch-up = missed jackpot rounds × current round amount (not pot size or player count).
 */
data class ParticipationEntryJoinBreakdown(
    val participation: EntryParticipation,
    val catchUpSlots: Int,
    val catchUpAmount: Double,
    val currentRoundEntryAmount: Double,
    val totalDue: Double,
    val chainSteps: List<JackpotChainStep> = emptyList()
)

/**
 * One FINISHED prior-game step when walking the jackpot chain.
 * Only JACKPOT entry money contributes; chain breaks after a jackpot winner.
 */
data class JackpotChainStep(
    val gameId: String,
    val gameLabel: String,
    val tippGroupId: String,
    val jackpotPotAdded: Double,
    val hadJackpotWinner: Boolean
)

/**
 * Catch-up context for a new JACKPOT entry.
 * Formula: [missedRoundSlots] × [entryAmount].
 */
data class JackpotCatchUpContext(
    val missedRoundSlots: Int,
    val entryAmount: Double,
    val catchUpAmount: Double
)

/**
 * Legacy single-pot summary used by current UI and repository.
 * Future screens should use [SplitTippGroupSettlementSummary].
 */
data class JackpotCarryOverSummary(
    val incomingJackpot: Double,
    val currentCollected: Double,
    val totalPot: Double,
    val carriedOut: Double,
    val hasWinner: Boolean,
    val winnerCount: Int,
    val sharePerWinner: Double
)

data class LocalPotSummary(
    val pot: Double,
    val winnerCount: Int,
    val sharePerWinner: Double,
    /** True when finished with no local winner; local money does not auto-carry (MVP). */
    val closed: Boolean = false
)

/**
 * Jackpot stream only. LOCAL_ONLY money is excluded and never carries forward.
 * [carriedOut] applies only when there is no jackpot winner (MVP: local pot closes).
 */
data class JackpotPotSummary(
    val incomingJackpot: Double,
    val currentCollected: Double,
    val totalPot: Double,
    val carriedOut: Double,
    val winnerCount: Int,
    val sharePerWinner: Double
)

/**
 * Split settlement: local pot and jackpot pot resolved independently.
 * Both winner types can exist in the same tipp group.
 */
data class SplitTippGroupSettlementSummary(
    val status: TippGroupSettlementStatus,
    val local: LocalPotSummary,
    val jackpot: JackpotPotSummary
)

/**
 * Signal for jackpot chain break after split winner resolution.
 * Only a jackpot winner breaks the chain; local winners do not.
 */
data class JackpotChainBreakSignal(
    val hasJackpotWinner: Boolean,
    val jackpotWinnerEntryIds: List<String> = emptyList()
)

/** One entry in a V2 settlement round (pure calculator input). */
data class JackpotV2EntryInput(
    val entryId: String,
    val participation: EntryParticipation,
    val currentRoundAmount: Double,
    val isCorrect: Boolean
)

data class JackpotV2RoundInput(
    val incomingJackpot: Double,
    val entries: List<JackpotV2EntryInput>
)

/** Result of [JackpotV2Calculator.calculate]. */
data class JackpotV2Result(
    val currentPot: Double,
    val jackpotPot: Double,
    val currentWinners: List<String>,
    val jackpotWinners: List<String>,
    val currentSharePerWinner: Double,
    val jackpotSharePerWinner: Double,
    val payoutsByEntryId: Map<String, Double>,
    val carryForwardJackpot: Double,
    val localReturnedAmount: Double
)