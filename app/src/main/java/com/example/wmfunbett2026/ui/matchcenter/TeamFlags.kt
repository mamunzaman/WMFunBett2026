package com.example.wmfunbett2026.ui.matchcenter

private val TEAM_FLAGS = mapOf(
    "germany" to "🇩🇪",
    "deutschland" to "🇩🇪",
    "france" to "🇫🇷",
    "brazil" to "🇧🇷",
    "brasil" to "🇧🇷",
    "spain" to "🇪🇸",
    "espana" to "🇪🇸",
    "italy" to "🇮🇹",
    "england" to "🏴󠁧󠁢󠁥󠁮󠁧󠁿",
    "netherlands" to "🇳🇱",
    "portugal" to "🇵🇹",
    "argentina" to "🇦🇷",
    "usa" to "🇺🇸",
    "united states" to "🇺🇸",
    "mexico" to "🇲🇽",
    "japan" to "🇯🇵",
    "south korea" to "🇰🇷",
    "korea republic" to "🇰🇷",
    "belgium" to "🇧🇪",
    "croatia" to "🇭🇷",
    "morocco" to "🇲🇦",
    "switzerland" to "🇨🇭",
    "poland" to "🇵🇱",
    "austria" to "🇦🇹",
    "denmark" to "🇩🇰",
    "sweden" to "🇸🇪",
    "uruguay" to "🇺🇾",
    "colombia" to "🇨🇴",
    "canada" to "🇨🇦"
)

fun teamFlagEmoji(teamName: String): String {
    val key = teamName.trim().lowercase()
    return TEAM_FLAGS[key] ?: "⚽"
}
