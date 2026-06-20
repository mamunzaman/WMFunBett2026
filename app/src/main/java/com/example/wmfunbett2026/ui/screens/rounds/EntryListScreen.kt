package com.example.wmfunbett2026.ui.screens.rounds

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.wmfunbett2026.data.sample.SampleData
import com.example.wmfunbett2026.ui.components.EntryListCard
import com.example.wmfunbett2026.ui.components.HierarchyListContent
import com.example.wmfunbett2026.ui.components.HierarchyScaffold
import com.example.wmfunbett2026.ui.navigation.HierarchyLabels
import com.example.wmfunbett2026.ui.theme.WMFunBett2026Theme

@Composable
fun EntryListScreen(
    roundId: String,
    dayId: String,
    gameId: String,
    tippGroupId: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val entries = SampleData.getEntries(tippGroupId)

    HierarchyScaffold(
        title = "Entries",
        breadcrumbs = HierarchyLabels.forEntryList(roundId, dayId, gameId, tippGroupId),
        onBackClick = onBackClick,
        fabLabel = "Create Entry",
        onFabClick = {},
        modifier = modifier
    ) { contentModifier ->
        HierarchyListContent(
            items = entries,
            emptyMessage = "No entries yet",
            modifier = contentModifier,
            key = { it.id }
        ) { entry ->
            EntryListCard(
                playerName = entry.playerName,
                prediction = entry.prediction,
                stake = entry.stake
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EntryListScreenPreview() {
    WMFunBett2026Theme {
        EntryListScreen(
            roundId = SampleData.ROUND_ID,
            dayId = SampleData.DAY_ID,
            gameId = SampleData.GAME_ID,
            tippGroupId = SampleData.TIPP_GROUP_ID,
            onBackClick = {}
        )
    }
}
