package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import com.example.wmfunbett2026.data.model.MatchTippType
import com.example.wmfunbett2026.ui.matchcenter.localizedLabel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTippGroupSheet(
    onDismiss: () -> Unit,
    onCreate: (tippType: MatchTippType, entryAmount: Double, note: String?) -> Unit
) {
    var selectedTippType by remember { mutableStateOf<MatchTippType?>(null) }
    var entryAmountInput by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var tippTypeMenuExpanded by remember { mutableStateOf(false) }

    val entryAmount = entryAmountInput.trim().toDoubleOrNull()
    val canCreate = selectedTippType != null && entryAmount != null && entryAmount > 0.0

    FormBottomSheet(
        title = stringResource(R.string.add_tipp_group),
        onDismiss = onDismiss,
        primaryActionLabel = stringResource(R.string.create),
        onPrimaryAction = {
            onCreate(
                selectedTippType!!,
                entryAmount!!,
                note.trim().takeIf { it.isNotEmpty() }
            )
        },
        primaryActionEnabled = canCreate
    ) {
        ExposedDropdownMenuBox(
            expanded = tippTypeMenuExpanded,
            onExpandedChange = { tippTypeMenuExpanded = it }
        ) {
            FormOutlinedTextField(
                value = selectedTippType?.localizedLabel().orEmpty(),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                label = { Text(stringResource(R.string.add_match_tipp_type_label)) },
                placeholder = { Text(stringResource(R.string.add_match_tipp_type_hint)) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = tippTypeMenuExpanded)
                }
            )
            ExposedDropdownMenu(
                expanded = tippTypeMenuExpanded,
                onDismissRequest = { tippTypeMenuExpanded = false }
            ) {
                MatchTippType.entries.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.localizedLabel()) },
                        onClick = {
                            selectedTippType = type
                            tippTypeMenuExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        FormOutlinedTextField(
            value = entryAmountInput,
            onValueChange = { entryAmountInput = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.add_tipp_group_entry_amount)) },
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
