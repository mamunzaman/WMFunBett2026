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
    onCreate: (firstName: String, lastName: String, note: String?) -> Unit
) {
    FunBettRepository.dataVersion.intValue
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    val trimmedFirst = firstName.trim()
    val trimmedLast = lastName.trim()
    val duplicateName = trimmedFirst.isNotEmpty() &&
        FunBettRepository.friendNameExists(trimmedFirst, trimmedLast)
    val canCreate = trimmedFirst.isNotEmpty() && !duplicateName

    FormBottomSheet(
        title = stringResource(R.string.add_friend),
        onDismiss = onDismiss,
        primaryActionLabel = stringResource(R.string.create),
        onPrimaryAction = {
            onCreate(
                trimmedFirst,
                trimmedLast,
                note.trim().takeIf { it.isNotEmpty() }
            )
        },
        primaryActionEnabled = canCreate
    ) {
        PersonNameFields(
            firstName = firstName,
            onFirstNameChange = { firstName = it },
            lastName = lastName,
            onLastNameChange = { lastName = it }
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

@Composable
fun EditFriendSheet(
    friendId: String,
    onDismiss: () -> Unit,
    onSave: (firstName: String, lastName: String, note: String?) -> Unit
) {
    FunBettRepository.dataVersion.intValue
    val friend = remember(friendId, FunBettRepository.dataVersion.intValue) {
        FunBettRepository.getFriend(friendId)
    } ?: return

    var firstName by remember(friendId) { mutableStateOf(friend.firstName) }
    var lastName by remember(friendId) { mutableStateOf(friend.lastName) }
    var note by remember(friendId) { mutableStateOf(friend.note.orEmpty()) }
    val trimmedFirst = firstName.trim()
    val trimmedLast = lastName.trim()
    val duplicateName = trimmedFirst.isNotEmpty() &&
        FunBettRepository.friendNameExists(trimmedFirst, trimmedLast, excludeFriendId = friendId)
    val canSave = trimmedFirst.isNotEmpty() && !duplicateName

    FormBottomSheet(
        title = stringResource(R.string.edit_friend),
        onDismiss = onDismiss,
        primaryActionLabel = stringResource(R.string.save),
        onPrimaryAction = {
            onSave(
                trimmedFirst,
                trimmedLast,
                note.trim().takeIf { it.isNotEmpty() }
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
