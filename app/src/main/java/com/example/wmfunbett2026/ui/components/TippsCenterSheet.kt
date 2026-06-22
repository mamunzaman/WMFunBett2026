package com.example.wmfunbett2026.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.ui.theme.GlassBorder
import com.example.wmfunbett2026.ui.theme.PrimaryBlue
import com.example.wmfunbett2026.ui.theme.PrimaryBlueBright
import com.example.wmfunbett2026.ui.theme.SheetSurface
import com.example.wmfunbett2026.ui.theme.Surface
import com.example.wmfunbett2026.ui.theme.TextPrimary
import com.example.wmfunbett2026.ui.theme.TextSecondary
import kotlinx.coroutines.delay

private val CreateMenuSheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
private val CreateMenuRowShape = RoundedCornerShape(16.dp)
private const val CreateMenuRowStaggerMs = 60
private const val CreateMenuRowDurationMs = 400
private val CreateMenuRowEntranceOffset = 24.dp

private enum class CreateMenuAction {
    Round,
    Match,
    TippGroup,
    Entry
}

private data class CreateMenuItem(
    val action: CreateMenuAction,
    val titleRes: Int,
    val descriptionRes: Int,
    val icon: ImageVector,
    val enabled: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TippsCenterActionSheet(
    onDismiss: () -> Unit,
    onRoundClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val menuItems = listOf(
        CreateMenuItem(
            action = CreateMenuAction.Round,
            titleRes = R.string.create_menu_round,
            descriptionRes = R.string.create_menu_round_description,
            icon = Icons.Outlined.EmojiEvents,
            enabled = true
        ),
        CreateMenuItem(
            action = CreateMenuAction.Match,
            titleRes = R.string.create_menu_match,
            descriptionRes = R.string.create_menu_match_description,
            icon = Icons.Outlined.SportsSoccer,
            enabled = false
        ),
        CreateMenuItem(
            action = CreateMenuAction.TippGroup,
            titleRes = R.string.create_menu_tipp_group,
            descriptionRes = R.string.create_menu_tipp_group_description,
            icon = Icons.Outlined.Groups,
            enabled = false
        ),
        CreateMenuItem(
            action = CreateMenuAction.Entry,
            titleRes = R.string.create_menu_entry,
            descriptionRes = R.string.create_menu_entry_description,
            icon = Icons.Outlined.EditNote,
            enabled = false
        )
    )

    DisposableEffect(Unit) {
        ModalSheetBackdropState.push()
        onDispose { ModalSheetBackdropState.pop() }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SheetSurface,
        contentColor = TextPrimary,
        scrimColor = ModalSheetScrimColor,
        shape = CreateMenuSheetShape,
        tonalElevation = 0.dp,
        dragHandle = { SheetDragHandle() },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = stringResource(R.string.create_menu_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
            )
            menuItems.forEachIndexed { index, item ->
                CreateMenuRowEntrance(staggerIndex = index) {
                    CreateMenuRow(
                        item = item,
                        onClick = {
                            if (item.enabled && item.action == CreateMenuAction.Round) {
                                onRoundClick()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateMenuRowEntrance(
    staggerIndex: Int,
    content: @Composable () -> Unit
) {
    var animateIn by remember { mutableStateOf(false) }
    val startOffsetPx = with(LocalDensity.current) { CreateMenuRowEntranceOffset.toPx() }

    LaunchedEffect(staggerIndex) {
        animateIn = false
        delay(staggerIndex * CreateMenuRowStaggerMs.toLong())
        animateIn = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0f,
        animationSpec = tween(
            durationMillis = CreateMenuRowDurationMs,
            easing = FastOutSlowInEasing
        ),
        label = "createMenuRowAlpha"
    )
    val offsetYPx by animateFloatAsState(
        targetValue = if (animateIn) 0f else startOffsetPx,
        animationSpec = tween(
            durationMillis = CreateMenuRowDurationMs,
            easing = FastOutSlowInEasing
        ),
        label = "createMenuRowOffset"
    )

    Box(
        modifier = Modifier.graphicsLayer {
            this.alpha = alpha
            translationY = offsetYPx
        }
    ) {
        content()
    }
}

@Composable
private fun CreateMenuRow(
    item: CreateMenuItem,
    onClick: () -> Unit
) {
    val titleColor = if (item.enabled) TextPrimary else TextPrimary.copy(alpha = 0.55f)
    val subtitle = if (item.enabled) {
        stringResource(item.descriptionRes)
    } else {
        stringResource(R.string.coming_soon)
    }
    val subtitleColor = if (item.enabled) TextSecondary else TextSecondary.copy(alpha = 0.65f)
    val iconTint = if (item.enabled) PrimaryBlueBright else TextSecondary.copy(alpha = 0.55f)
    val iconBackground = if (item.enabled) {
        PrimaryBlue.copy(alpha = 0.22f)
    } else {
        Surface.copy(alpha = 0.65f)
    }
    val chevronTint = if (item.enabled) TextSecondary else TextSecondary.copy(alpha = 0.35f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CreateMenuRowShape)
            .background(Surface)
            .border(1.dp, GlassBorder, CreateMenuRowShape)
            .clickable(
                enabled = item.enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = PrimaryBlueBright.copy(alpha = 0.12f)),
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(iconBackground)
                .border(1.dp, GlassBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = stringResource(item.titleRes),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = titleColor
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = subtitleColor
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
            contentDescription = null,
            tint = chevronTint,
            modifier = Modifier.size(22.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TippsSampleActionSheet(
    title: String,
    actions: List<String>,
    onDismiss: () -> Unit,
    onActionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    DisposableEffect(Unit) {
        ModalSheetBackdropState.push()
        onDispose { ModalSheetBackdropState.pop() }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SheetSurface,
        contentColor = TextPrimary,
        scrimColor = ModalSheetScrimColor,
        shape = CreateMenuSheetShape,
        tonalElevation = 0.dp,
        dragHandle = { SheetDragHandle() },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            actions.forEach { action ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onActionClick(action) }
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = action,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary
                    )
                    Text(
                        text = "›",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
internal fun SheetDragHandle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 40.dp, height = 4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(TextSecondary.copy(alpha = 0.42f))
        )
    }
}
