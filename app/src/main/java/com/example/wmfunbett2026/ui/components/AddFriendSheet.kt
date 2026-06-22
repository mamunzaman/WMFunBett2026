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
import com.example.wmfunbett2026.data.repository.FunBettRepository
import com.example.wmfunbett2026.ui.theme.DangerRed

@Composable
fun AddFriendSheet(
    onDismiss: () -> Unit,
    onCreate: (name: String, note: String?) -> Unit
) {
    FunBettRepository.dataVersion.intValue
    var name by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    val trimmedName = name.trim()
    val duplicateName = trimmedName.isNotEmpty() &&
        FunBettRepository.friendNameExists(trimmedName)
    val canCreate = trimmedName.isNotEmpty() && !duplicateName

    FormBottomSheet(
        title = stringResource(R.string.add_friend),
        onDismiss = onDismiss,
        primaryActionLabel = stringResource(R.string.create),
        onPrimaryAction = {
            onCreate(
                trimmedName,
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

        if (duplicateName) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.add_friend_duplicate_error),
                style = MaterialTheme.typography.bodySmall,
                color = DangerRed
            )
        }

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
