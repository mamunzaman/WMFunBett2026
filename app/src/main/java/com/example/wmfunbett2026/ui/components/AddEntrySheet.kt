package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.data.model.toEuroLabel
import com.example.wmfunbett2026.data.repository.FunBettRepository

@Composable
fun AddEntrySheet(
    tippGroupId: String,
    onDismiss: () -> Unit,
    onCreate: (name: String, prediction: String, note: String?) -> Unit
) {
    FunBettRepository.dataVersion.intValue
    val tippGroup = remember(tippGroupId, FunBettRepository.dataVersion.intValue) {
        FunBettRepository.getTippGroup(tippGroupId)
    }
    val entryAmount = tippGroup?.entryAmount

    var name by remember { mutableStateOf("") }
    var prediction by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    val canCreate = name.isNotBlank() &&
        prediction.isNotBlank() &&
        entryAmount != null &&
        entryAmount > 0.0

    FormBottomSheet(
        title = stringResource(R.string.add_entry),
        onDismiss = onDismiss,
        primaryActionLabel = stringResource(R.string.create),
        onPrimaryAction = {
            onCreate(
                name.trim(),
                prediction.trim(),
                note.trim().takeIf { it.isNotEmpty() }
            )
        },
        primaryActionEnabled = canCreate
    ) {
        if (entryAmount != null && entryAmount > 0.0) {
            LockedEntryAmountInfoRow(amountLabel = entryAmount.toEuroLabel())
        } else {
            Text(
                text = stringResource(R.string.add_entry_no_group_amount),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

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
            value = note,
            onValueChange = { note = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.note_optional)) },
            singleLine = false,
            minLines = 2
        )
    }
}
