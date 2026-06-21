package com.example.wmfunbett2026.ui.matchcenter

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.data.model.Entry
import com.example.wmfunbett2026.data.model.Game
import com.example.wmfunbett2026.data.model.MatchStatus
import com.example.wmfunbett2026.data.model.Round
import com.example.wmfunbett2026.data.model.toEuroLabel
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

enum class MatchSection(@StringRes val titleRes: Int) {
    LIVE(R.string.section_live),
    TODAY(R.string.section_today),
    TOMORROW(R.string.section_tomorrow),
    FINISHED(R.string.section_finished)
}

enum class MatchSelectFilter(@StringRes val labelRes: Int) {
    LIVE(R.string.section_live),
    TODAY(R.string.section_today),
    TOMORROW(R.string.section_tomorrow),
    MATCHDAY_1(R.string.matchday_1),
    MATCHDAY_2(R.string.matchday_2),
    ALL_MATCHES(R.string.filter_all_matches);

    companion object {
        val sheetOptions = listOf(
            LIVE,
            TODAY,
            TOMORROW,
            MATCHDAY_1,
            MATCHDAY_2,
            ALL_MATCHES
        )
    }
}

/** @deprecated Use [MatchSelectFilter] */
typealias MatchdayFilter = MatchSelectFilter

data class LeagueSummary(
    val id: String,
    val name: String,
    val roundId: String?,
    val matchCount: Int,
    val activeMatchCount: Int,
    val tippGroupCount: Int
)

data class FriendSummary(
    val name: String,
    val joinedMatches: Int,
    val totalTipped: Double,
    val winsPlaceholder: Int
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

fun flattenGamesForRound(roundId: String?): List<FlatGameItem> {
    if (roundId.isNullOrBlank()) return emptyList()
    val round = FunBettRepository.getRound(roundId) ?: return emptyList()
    return flattenAllGames(listOf(round))
}

fun loadFlatGames(): List<FlatGameItem> = flattenAllGames(FunBettRepository.getRounds())

fun FlatGameItem.section(): MatchSection {
    return when (game.status) {
        MatchStatus.LIVE -> MatchSection.LIVE
        MatchStatus.FINISHED -> MatchSection.FINISHED
        MatchStatus.NOT_STARTED -> {
            when {
                game.dateTimeLabel.contains("tomorrow", ignoreCase = true) ||
                    dayName.contains("tomorrow", ignoreCase = true) -> MatchSection.TOMORROW
                else -> MatchSection.TODAY
            }
        }
    }
}

fun filterMatches(
    items: List<FlatGameItem>,
    liveOnlyActive: Boolean,
    selectFilter: MatchSelectFilter
): List<FlatGameItem> {
    if (liveOnlyActive) {
        return items.filter { it.game.status == MatchStatus.LIVE }
    }
    return items.filter { it.matchesSelectFilter(selectFilter) }
}

enum class MatchTimeQuickFilter(@StringRes val labelRes: Int) {
    LIVE(R.string.section_live),
    TODAY(R.string.section_today),
    THREE_HOURS(R.string.quick_filter_3h),
    FORTY_EIGHT_HOURS(R.string.quick_filter_48h)
}

data class MatchLeagueQuickFilter(
    val id: String,
    val emoji: String,
    @StringRes val labelRes: Int
)

val matchLeagueQuickFilters = listOf(
    MatchLeagueQuickFilter("wc2026", "🌍", R.string.quick_filter_league_wc),
    MatchLeagueQuickFilter("bundesliga", "🇩🇪", R.string.quick_filter_league_bundesliga),
    MatchLeagueQuickFilter("premier-league", "🏴", R.string.quick_filter_league_premier),
    MatchLeagueQuickFilter("la-liga", "🇪🇸", R.string.quick_filter_league_laliga),
    MatchLeagueQuickFilter("champions-league", "⭐", R.string.quick_filter_league_ucl)
)

fun applyMatchQuickFilters(
    items: List<FlatGameItem>,
    timeFilter: MatchTimeQuickFilter?,
    leagueFilterId: String?
): List<FlatGameItem> {
    var result = items
    timeFilter?.let { filter ->
        result = result.filter { it.matchesTimeQuickFilter(filter) }
    }
    leagueFilterId?.let { leagueId ->
        val roundId = leagueRoundId(leagueId)
        result = if (roundId != null) {
            result.filter { it.roundId == roundId }
        } else {
            emptyList()
        }
    }
    return result
}

private fun FlatGameItem.matchesTimeQuickFilter(filter: MatchTimeQuickFilter): Boolean {
    return when (filter) {
        MatchTimeQuickFilter.LIVE -> game.status == MatchStatus.LIVE
        MatchTimeQuickFilter.TODAY -> section() == MatchSection.TODAY
        MatchTimeQuickFilter.THREE_HOURS ->
            section() == MatchSection.TODAY && game.status == MatchStatus.NOT_STARTED
        MatchTimeQuickFilter.FORTY_EIGHT_HOURS ->
            section() == MatchSection.TODAY || section() == MatchSection.TOMORROW
    }
}

fun FlatGameItem.matchesSelectFilter(filter: MatchSelectFilter): Boolean {
    return when (filter) {
        MatchSelectFilter.ALL_MATCHES -> true
        MatchSelectFilter.LIVE -> game.status == MatchStatus.LIVE
        MatchSelectFilter.MATCHDAY_1 -> dayName.contains("matchday 1", ignoreCase = true)
        MatchSelectFilter.MATCHDAY_2 -> dayName.contains("matchday 2", ignoreCase = true)
        MatchSelectFilter.TODAY -> section() == MatchSection.TODAY
        MatchSelectFilter.TOMORROW -> section() == MatchSection.TOMORROW
    }
}

/** @deprecated Use [matchesSelectFilter] */
fun FlatGameItem.matchesFilter(filter: MatchSelectFilter): Boolean = matchesSelectFilter(filter)

fun groupMatchesBySection(items: List<FlatGameItem>): Map<MatchSection, List<FlatGameItem>> {
    return MatchSection.entries.associateWith { section ->
        items.filter { it.section() == section }
    }.filterValues { it.isNotEmpty() }
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

fun loadLeagueSummaries(): List<LeagueSummary> {
    val rounds = FunBettRepository.getRounds()
    val wcRound = rounds.find { it.id == FunBettRepository.ROUND_ID }
    val wcGames = wcRound?.let { flattenAllGames(listOf(it)) }.orEmpty()

    return listOf(
        LeagueSummary(
            id = "wc2026",
            name = "World Cup 2026",
            roundId = FunBettRepository.ROUND_ID,
            matchCount = wcGames.size,
            activeMatchCount = wcGames.count {
                it.game.status == MatchStatus.LIVE || it.game.status == MatchStatus.NOT_STARTED
            },
            tippGroupCount = wcGames.sumOf { it.game.tippGroups.size }
        ),
        LeagueSummary("bundesliga", "Bundesliga", null, 8, 3, 2),
        LeagueSummary("premier-league", "Premier League", null, 10, 4, 3),
        LeagueSummary("la-liga", "La Liga", null, 9, 2, 1),
        LeagueSummary("champions-league", "Champions League", null, 6, 2, 2),
        LeagueSummary("custom-league", "Custom League", null, 0, 0, 0)
    )
}

fun leagueRoundId(leagueId: String): String? =
    loadLeagueSummaries().find { it.id == leagueId }?.roundId

fun loadFriendSummaries(): List<FriendSummary> {
    val entriesByPerson = linkedMapOf<String, MutableList<Pair<FlatGameItem, Entry>>>()

    loadFlatGames().forEach { item ->
        item.game.tippGroups.forEach { group ->
            group.entries.forEach { entry ->
                entriesByPerson.getOrPut(entry.name) { mutableListOf() }.add(item to entry)
            }
        }
    }

    return entriesByPerson.map { (name, rows) ->
        val matchIds = rows.map { it.first.game.id }.distinct()
        FriendSummary(
            name = name,
            joinedMatches = matchIds.size,
            totalTipped = rows.sumOf { it.second.currentRoundAmount },
            winsPlaceholder = if (rows.any { it.first.game.status == MatchStatus.FINISHED }) 1 else 0
        )
    }.sortedBy { it.name }
}

enum class MatchCenterOutcomeBadge {
    ACTIVE,
    UPCOMING,
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
    return "- : -"
}

fun Game.primaryTippLabel(): String? =
    tippGroups.firstOrNull()?.title

fun isMatchCardExpandedLayout(game: Game): Boolean =
    game.status == MatchStatus.LIVE ||
        (game.status == MatchStatus.NOT_STARTED &&
            game.tippGroups.any { it.entries.isNotEmpty() })

enum class MatchCardDisplayMode {
    LIST,
    DETAIL
}

fun matchCardSurfaceColor(
    game: Game,
    displayMode: MatchCardDisplayMode = MatchCardDisplayMode.LIST
): Color {
    if (displayMode == MatchCardDisplayMode.DETAIL) {
        return com.example.wmfunbett2026.ui.theme.MatchCardActiveSurface
    }
    return if (isMatchCardExpandedLayout(game)) {
        com.example.wmfunbett2026.ui.theme.MatchCardActiveSurface
    } else {
        com.example.wmfunbett2026.ui.theme.MatchCardCompactSurface
    }
}

fun resolveMatchStatusBadge(
    game: Game,
    displayMode: MatchCardDisplayMode = MatchCardDisplayMode.LIST
): MatchCenterOutcomeBadge? {
    if (displayMode == MatchCardDisplayMode.DETAIL &&
        game.status == MatchStatus.NOT_STARTED &&
        game.tippGroups.none { it.entries.isNotEmpty() }
    ) {
        return MatchCenterOutcomeBadge.UPCOMING
    }
    return resolveOutcomeBadge(game)
}

fun shouldUseDetailUpcomingLayout(
    game: Game,
    displayMode: MatchCardDisplayMode
): Boolean =
    displayMode == MatchCardDisplayMode.DETAIL && !isMatchCardExpandedLayout(game)

enum class MatchCardWinnerSide { TEAM_A, TEAM_B, DRAW }

fun Game.matchCardWinnerSide(): MatchCardWinnerSide? {
    val scoreA = teamAScore ?: return null
    val scoreB = teamBScore ?: return null
    return when {
        scoreA > scoreB -> MatchCardWinnerSide.TEAM_A
        scoreB > scoreA -> MatchCardWinnerSide.TEAM_B
        else -> MatchCardWinnerSide.DRAW
    }
}

fun FriendSummary.totalTippedLabel(): String = totalTipped.toEuroLabel()
