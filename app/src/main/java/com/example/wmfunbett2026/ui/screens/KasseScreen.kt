package com.example.wmfunbett2026.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.wmfunbett2026.data.model.toEuroLabel
import com.example.wmfunbett2026.data.repository.FunBettRepository
import com.example.wmfunbett2026.ui.components.JackpotCard
import com.example.wmfunbett2026.ui.components.PlaceholderCard
import com.example.wmfunbett2026.ui.components.TopLevelScrollContent
import com.example.wmfunbett2026.ui.theme.WMFunBett2026Theme

@Composable
fun KasseScreen(modifier: Modifier = Modifier) {
    FunBettRepository.dataVersion.intValue
    val totalKasse = FunBettRepository.getTotalKassePreview().toEuroLabel()
    val entryCount = FunBettRepository.getTotalEntryCount()

    TopLevelScrollContent(modifier = modifier) {
        Text(
            text = "Kasse / Jackpot",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        JackpotCard(amount = totalKasse, modifier = Modifier.fillMaxWidth())

        PlaceholderCard(message = "Calculation coming later · $entryCount entries loaded")
    }
}

@Preview(showBackground = true)
@Composable
fun KasseScreenPreview() {
    WMFunBett2026Theme {
        KasseScreen()
    }
}
