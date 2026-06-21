package com.example.wmfunbett2026.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.data.model.toEuroLabel
import com.example.wmfunbett2026.data.repository.FunBettRepository
import com.example.wmfunbett2026.ui.components.JackpotCard
import com.example.wmfunbett2026.ui.components.MatchCenterHeader
import com.example.wmfunbett2026.ui.components.PlaceholderCard
import com.example.wmfunbett2026.ui.theme.DarkNavy
import com.example.wmfunbett2026.ui.theme.WMFunBett2026Theme

@Composable
fun KasseScreen(modifier: Modifier = Modifier) {
    FunBettRepository.dataVersion.intValue
    val totalKasse = FunBettRepository.getTotalKassePreview().toEuroLabel()
    val entryCount = FunBettRepository.getTotalEntryCount()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavy)
    ) {
        MatchCenterHeader(title = stringResource(R.string.screen_kasse))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.total_jackpot),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            JackpotCard(amount = totalKasse, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            PlaceholderCard(
                message = stringResource(R.string.kasse_placeholder, entryCount)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KasseScreenPreview() {
    WMFunBett2026Theme {
        KasseScreen()
    }
}
