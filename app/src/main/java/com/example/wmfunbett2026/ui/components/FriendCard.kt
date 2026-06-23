package com.example.wmfunbett2026.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.data.model.FriendWithStats
import com.example.wmfunbett2026.ui.theme.Divider
import com.example.wmfunbett2026.ui.theme.JackpotGold
import com.example.wmfunbett2026.ui.theme.MatchCardCompactSurface
import com.example.wmfunbett2026.ui.theme.PrimaryBlue
import com.example.wmfunbett2026.ui.theme.Surface
import com.example.wmfunbett2026.ui.theme.SurfaceElevated
import com.example.wmfunbett2026.ui.theme.TextPrimary
import com.example.wmfunbett2026.ui.theme.TextSecondary
import kotlinx.coroutines.delay

const val FriendsEntranceDurationMs = 400
const val FriendsEntranceStaggerMs = 85
val FriendsEntranceOffset = 32.dp
const val FriendsContentStaggerStart = 1

private val FriendGridCardShape = RoundedCornerShape(14.dp)
private val FriendOverviewCardShape = RoundedCornerShape(12.dp)
private val FriendsToolbarShape = RoundedCornerShape(10.dp)
private val FriendsToolbarHeight = 32.dp

@Composable
fun FriendsEntranceHost(
    staggerIndex: Int,
    entranceSession: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var animateIn by remember(entranceSession, staggerIndex) { mutableStateOf(false) }
    val startOffsetPx = with(LocalDensity.current) { FriendsEntranceOffset.toPx() }

    LaunchedEffect(entranceSession, staggerIndex) {
        animateIn = false
        delay(staggerIndex * FriendsEntranceStaggerMs.toLong())
        animateIn = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0f,
        animationSpec = tween(
            durationMillis = FriendsEntranceDurationMs,
            easing = FastOutSlowInEasing
        ),
        label = "friendsEntranceAlpha"
    )
    val offsetYPx by animateFloatAsState(
        targetValue = if (animateIn) 0f else startOffsetPx,
        animationSpec = tween(
            durationMillis = FriendsEntranceDurationMs,
            easing = FastOutSlowInEasing
        ),
        label = "friendsEntranceOffset"
    )

    Box(
        modifier = modifier.graphicsLayer {
            this.alpha = alpha
            translationY = offsetYPx
        }
    ) {
        content()
    }
}

@Composable
fun FriendsToolbarButton(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val borderColor = Divider.copy(alpha = if (selected) 0.85f else 0.55f)

    Box(
        modifier = modifier
            .height(FriendsToolbarHeight)
            .clip(FriendsToolbarShape)
            .background(MatchCardCompactSurface)
            .border(1.dp, borderColor, FriendsToolbarShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun FriendsToolbarIconButton(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val borderColor = Divider.copy(alpha = if (selected) 0.85f else 0.55f)

    Box(
        modifier = modifier
            .size(FriendsToolbarHeight)
            .clip(FriendsToolbarShape)
            .background(MatchCardCompactSurface)
            .border(1.dp, borderColor, FriendsToolbarShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (selected) PrimaryBlue else TextSecondary,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun FriendOverviewStatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = TextPrimary
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(FriendOverviewCardShape)
            .background(MatchCardCompactSurface)
            .border(1.dp, Divider.copy(alpha = 0.65f), FriendOverviewCardShape)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = valueColor,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun FriendGridCard(
    item: FriendWithStats,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null
) {
    val friend = item.friend
    val shape = FriendGridCardShape

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, Divider.copy(alpha = 0.7f), shape)
            .then(
                when {
                    onLongClick != null -> Modifier.combinedClickable(
                        onClick = onClick,
                        onLongClick = onLongClick
                    )
                    else -> Modifier.clickable(onClick = onClick)
                }
            )
            .background(Surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FriendAvatar(
                friend = friend,
                size = 40.dp
            )
            Text(
                text = friendDisplayShortName(friend.firstName, friend.lastName),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = stringResource(R.string.friend_grid_active_tipps, item.activeEntryCount),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = JackpotGold,
                textAlign = TextAlign.Center
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .background(SurfaceElevated)
        ) {
            FriendGridStatCell(
                label = stringResource(R.string.friend_grid_all_tipps),
                value = item.totalTipps.toString(),
                modifier = Modifier.weight(1f)
            )
            VerticalDivider(
                modifier = Modifier.fillMaxHeight(),
                color = Divider.copy(alpha = 0.55f),
                thickness = 1.dp
            )
            FriendGridStatCell(
                label = stringResource(R.string.friend_grid_wins),
                value = item.winCount.toString(),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun FriendGridStatCell(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary,
            letterSpacing = 0.6.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
fun FriendRowActionSheet(
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    FormActionMenuSheet(
        title = stringResource(R.string.friend_actions_title),
        onDismiss = onDismiss,
        actions = listOf(
            FormActionMenuItem(
                label = stringResource(R.string.action_edit_friend),
                onClick = onEdit
            ),
            FormActionMenuItem(
                label = stringResource(R.string.action_delete_friend),
                onClick = onDelete,
                destructive = true
            )
        )
    )
}
