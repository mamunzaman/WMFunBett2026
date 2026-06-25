package com.example.wmfunbett2026.ui.designsystem.cards

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.wmfunbett2026.ui.designsystem.layout.StatCardValueSpacing
import com.example.wmfunbett2026.ui.theme.JackpotGold
import com.example.wmfunbett2026.ui.theme.PrimaryText
import com.example.wmfunbett2026.ui.theme.SecondaryText

@Composable
fun AppStatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    highlightValue: Boolean = false
) {
    AppSurfaceCard(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = SecondaryText
        )
        Spacer(modifier = Modifier.height(StatCardValueSpacing))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = if (highlightValue) JackpotGold else PrimaryText
        )
    }
}
