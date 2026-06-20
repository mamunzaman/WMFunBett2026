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
fun RoundDetailScreen(
    roundId: String,
    onBackClick: () -> Unit,
    onDayClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val round = SampleData.getRound(roundId)
    val days = SampleData.getDays(roundId)

    HierarchyScaffold(
        title = round?.name ?: "Round",
        breadcrumbs = HierarchyLabels.forRoundDetail(roundId),
        onBackClick = onBackClick,
        fabLabel = "Create Day",
        onFabClick = {},
        modifier = modifier
    ) { contentModifier ->
        HierarchyListContent(
            items = days,
            emptyMessage = "No days yet",
            modifier = contentModifier,
            key = { it.id }
        ) { day ->
            NavListCard(
                title = day.name,
                subtitle = "Tap to view games",
                onClick = { onDayClick(day.id) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoundDetailScreenPreview() {
    WMFunBett2026Theme {
        RoundDetailScreen(
            roundId = SampleData.ROUND_ID,
            onBackClick = {},
            onDayClick = {}
        )
    }
}
