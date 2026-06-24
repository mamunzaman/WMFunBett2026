package com.example.wmfunbett2026.ui.matchcenter

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.data.jackpot.TippGroupV2Settlement
import com.example.wmfunbett2026.data.jackpot.TippGroupV2SettlementPhase
import com.example.wmfunbett2026.data.jackpot.TippGroupV2WinnerLine
import com.example.wmfunbett2026.data.model.EntryParticipation
import com.example.wmfunbett2026.data.model.Game
import com.example.wmfunbett2026.data.model.MatchStatus
import com.example.wmfunbett2026.data.model.toEuroLabel

@Composable
fun tippGroupV2CompactSummary(settlement: TippGroupV2Settlement): String {
    val calc = settlement.calculation
    return when (settlement.phase) {
        TippGroupV2SettlementPhase.WAITING_RESULT ->
            stringResource(R.string.game_tipp_summary_waiting)
        TippGroupV2SettlementPhase.NO_ENTRIES ->
            stringResource(R.string.result_no_entries)
        TippGroupV2SettlementPhase.FINISHED_NO_WINNERS ->
            stringResource(
                R.string.game_tipp_summary_no_winner_carry,
                (calc?.carryForwardJackpot ?: 0.0).toEuroLabel()
            )
        TippGroupV2SettlementPhase.FINISHED_WINNERS -> {
            val jackpotLines = settlement.winnerLines.filter {
                it.participation == EntryParticipation.JACKPOT && it.jackpotShare > 0.0
            }
            when {
                jackpotLines.size == 1 ->
                    stringResource(
                        R.string.game_tipp_summary_jackpot_winner,
                        jackpotLines.first().totalPayout.toEuroLabel()
                    )
                jackpotLines.size > 1 ->
                    stringResource(
                        R.string.game_tipp_summary_jackpot_winners,
                        jackpotLines.size,
                        jackpotLines.first().totalPayout.toEuroLabel()
                    )
                else ->
                    stringResource(
                        R.string.game_tipp_summary_winners,
                        settlement.winnerLines.size,
                        (calc?.currentSharePerWinner ?: 0.0).toEuroLabel()
                    )
            }
        }
    }
}

@Composable
fun entryV2PayoutLabel(game: Game, isWinner: Boolean, payout: Double?): String? {
    if (!game.hasResult || game.status != MatchStatus.FINISHED || !isWinner) return null
    val amount = payout ?: return null
    if (amount <= 0.0) return null
    return stringResource(R.string.entry_winner_payout, amount.toEuroLabel())
}

@Composable
fun winnerLineLabel(line: TippGroupV2WinnerLine): String {
    return if (line.jackpotShare > 0.0) {
        stringResource(
            R.string.result_winner_line_split,
            line.name,
            line.currentShare.toEuroLabel(),
            line.jackpotShare.toEuroLabel()
        )
    } else {
        stringResource(
            R.string.result_winner_line_total,
            line.name,
            line.totalPayout.toEuroLabel()
        )
    }
}
