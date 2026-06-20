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
fun RoundsScreen(
    onRoundClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val rounds = SampleData.getRounds()

    HierarchyScaffold(
        title = "Rounds",
        breadcrumbs = HierarchyLabels.forRoundsList(),
        onBackClick = null,
        fabLabel = "Create Round",
        onFabClick = {},
        modifier = modifier
    ) { contentModifier ->
        HierarchyListContent(
            items = rounds,
            emptyMessage = "No rounds yet",
            modifier = contentModifier,
            key = { it.id }
        ) { round ->
            NavListCard(
                title = round.name,
                subtitle = "Tap to view days",
                onClick = { onRoundClick(round.id) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoundsScreenPreview() {
    WMFunBett2026Theme {
        RoundsScreen(onRoundClick = {})
    }
}
