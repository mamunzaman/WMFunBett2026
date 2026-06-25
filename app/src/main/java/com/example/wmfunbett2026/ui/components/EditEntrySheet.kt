package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.data.model.Entry
import com.example.wmfunbett2026.data.model.EntryUpdateRequest
import com.example.wmfunbett2026.data.model.formatScorePrediction
import com.example.wmfunbett2026.data.model.parseScorePrediction
import com.example.wmfunbett2026.data.repository.FunBettRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEntrySheet(
    tippGroupId: String,
    entry: Entry,
    onDismiss: () -> Unit,
    onSave: (EntryUpdateRequest) -> Unit
) {
    FunBettRepository.dataVersion.intValue
    val game = remember(tippGroupId, FunBettRepository.dataVersion.intValue) {
        FunBettRepository.getGameForTippGroup(tippGroupId)
    }
    val friends = remember(FunBettRepository.dataVersion.intValue) {
        FunBettRepository.getFriends()
    }
    val joinedFriendIds = remember(tippGroupId, FunBettRepository.dataVersion.intValue) {
        FunBettRepository.getFriendIdsInTippGroup(tippGroupId)
    }
    val selectableFriends = remember(friends, joinedFriendIds, entry.friendId) {
        val takenByOthers = joinedFriendIds - entry.friendId
        friends.filter { it.id == entry.friendId || it.id !in takenByOthers }
    }

    val parsedScores = remember(entry.id) { parseScorePrediction(entry.prediction) }
    val legacyPrediction = remember(entry.id) {
        if (parsedScores == null && entry.prediction.isNotBlank()) entry.prediction else null
    }

    var selectedFriend by remember(entry.id, friends) {
        mutableStateOf(friends.find { it.id == entry.friendId })
    }
    var friendSearchQuery by remember { mutableStateOf("") }
    var scoreA by remember(entry.id) { mutableStateOf(parsedScores?.scoreA.orEmpty()) }
    var scoreB by remember(entry.id) { mutableStateOf(parsedScores?.scoreB.orEmpty()) }
    var amountInput by remember(entry.id) {
        mutableStateOf(formatEditableAmount(entry.amount))
    }
    var note by remember(entry.id) { mutableStateOf(entry.note.orEmpty()) }
    val searchFocusRequester = remember { FocusRequester() }

    val prediction = formatScorePrediction(scoreA, scoreB).orEmpty()
    val amount = amountInput.trim().replace(',', '.').toDoubleOrNull()
    val duplicateSelected = selectedFriend?.id?.let { friendId ->
        friendId != entry.friendId && friendId in joinedFriendIds
    } == true
    val canSave = selectedFriend != null &&
        !duplicateSelected &&
        prediction.isNotBlank() &&
        amount != null &&
        amount > 0.0

    AppBottomSheetContainer(
        onDismiss = onDismiss,
        footer = {
            SheetPrimaryButton(
                label = stringResource(R.string.save),
                onClick = {
                    val friend = selectedFriend ?: return@SheetPrimaryButton
                    onSave(
                        EntryUpdateRequest(
                            friendId = friend.id,
                            prediction = prediction,
                            amount = amount!!,
                            note = note.trim().takeIf { it.isNotEmpty() }
                        )
                    )
                },
                enabled = canSave
            )
            SheetTextCancelButton(onClick = onDismiss)
        }
    ) {
        AppBottomSheetHeader(
            title = stringResource(R.string.edit_entry),
            onCloseClick = onDismiss
        )

        when {
            friends.isEmpty() -> {
                Text(
                    text = stringResource(R.string.add_entry_no_friends),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            selectableFriends.isEmpty() -> {
                Text(
                    text = stringResource(R.string.add_entry_no_friends),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            else -> {
                EntryFriendPickerSection(
                    friends = selectableFriends,
                    searchQuery = friendSearchQuery,
                    onSearchQueryChange = { friendSearchQuery = it },
                    selectedFriend = selectedFriend,
                    onFriendSelected = { selectedFriend = it },
                    onClearSelection = {
                        selectedFriend = null
                        friendSearchQuery = ""
                    },
                    searchFocusRequester = searchFocusRequester
                )
            }
        }

        if (duplicateSelected) {
            Spacer(modifier = Modifier.height(8.dp))
            FormErrorText(text = stringResource(R.string.add_entry_friend_already_joined))
        }

        Spacer(modifier = Modifier.height(14.dp))

        FormOutlinedTextField(
            value = amountInput,
            onValueChange = { amountInput = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.edit_entry_amount_label)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        game?.let { match ->
            Spacer(modifier = Modifier.height(16.dp))
            FormSectionLabel(text = stringResource(R.string.prediction))
            Spacer(modifier = Modifier.height(8.dp))
            MatchScoreInputCard(
                teamA = match.teamA,
                teamB = match.teamB,
                scoreA = scoreA,
                scoreB = scoreB,
                onScoreAChange = { scoreA = it.filter { c -> c.isDigit() }.take(2) },
                onScoreBChange = { scoreB = it.filter { c -> c.isDigit() }.take(2) }
            )
            if (legacyPrediction != null && prediction.isBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.edit_entry_legacy_prediction, legacyPrediction),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

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
