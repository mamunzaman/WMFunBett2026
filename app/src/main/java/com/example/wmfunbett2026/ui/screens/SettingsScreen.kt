package com.example.wmfunbett2026.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.ui.components.SettingsItemCard
import com.example.wmfunbett2026.ui.theme.WMFunBett2026Theme

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        SettingsItemCard(label = "App mode", value = "Offline only")
        SettingsItemCard(label = "Theme", value = "FIFA Dark")
        SettingsItemCard(label = "Version", value = "0.1.0")
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    WMFunBett2026Theme {
        SettingsScreen()
    }
}
