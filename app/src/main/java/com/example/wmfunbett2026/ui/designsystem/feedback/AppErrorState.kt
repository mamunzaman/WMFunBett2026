package com.example.wmfunbett2026.ui.designsystem.feedback

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AppErrorState(
    message: String,
    modifier: Modifier = Modifier,
    title: String = "Something went wrong"
) {
    AppEmptyState(
        title = title,
        message = message,
        modifier = modifier
    )
}
