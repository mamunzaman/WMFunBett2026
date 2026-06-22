package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.R

@Composable
fun AddEntrySheet(
    tippGroupId: String,
    onDismiss: () -> Unit,
    onCreate: (name: String, prediction: String, amount: Double, note: String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var prediction by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    val amount = amountInput.trim().toDoubleOrNull()
    val canCreate = name.isNotBlank() &&
        prediction.isNotBlank() &&
        amount != null &&
        amount > 0.0

    FormBottomSheet(
        title = stringResource(R.string.add_entry),
        onDismiss = onDismiss,
        primaryActionLabel = stringResource(R.string.create),
        onPrimaryAction = {
            onCreate(
                name.trim(),
                prediction.trim(),
                amount!!,
                note.trim().takeIf { it.isNotEmpty() }
            )
        },
        primaryActionEnabled = canCreate
    ) {
        FormOutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.name)) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        FormOutlinedTextField(
            value = prediction,
            onValueChange = { prediction = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.prediction)) },
            placeholder = { Text(stringResource(R.string.add_entry_prediction_hint)) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        FormOutlinedTextField(
            value = amountInput,
            onValueChange = { amountInput = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.add_entry_amount)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        Spacer(modifier = Modifier.height(10.dp))

        FormOutlinedTextField(
            value = note,
            onValueChange = { note = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.note_optional)) },
            singleLine = false,
            minLines = 2
        )
    }
}
