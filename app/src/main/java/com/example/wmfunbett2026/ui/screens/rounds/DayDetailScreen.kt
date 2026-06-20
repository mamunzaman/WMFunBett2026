package com.example.wmfunbett2026.ui.screens.rounds

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.wmfunbett2026.data.sample.SampleData
import com.example.wmfunbett2026.ui.components.HierarchyListContent
import com.example.wmfunbett2026.ui.components.HierarchyScaffold
import com.example.wmfunbett2026.ui.components.NavListCard
import com.example.wmfunbett2026.ui.navigation.HierarchyLabels
import com.example.wmfunbett2026.ui.theme.WMFunBett2026Theme

@Composable
fun DayDetailScreen(
    roundId: String,
    dayId: String,
    onBackClick: () -> Unit,
    onGameClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val day = SampleData.getDay(dayId)
    val games = SampleData.getGames(dayId)

    HierarchyScaffold(
        title = day?.name ?: "Day",
        breadcrumbs = HierarchyLabels.forDayDetail(roundId, dayId),
        onBackClick = onBackClick,
        fabLabel = "Create Game",
        onFabClick = {},
        modifier = modifier
    ) { contentModifier ->
        HierarchyListContent(
            items = games,
            emptyMessage = "No games yet",
            modifier = contentModifier,
            key = { it.id }
        ) { game ->
            NavListCard(
                title = game.displayName,
                subtitle = "Tap to view tipp groups",
                onClick = { onGameClick(game.id) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DayDetailScreenPreview() {
    WMFunBett2026Theme {
        DayDetailScreen(
            roundId = SampleData.ROUND_ID,
            dayId = SampleData.DAY_ID,
            onBackClick = {},
            onGameClick = {}
        )
    }
}
