package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.ui.theme.PrimaryText
import com.example.wmfunbett2026.ui.theme.SecondaryText

private enum class WinnerShareMode {
    EQUAL,
    CUSTOM
}

@Composable
fun WinnerShareSettingsDialog(
    onDismiss: () -> Unit
) {
    var mode by remember { mutableStateOf(WinnerShareMode.EQUAL) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Winner Share Settings") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                ShareModeRow(
                    label = "Equal split (default)",
                    selected = mode == WinnerShareMode.EQUAL,
                    onSelect = { mode = WinnerShareMode.EQUAL }
                )
                Spacer(modifier = Modifier.height(10.dp))
                ShareModeRow(
                    label = "Manual custom shares",
                    selected = mode == WinnerShareMode.CUSTOM,
                    onSelect = { mode = WinnerShareMode.CUSTOM }
                )
                if (mode == WinnerShareMode.CUSTOM) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Custom winner shares coming next",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SecondaryText
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}

@Composable
private fun ShareModeRow(
    label: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RadioButton(selected = selected, onClick = onSelect)
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (selected) PrimaryText else SecondaryText
        )
    }
}
