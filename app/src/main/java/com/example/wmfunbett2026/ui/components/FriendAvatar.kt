package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wmfunbett2026.data.model.Friend
import com.example.wmfunbett2026.data.model.parsePersonName
import com.example.wmfunbett2026.ui.theme.PrimaryBlue
import com.example.wmfunbett2026.ui.theme.SheetOnSurface
import com.example.wmfunbett2026.ui.theme.SheetOnSurfaceVariant

fun friendDisplayInitials(firstName: String, lastName: String = ""): String {
    val first = firstName.trim()
    val last = lastName.trim()
    return when {
        first.isEmpty() && last.isEmpty() -> "?"
        last.isEmpty() -> first.take(2).uppercase()
        else -> "${first.first().uppercaseChar()}${last.first().uppercaseChar()}"
    }
}

fun friendDisplayShortName(firstName: String, lastName: String = ""): String {
    val first = firstName.trim()
    val last = lastName.trim()
    return when {
        first.isEmpty() -> last
        last.isEmpty() -> first
        else -> "${first} ${last.first().uppercaseChar()}."
    }
}

fun friendDisplayInitials(name: String): String {
    val (first, last) = parsePersonName(name)
    return friendDisplayInitials(first, last)
}

fun friendDisplayShortName(name: String): String {
    val (first, last) = parsePersonName(name)
    return friendDisplayShortName(first, last)
}

@Composable
fun FriendAvatar(
    name: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    size: Dp = 44.dp
) {
    val (first, last) = parsePersonName(name)
    FriendAvatar(
        firstName = first,
        lastName = last,
        modifier = modifier,
        selected = selected,
        size = size
    )
}

@Composable
fun FriendAvatar(
    friend: Friend,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    size: Dp = 44.dp
) {
    FriendAvatar(
        firstName = friend.firstName,
        lastName = friend.lastName,
        modifier = modifier,
        selected = selected,
        size = size
    )
}

@Composable
fun FriendAvatar(
    firstName: String,
    lastName: String = "",
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    size: Dp = 44.dp
) {
    FriendInitialsAvatar(
        initials = friendDisplayInitials(firstName, lastName),
        modifier = modifier,
        selected = selected,
        size = size
    )
}

@Composable
fun FriendInitialsAvatar(
    initials: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    size: Dp = 44.dp
) {
    val background = if (selected) {
        PrimaryBlue.copy(alpha = 0.35f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
    }
    val borderColor = if (selected) PrimaryBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(background)
            .border(1.5.dp, borderColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = if (selected) SheetOnSurface else SheetOnSurfaceVariant,
            fontSize = if (size <= 36.dp) 11.sp else 13.sp
        )
    }
}
