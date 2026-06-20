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
fun GameDetailScreen(
    roundId: String,
    dayId: String,
    gameId: String,
    onBackClick: () -> Unit,
    onTippGroupClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val game = SampleData.getGame(gameId)
    val tippGroups = SampleData.getTippGroups(gameId)

    HierarchyScaffold(
        title = game?.displayName ?: "Game",
        breadcrumbs = HierarchyLabels.forGameDetail(roundId, dayId, gameId),
        onBackClick = onBackClick,
        fabLabel = "Create Tipp Group",
        onFabClick = {},
        modifier = modifier
    ) { contentModifier ->
        HierarchyListContent(
            items = tippGroups,
            emptyMessage = "No tipp groups yet",
            modifier = contentModifier,
            key = { it.id }
        ) { tippGroup ->
            NavListCard(
                title = tippGroup.name,
                subtitle = "Tap to view details",
                onClick = { onTippGroupClick(tippGroup.id) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameDetailScreenPreview() {
    WMFunBett2026Theme {
        GameDetailScreen(
            roundId = SampleData.ROUND_ID,
            dayId = SampleData.DAY_ID,
            gameId = SampleData.GAME_ID,
            onBackClick = {},
            onTippGroupClick = {}
        )
    }
}
