package com.example.wmfunbett2026.ui.screens.rounds

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.data.sample.SampleData
import com.example.wmfunbett2026.ui.components.HierarchyScaffold
import com.example.wmfunbett2026.ui.components.NavListCard
import com.example.wmfunbett2026.ui.components.PlaceholderCard
import com.example.wmfunbett2026.ui.navigation.HierarchyLabels
import com.example.wmfunbett2026.ui.theme.WMFunBett2026Theme

@Composable
fun TippGroupDetailScreen(
    roundId: String,
    dayId: String,
    gameId: String,
    tippGroupId: String,
    onBackClick: () -> Unit,
    onViewEntriesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tippGroup = SampleData.getTippGroup(tippGroupId)
    val entries = SampleData.getEntries(tippGroupId)

    HierarchyScaffold(
        title = tippGroup?.name ?: "Tipp Group",
        breadcrumbs = HierarchyLabels.forTippGroupDetail(roundId, dayId, gameId, tippGroupId),
        onBackClick = onBackClick,
        fabLabel = "Create Entry",
        onFabClick = {},
        modifier = modifier
    ) { contentModifier ->
        Column(
            modifier = contentModifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PlaceholderCard(
                message = "${entries.size} entries in this group"
            )

            NavListCard(
                title = "View Entries",
                subtitle = entries.joinToString(" · ") { "${it.playerName} ${it.prediction}" },
                onClick = onViewEntriesClick
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TippGroupDetailScreenPreview() {
    WMFunBett2026Theme {
        TippGroupDetailScreen(
            roundId = SampleData.ROUND_ID,
            dayId = SampleData.DAY_ID,
            gameId = SampleData.GAME_ID,
            tippGroupId = SampleData.TIPP_GROUP_ID,
            onBackClick = {},
            onViewEntriesClick = {}
        )
    }
}
