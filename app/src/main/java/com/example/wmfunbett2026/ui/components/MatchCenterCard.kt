package com.example.wmfunbett2026.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.data.model.Game
import com.example.wmfunbett2026.data.model.MatchStatus
import com.example.wmfunbett2026.ui.matchcenter.MatchCardDisplayMode
import com.example.wmfunbett2026.ui.matchcenter.MatchCenterOutcomeBadge
import com.example.wmfunbett2026.ui.theme.PrimaryBlue
import com.example.wmfunbett2026.ui.theme.PrimaryBlueBright
import com.example.wmfunbett2026.ui.theme.TextPrimary
import com.example.wmfunbett2026.ui.theme.WinnerGreen
import kotlinx.coroutines.delay

internal const val MatchCardEntranceDurationMs = 400
internal const val MatchCardEntranceStaggerMs = 85
internal val MatchCardEntranceOffset = 32.dp
private const val MatchBadgeEntranceDurationMs = 220
private const val MatchBadgeEntranceStartScale = 0.94f

internal val LocalMatchCardBadgeEntranceDelayMs = staticCompositionLocalOf { 0 }

/** @see MatchCard */
@Composable
fun MatchCenterCard(
    game: Game,
    matchdayLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    staggerIndex: Int = 0,
    entranceSession: Int = 0,
    animateEntrance: Boolean = false
) {
    val badgeEntranceDelayMs = if (animateEntrance) {
        staggerIndex * MatchCardEntranceStaggerMs + MatchCardEntranceDurationMs
    } else {
        0
    }

    CompositionLocalProvider(LocalMatchCardBadgeEntranceDelayMs provides badgeEntranceDelayMs) {
        MatchCardEntranceHost(
            staggerIndex = staggerIndex,
            entranceSession = entranceSession,
            enabled = animateEntrance,
            modifier = modifier
        ) {
            MatchCard(
                game = game,
                matchdayLabel = matchdayLabel,
                onClick = onClick
            )
        }
    }
}

@Composable
private fun MatchCardEntranceHost(
    staggerIndex: Int,
    entranceSession: Int,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    if (!enabled) {
        Box(modifier = modifier) { content() }
        return
    }

    var animateIn by remember(entranceSession, staggerIndex) { mutableStateOf(false) }
    val startOffsetPx = with(LocalDensity.current) { MatchCardEntranceOffset.toPx() }

    LaunchedEffect(entranceSession, staggerIndex) {
        animateIn = false
        delay(staggerIndex * MatchCardEntranceStaggerMs.toLong())
        animateIn = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0f,
        animationSpec = tween(
            durationMillis = MatchCardEntranceDurationMs,
            easing = FastOutSlowInEasing
        ),
        label = "matchCardEntranceAlpha"
    )
    val offsetYPx by animateFloatAsState(
        targetValue = if (animateIn) 0f else startOffsetPx,
        animationSpec = tween(
            durationMillis = MatchCardEntranceDurationMs,
            easing = FastOutSlowInEasing
        ),
        label = "matchCardEntranceOffset"
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
fun MatchCenterMatchCardShell(
    modifier: Modifier = Modifier,
    game: Game? = null,
    displayMode: MatchCardDisplayMode = MatchCardDisplayMode.DETAIL,
    content: @Composable ColumnScope.() -> Unit
) {
    MatchCardShell(
        game = game,
        displayMode = displayMode,
        modifier = modifier,
        content = content
    )
}

@Composable
fun MatchCenterMatchCardBody(
    game: Game,
    matchdayLabel: String,
    modifier: Modifier = Modifier,
    bottomMetaOverride: String? = null,
    displayMode: MatchCardDisplayMode = MatchCardDisplayMode.DETAIL
) {
    MatchCardContent(
        game = game,
        matchdayLabel = matchdayLabel,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 16.dp),
        bottomMetaOverride = bottomMetaOverride,
        displayMode = displayMode
    )
}

@Composable
fun MatchStatusPill(badge: MatchCenterOutcomeBadge?) {
    if (badge == null) return

    when (badge) {
        MatchCenterOutcomeBadge.LIVE -> MatchStatusBadge(
            status = MatchStatus.LIVE,
            style = MatchStatusBadgeStyle.Pill
        )
        else -> {
            val (background, text, labelRes) = when (badge) {
                MatchCenterOutcomeBadge.ACTIVE -> Triple(
                    PrimaryBlue.copy(alpha = 0.35f),
                    TextPrimary,
                    R.string.badge_active
                )
                MatchCenterOutcomeBadge.UPCOMING -> Triple(
                    PrimaryBlue.copy(alpha = 0.22f),
                    PrimaryBlueBright,
                    R.string.status_upcoming
                )
                MatchCenterOutcomeBadge.NO_WINNER,
                MatchCenterOutcomeBadge.FINISHED -> Triple(
                    WinnerGreen.copy(alpha = 0.22f),
                    WinnerGreen,
                    R.string.status_finished
                )
                MatchCenterOutcomeBadge.LIVE -> error("Handled above")
            }

            val pill = @Composable {
                Text(
                    text = stringResource(labelRes),
                    modifier = Modifier
                        .background(background, RoundedCornerShape(999.dp))
                        .padding(horizontal = 12.dp, vertical = 5.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = text
                )
            }

            if (badge == MatchCenterOutcomeBadge.UPCOMING) {
                SubtleBadgeEntrance(content = pill)
            } else {
                pill()
            }
        }
    }
}

@Composable
private fun SubtleBadgeEntrance(content: @Composable () -> Unit) {
    val entranceDelayMs = LocalMatchCardBadgeEntranceDelayMs.current
    if (entranceDelayMs <= 0) {
        content()
        return
    }

    var animateIn by remember(entranceDelayMs) { mutableStateOf(false) }

    LaunchedEffect(entranceDelayMs) {
        animateIn = false
        delay(entranceDelayMs.toLong())
        animateIn = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0f,
        animationSpec = tween(
            durationMillis = MatchBadgeEntranceDurationMs,
            easing = FastOutSlowInEasing
        ),
        label = "matchBadgeEntranceAlpha"
    )
    val scale by animateFloatAsState(
        targetValue = if (animateIn) 1f else MatchBadgeEntranceStartScale,
        animationSpec = tween(
            durationMillis = MatchBadgeEntranceDurationMs,
            easing = FastOutSlowInEasing
        ),
        label = "matchBadgeEntranceScale"
    )

    Box(
        modifier = Modifier.graphicsLayer {
            this.alpha = alpha
            scaleX = scale
            scaleY = scale
        }
    ) {
        content()
    }
}
