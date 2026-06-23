package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.data.model.Entry
import com.example.wmfunbett2026.data.model.EntryUpdateRequest
import com.example.wmfunbett2026.data.model.parsePersonName
import com.example.wmfunbett2026.data.repository.FunBettRepository
import androidx.compose.ui.unit.dp

@Composable
fun EditEntrySheet(
    tippGroupId: String,
    entry: Entry,
    onDismiss: () -> Unit,
    onSave: (EntryUpdateRequest) -> Unit
) {
    FunBettRepository.dataVersion.intValue
    val friend = remember(entry.friendId, FunBettRepository.dataVersion.intValue) {
        FunBettRepository.getFriend(entry.friendId)
    }
    val initialNames = remember(entry.id, friend) {
        friend?.let { it.firstName to it.lastName } ?: parsePersonName(entry.friendName)
    }

    var firstName by remember(entry.id) { mutableStateOf(initialNames.first) }
    var lastName by remember(entry.id) { mutableStateOf(initialNames.second) }
    var prediction by remember(entry.id) { mutableStateOf(entry.prediction) }
    var amountInput by remember(entry.id) {
        mutableStateOf(formatEditableAmount(entry.amount))
    }
    var note by remember(entry.id) { mutableStateOf(entry.note.orEmpty()) }

    val amount = amountInput.trim().replace(',', '.').toDoubleOrNull()
    val trimmedFirst = firstName.trim()
    val canSave = trimmedFirst.isNotEmpty() &&
        prediction.isNotBlank() &&
        amount != null &&
        amount > 0.0

    FormBottomSheet(
        title = stringResource(R.string.edit_entry),
        onDismiss = onDismiss,
        primaryActionLabel = stringResource(R.string.save),
        onPrimaryAction = {
            onSave(
                EntryUpdateRequest(
                    friendId = entry.friendId,
                    firstName = trimmedFirst,
                    lastName = lastName.trim(),
                    prediction = prediction.trim(),
                    amount = amount!!,
                    note = note.trim().takeIf { it.isNotEmpty() }
                )
            )
        },
        primaryActionEnabled = canSave
    ) {
        PersonNameFields(
            firstName = firstName,
            onFirstNameChange = { firstName = it },
            lastName = lastName,
            onLastNameChange = { lastName = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

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
            label = { Text(stringResource(R.string.edit_entry_amount_label)) },
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

private fun formatEditableAmount(amount: Double): String =
    if (amount % 1.0 == 0.0) amount.toInt().toString() else amount.toString()
