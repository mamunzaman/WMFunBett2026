package com.example.wmfunbett2026.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.data.model.toEuroLabel
import com.example.wmfunbett2026.data.repository.FunBettRepository
import com.example.wmfunbett2026.ui.components.JackpotCard
import com.example.wmfunbett2026.ui.components.PlaceholderCard
import com.example.wmfunbett2026.ui.components.StatCard
import com.example.wmfunbett2026.ui.components.TopLevelScrollContent
import com.example.wmfunbett2026.ui.theme.WMFunBett2026Theme

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val version = FunBettRepository.dataVersion.intValue
    val homeStats = remember(version) {
        Triple(
            FunBettRepository.getRounds().size,
            FunBettRepository.getTotalGameCount(),
            FunBettRepository.getTotalKassePreview().toEuroLabel()
        )
    }
    val (roundCount, gameCount, totalKasse) = homeStats

    TopLevelScrollContent(modifier = modifier) {
        Text(
            text = stringResource(R.string.screen_home),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "WM Fun Bett 2026",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        JackpotCard(amount = totalKasse, modifier = Modifier.fillMaxWidth())

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
        ) {
            StatCard(label = "Tournaments", value = roundCount.toString(), modifier = Modifier.weight(1f))
            StatCard(label = "Games", value = gameCount.toString(), modifier = Modifier.weight(1f))
        }

        PlaceholderCard(message = "Offline mode · data saved in memory only")
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    WMFunBett2026Theme {
        HomeScreen()
    }
}
