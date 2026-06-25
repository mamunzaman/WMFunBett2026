package com.example.wmfunbett2026.ui.designsystem.cards

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.wmfunbett2026.ui.theme.TextSecondary

@Composable
fun AppInfoCard(
    text: String,
    modifier: Modifier = Modifier
) {
    AppSurfaceCard(modifier = modifier) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}
