package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.ui.theme.PrimaryBlue

private enum class WinnerShareMode {
    EQUAL,
    CUSTOM
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WinnerShareSettingsDialog(
    onDismiss: () -> Unit
) {
    var mode by remember { mutableStateOf(WinnerShareMode.EQUAL) }

    FormBottomSheet(
        title = stringResource(R.string.winner_share_settings),
        onDismiss = onDismiss,
        primaryActionLabel = stringResource(R.string.done),
        onPrimaryAction = onDismiss,
        showCancel = false
    ) {
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                ShareModeRow(
                    label = stringResource(R.string.equal_split_default),
                    selected = mode == WinnerShareMode.EQUAL,
                    onSelect = { mode = WinnerShareMode.EQUAL }
                )
                ShareModeRow(
                    label = stringResource(R.string.manual_custom_shares),
                    selected = mode == WinnerShareMode.CUSTOM,
                    onSelect = { mode = WinnerShareMode.CUSTOM }
                )
            }
        }
        if (mode == WinnerShareMode.CUSTOM) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.custom_shares_coming),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
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
        RadioButton(
            selected = selected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (selected) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}
