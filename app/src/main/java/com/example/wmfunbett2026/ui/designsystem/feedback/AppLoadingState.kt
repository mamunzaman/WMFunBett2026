package com.example.wmfunbett2026.ui.designsystem.feedback

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AppLoadingState(
    message: String,
    modifier: Modifier = Modifier
) {
    AppEmptyState(message = message, modifier = modifier)
}
