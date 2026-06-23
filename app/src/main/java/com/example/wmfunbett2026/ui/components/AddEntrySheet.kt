package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.data.model.Friend
import com.example.wmfunbett2026.data.model.TippGroupEntryBlockReason
import com.example.wmfunbett2026.data.model.toEuroLabel
import com.example.wmfunbett2026.data.repository.FunBettRepository
import com.example.wmfunbett2026.ui.theme.TextSecondary

private const val QUICK_PICK_VISIBLE_COUNT = 4
private const val SEARCH_RESULT_LIMIT = 5

private object FriendQuickPickRecents {
    private const val MAX_RECENTS = 5
    private val recentIds = ArrayDeque<String>()

    fun record(friendId: String) {
        recentIds.remove(friendId)
        recentIds.addFirst(friendId)
        while (recentIds.size > MAX_RECENTS) {
            recentIds.removeLast()
        }
    }

    fun resolveQuickPickFriends(allFriends: List<Friend>): List<Friend> {
        if (allFriends.isEmpty()) return emptyList()
        val byId = allFriends.associateBy { it.id }
        val fromRecents = recentIds.mapNotNull { byId[it] }
        if (fromRecents.isNotEmpty()) return fromRecents.take(QUICK_PICK_VISIBLE_COUNT)

        return allFriends
            .sortedBy { it.name.lowercase() }
            .take(QUICK_PICK_VISIBLE_COUNT)
    }
}

@Composable
fun AddEntrySheet(
    tippGroupId: String,
    onDismiss: () -> Unit,
    onCreate: (firstName: String, lastName: String, prediction: String, note: String?) -> Unit
) {
    FunBettRepository.dataVersion.intValue
    val entryBlockReason = remember(tippGroupId, FunBettRepository.dataVersion.intValue) {
        FunBettRepository.getTippGroupEntryBlockReason(tippGroupId)
    }
    if (entryBlockReason != null) {
        EntryBlockedInfoSheet(reason = entryBlockReason, onDismiss = onDismiss)
        return
    }

    val tippGroup = remember(tippGroupId, FunBettRepository.dataVersion.intValue) {
        FunBettRepository.getTippGroup(tippGroupId)
    }
    val friends = remember(FunBettRepository.dataVersion.intValue) {
        FunBettRepository.getFriends()
    }
    val joinedFriendIds = remember(tippGroupId, FunBettRepository.dataVersion.intValue) {
        FunBettRepository.getFriendIdsInTippGroup(tippGroupId)
    }
    val entryAmount = tippGroup?.entryAmount

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var friendSearchQuery by remember { mutableStateOf("") }
    var prediction by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    val searchFocusRequester = remember { FocusRequester() }

    val trimmedFirst = firstName.trim()
    val trimmedLast = lastName.trim()
    val matchedFriend = remember(friends, trimmedFirst, trimmedLast) {
        if (trimmedFirst.isEmpty()) null
        else friends.find {
            it.firstName.equals(trimmedFirst, ignoreCase = true) &&
                it.lastName.equals(trimmedLast, ignoreCase = true)
        }
    }
    val duplicateInGroup = matchedFriend?.id in joinedFriendIds
    val canCreate = trimmedFirst.isNotEmpty() &&
        !duplicateInGroup &&
        prediction.isNotBlank() &&
        entryAmount != null &&
        entryAmount > 0.0

    FormBottomSheet(
        title = stringResource(R.string.add_entry),
        onDismiss = onDismiss,
        primaryActionLabel = stringResource(R.string.create),
        onPrimaryAction = {
            if (duplicateInGroup) return@FormBottomSheet
            matchedFriend?.id?.let { FriendQuickPickRecents.record(it) }
            onCreate(
                trimmedFirst,
                trimmedLast,
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

        Spacer(modifier = Modifier.height(12.dp))

        PersonNameFields(
            firstName = firstName,
            onFirstNameChange = { firstName = it },
            lastName = lastName,
            onLastNameChange = { lastName = it }
        )

        if (friends.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            EntryFriendQuickPickSection(
                friends = friends,
                searchQuery = friendSearchQuery,
                onSearchQueryChange = { friendSearchQuery = it },
                onFriendPicked = { friend ->
                    firstName = friend.firstName
                    lastName = friend.lastName
                    friendSearchQuery = ""
                },
                searchFocusRequester = searchFocusRequester
            )
        }

        if (duplicateInGroup) {
            Spacer(modifier = Modifier.height(8.dp))
            FormErrorText(text = stringResource(R.string.add_entry_friend_already_joined))
        }

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
private fun EntryFriendQuickPickSection(
    friends: List<Friend>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onFriendPicked: (Friend) -> Unit,
    searchFocusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    val normalizedQuery = searchQuery.trim().lowercase()
    val searchResults = remember(friends, normalizedQuery) {
        if (normalizedQuery.isEmpty()) {
            emptyList()
        } else {
            friends
                .filter { it.name.lowercase().contains(normalizedQuery) }
                .take(SEARCH_RESULT_LIMIT)
        }
    }
    val quickPickFriends = remember(friends) {
        FriendQuickPickRecents.resolveQuickPickFriends(friends)
    }
    val showMoreChip = friends.size > quickPickFriends.size

    Column(modifier = modifier.fillMaxWidth()) {
        FormSectionLabel(text = stringResource(R.string.add_entry_friend_label))

        Spacer(modifier = Modifier.height(8.dp))

        FormOutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(searchFocusRequester),
            label = { Text(stringResource(R.string.add_entry_search_friend)) },
            singleLine = true
        )

        if (normalizedQuery.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            if (searchResults.isEmpty()) {
                Text(
                    text = stringResource(R.string.add_entry_no_friend_found),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    searchResults.forEach { result ->
                        FriendSearchResultCard(
                            friendName = result.name,
                            onClick = { onFriendPicked(result) }
                        )
                    }
                }
            }
        } else if (quickPickFriends.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            FormSectionLabel(text = stringResource(R.string.add_entry_recently_used))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickPickFriends.forEach { friend ->
                    FriendQuickPickChip(
                        friendName = friendDisplayShortName(friend.firstName, friend.lastName),
                        onClick = { onFriendPicked(friend) }
                    )
                }
                if (showMoreChip) {
                    FriendQuickPickMoreChip(
                        onClick = { searchFocusRequester.requestFocus() }
                    )
                }
            }
        }
    }
}
