package com.example.wmfunbett2026.ui.matchcenter

import com.example.wmfunbett2026.data.model.Game
import com.example.wmfunbett2026.data.model.MatchStatus
import com.example.wmfunbett2026.data.model.Round
import com.example.wmfunbett2026.data.repository.FunBettRepository
import com.example.wmfunbett2026.data.winner.TippGroupWinnerEngine
import com.example.wmfunbett2026.data.winner.TippGroupWinnerOutcome

data class FlatGameItem(
    val roundId: String,
    val roundName: String,
    val dayId: String,
    val dayName: String,
    val game: Game
)

fun flattenAllGames(rounds: List<Round>): List<FlatGameItem> =
    rounds.flatMap { round ->
        round.days.flatMap { day ->
            day.games.map { game ->
                FlatGameItem(
                    roundId = round.id,
                    roundName = round.name,
                    dayId = day.id,
                    dayName = day.name,
                    game = game
                )
            }
        }
    }

fun countActiveGames(rounds: List<Round>): Int =
    flattenAllGames(rounds).count { item ->
        item.game.status == MatchStatus.LIVE ||
            item.game.status == MatchStatus.NOT_STARTED
    }

fun countOpenRounds(rounds: List<Round>): Int =
    flattenAllGames(rounds).sumOf { item ->
        item.game.tippGroups.count { group ->
            group.entries.isNotEmpty() &&
                TippGroupWinnerEngine.calculate(item.game, group) is TippGroupWinnerOutcome.Pending
        }
    }

fun loadFlatGames(): List<FlatGameItem> = flattenAllGames(FunBettRepository.getRounds())

enum class MatchCenterOutcomeBadge {
    ACTIVE,
    NO_WINNER,
    FINISHED,
    LIVE
}

fun resolveOutcomeBadge(game: Game): MatchCenterOutcomeBadge? {
    return when (game.status) {
        MatchStatus.LIVE -> MatchCenterOutcomeBadge.LIVE
        MatchStatus.NOT_STARTED -> {
            if (game.tippGroups.any { it.entries.isNotEmpty() }) {
                MatchCenterOutcomeBadge.ACTIVE
            } else {
                null
            }
        }
        MatchStatus.FINISHED -> {
            if (game.tippGroups.isEmpty()) return MatchCenterOutcomeBadge.FINISHED
            val outcomes = game.tippGroups.map { TippGroupWinnerEngine.calculate(game, it) }
            when {
                outcomes.any { it is TippGroupWinnerOutcome.NoWinner } ->
                    MatchCenterOutcomeBadge.NO_WINNER
                outcomes.any { it is TippGroupWinnerOutcome.Winners } ->
                    MatchCenterOutcomeBadge.FINISHED
                else -> MatchCenterOutcomeBadge.FINISHED
            }
        }
    }
}

fun Game.centerScoreText(): String {
    if (teamAScore != null && teamBScore != null) return "$teamAScore:$teamBScore"
    return "--:--"
}

fun Game.primaryTippLabel(): String? =
    tippGroups.firstOrNull()?.title
