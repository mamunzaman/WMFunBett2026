package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.ui.theme.TextSecondary

@Composable
fun AddEntryPlaceholderSheet(
    onDismiss: () -> Unit
) {
    FormBottomSheet(
        title = stringResource(R.string.add_entry),
        onDismiss = onDismiss,
        primaryActionLabel = stringResource(R.string.ok),
        onPrimaryAction = onDismiss,
        showCancel = false
    ) {
        Text(
            text = stringResource(R.string.add_entry_placeholder_message),
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}
