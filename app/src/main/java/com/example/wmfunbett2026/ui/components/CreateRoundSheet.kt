package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.wmfunbett2026.R

@Composable
fun CreateRoundSheet(
    onDismiss: () -> Unit,
    onCreate: (name: String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    FormBottomSheet(
        title = stringResource(R.string.create_round),
        onDismiss = onDismiss,
        primaryActionLabel = stringResource(R.string.create),
        onPrimaryAction = { onCreate(name.trim()) },
        primaryActionEnabled = name.isNotBlank()
    ) {
        FormOutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.round_name)) }
        )
    }
}
