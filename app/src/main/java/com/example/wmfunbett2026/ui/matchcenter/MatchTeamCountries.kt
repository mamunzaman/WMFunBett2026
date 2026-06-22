package com.example.wmfunbett2026.ui.matchcenter

import java.util.Locale

data class MatchTeamCountry(
    val name: String,
    val flagEmoji: String
)

object MatchTeamCountryCatalog {
    val countries: List<MatchTeamCountry> = listOf(
        MatchTeamCountry("Germany", "🇩🇪"),
        MatchTeamCountry("Brazil", "🇧🇷"),
        MatchTeamCountry("Spain", "🇪🇸"),
        MatchTeamCountry("France", "🇫🇷"),
        MatchTeamCountry("Argentina", "🇦🇷"),
        MatchTeamCountry("England", "🏴󠁧󠁢󠁥󠁮󠁧󠁿"),
        MatchTeamCountry("Italy", "🇮🇹"),
        MatchTeamCountry("Portugal", "🇵🇹"),
        MatchTeamCountry("Netherlands", "🇳🇱"),
        MatchTeamCountry("Belgium", "🇧🇪")
    )

    private val byKey: Map<String, MatchTeamCountry> =
        countries.associateBy { it.name.lowercase(Locale.ROOT) }

    fun find(input: String): MatchTeamCountry? =
        byKey[input.trim().lowercase(Locale.ROOT)]

    fun suggestions(query: String, limit: Int = 6): List<MatchTeamCountry> {
        val normalizedQuery = query.trim().lowercase(Locale.ROOT)
        if (normalizedQuery.isEmpty()) return emptyList()
        return countries
            .filter { it.name.lowercase(Locale.ROOT).contains(normalizedQuery) }
            .take(limit)
    }

    fun normalizeForStorage(input: String): String {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return trimmed
        return find(trimmed)?.name ?: titleCaseWords(trimmed)
    }

    private fun titleCaseWords(input: String): String =
        input.split(Regex("\\s+"))
            .filter { it.isNotEmpty() }
            .joinToString(" ") { word ->
                word.lowercase(Locale.getDefault()).replaceFirstChar { char ->
                    if (char.isLowerCase()) {
                        char.titlecase(Locale.getDefault())
                    } else {
                        char.toString()
                    }
                }
            }
}

fun matchTeamFlagEmojiOrNull(teamName: String): String? {
    MatchTeamCountryCatalog.find(teamName)?.flagEmoji?.let { return it }
    val fallback = teamFlagEmoji(teamName)
    return fallback.takeIf { it != "⚽" }
}

fun matchTeamInitials(teamName: String): String {
    val words = teamName.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
    return when {
        words.isEmpty() -> "?"
        words.size == 1 -> words[0].take(2).uppercase(Locale.getDefault())
        else -> "${words[0].first()}${words[1].first()}".uppercase(Locale.getDefault())
    }
}
