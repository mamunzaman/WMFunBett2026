package com.example.wmfunbett2026.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
    onCreate: (friendId: String, prediction: String, note: String?) -> Unit
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
    val availableFriends = remember(friends, joinedFriendIds) {
        friends.filter { it.id !in joinedFriendIds }
    }
    val entryAmount = tippGroup?.entryAmount

    var selectedFriend by remember { mutableStateOf<Friend?>(null) }
    var friendSearchQuery by remember { mutableStateOf("") }
    var prediction by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    val searchFocusRequester = remember { FocusRequester() }

    val duplicateSelected = selectedFriend?.id?.let { it in joinedFriendIds } == true
    val canCreate = selectedFriend != null &&
        !duplicateSelected &&
        prediction.isNotBlank() &&
        entryAmount != null &&
        entryAmount > 0.0

    FormBottomSheet(
        title = stringResource(R.string.add_entry),
        onDismiss = onDismiss,
        primaryActionLabel = stringResource(R.string.create),
        onPrimaryAction = {
            val friend = selectedFriend ?: return@FormBottomSheet
            if (FunBettRepository.isFriendInTippGroup(tippGroupId, friend.id)) return@FormBottomSheet
            FriendQuickPickRecents.record(friend.id)
            onCreate(
                friend.id,
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

        when {
            friends.isEmpty() -> {
                Text(
                    text = stringResource(R.string.add_entry_no_friends),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            availableFriends.isEmpty() -> {
                Text(
                    text = stringResource(R.string.add_entry_all_friends_joined),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            else -> {
                EntryFriendPickerSection(
                    friends = availableFriends,
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
internal fun EntryFriendPickerSection(
    friends: List<Friend>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedFriend: Friend?,
    onFriendSelected: (Friend) -> Unit,
    onClearSelection: () -> Unit,
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

        AnimatedContent(
            targetState = selectedFriend,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "friendSelection"
        ) { friend ->
            if (friend == null) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                                        onClick = { onFriendSelected(result) }
                                    )
                                }
                            }
                        }
                    } else {
                        FormSectionLabel(text = stringResource(R.string.add_entry_recently_used))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            quickPickFriends.forEach { pickFriend ->
                                FriendQuickPickChip(
                                    friendName = friendDisplayShortName(
                                        pickFriend.firstName,
                                        pickFriend.lastName
                                    ),
                                    onClick = { onFriendSelected(pickFriend) }
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
            } else {
                FriendSelectedCard(
                    friendName = friend.name,
                    onClear = onClearSelection
                )
            }
        }
    }
}
