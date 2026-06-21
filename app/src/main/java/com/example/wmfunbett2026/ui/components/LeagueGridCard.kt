package com.example.wmfunbett2026.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import kotlinx.coroutines.delay
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.ripple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.ui.theme.GlassBorder
import com.example.wmfunbett2026.ui.theme.JackpotGold
import com.example.wmfunbett2026.ui.theme.PrimaryBlue
import com.example.wmfunbett2026.ui.theme.PrimaryBlueBright
import com.example.wmfunbett2026.ui.theme.Surface
import com.example.wmfunbett2026.ui.theme.TextPrimary
import com.example.wmfunbett2026.ui.theme.TextSecondary

private val LeagueGridCardShape = RoundedCornerShape(24.dp)
private const val LeagueCardEntranceDurationMs = 400
private const val LeagueCardEntranceStaggerMs = 85
private val LeagueCardEntranceOffset = 32.dp
private const val LeagueCardPressDurationMs = 120
private const val LeagueCardPressedScale = 0.97f
private val LeagueCardRestElevation = 8.dp
private val LeagueCardPressedElevation = 12.dp
private val LeagueCardPressedBorderColor = PrimaryBlueBright.copy(alpha = 0.48f)
private val LeagueCardRestSpotAlpha = 0.12f
private val LeagueCardPressedSpotAlpha = 0.28f

@Composable
fun LeagueGridCard(
    leagueId: String,
    name: String,
    matchCount: Int,
    activeMatchCount: Int,
    tippGroupCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cardHeight: Dp = 195.dp,
    staggerIndex: Int = 0
) {
    var animateIn by remember { mutableStateOf(false) }
    val startOffsetPx = with(LocalDensity.current) { LeagueCardEntranceOffset.toPx() }

    LaunchedEffect(staggerIndex) {
        animateIn = false
        delay(staggerIndex * LeagueCardEntranceStaggerMs.toLong())
        animateIn = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0f,
        animationSpec = tween(
            durationMillis = LeagueCardEntranceDurationMs,
            easing = FastOutSlowInEasing
        ),
        label = "leagueGridCardAlpha"
    )
    val offsetYPx by animateFloatAsState(
        targetValue = if (animateIn) 0f else startOffsetPx,
        animationSpec = tween(
            durationMillis = LeagueCardEntranceDurationMs,
            easing = FastOutSlowInEasing
        ),
        label = "leagueGridCardOffset"
    )

    val isCustom = leagueId == "custom-league"
    val visual = leagueGridVisual(leagueId)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressFloatSpec = tween<Float>(
        durationMillis = LeagueCardPressDurationMs,
        easing = FastOutSlowInEasing
    )
    val pressDpSpec = tween<Dp>(
        durationMillis = LeagueCardPressDurationMs,
        easing = FastOutSlowInEasing
    )

    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) LeagueCardPressedScale else 1f,
        animationSpec = pressFloatSpec,
        label = "leagueGridPressScale"
    )
    val elevation by animateDpAsState(
        targetValue = if (isPressed) LeagueCardPressedElevation else LeagueCardRestElevation,
        animationSpec = pressDpSpec,
        label = "leagueGridPressElevation"
    )
    val pressGlow by animateFloatAsState(
        targetValue = if (isPressed) 1f else 0f,
        animationSpec = pressFloatSpec,
        label = "leagueGridPressGlow"
    )
    val borderColor = lerp(GlassBorder, LeagueCardPressedBorderColor, pressGlow)
    val spotAlpha = LeagueCardRestSpotAlpha +
        (LeagueCardPressedSpotAlpha - LeagueCardRestSpotAlpha) * pressGlow

    Column(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                this.alpha = alpha
                translationY = offsetYPx
                scaleX = pressScale
                scaleY = pressScale
            }
            .height(cardHeight)
            .shadow(
                elevation = elevation,
                shape = LeagueGridCardShape,
                ambientColor = Color.Black.copy(alpha = 0.35f),
                spotColor = PrimaryBlue.copy(alpha = spotAlpha)
            )
            .clip(LeagueGridCardShape)
            .background(Surface)
            .border(1.dp, borderColor, LeagueGridCardShape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = PrimaryBlueBright.copy(alpha = 0.16f)),
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(
                    if (isCustom) {
                        PrimaryBlue.copy(alpha = 0.18f)
                    } else {
                        PrimaryBlue.copy(alpha = 0.24f)
                    }
                )
                .border(
                    width = 1.dp,
                    color = if (isCustom) PrimaryBlueBright.copy(alpha = 0.4f) else GlassBorder,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            when (visual) {
                is LeagueGridVisual.Icon -> {
                    Icon(
                        imageVector = visual.icon,
                        contentDescription = null,
                        tint = if (isCustom) PrimaryBlueBright else JackpotGold.copy(alpha = 0.95f),
                        modifier = Modifier.size(26.dp)
                    )
                }
                is LeagueGridVisual.Emoji -> {
                    Text(text = visual.emoji, fontSize = 24.sp, textAlign = TextAlign.Center)
                }
            }
        }

        Text(
            text = name,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = stringResource(R.string.league_grid_matches, matchCount),
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.league_grid_active, activeMatchCount),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryBlueBright,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.league_grid_tipps, tippGroupCount),
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

private sealed class LeagueGridVisual {
    data class Emoji(val emoji: String) : LeagueGridVisual()
    data class Icon(val icon: androidx.compose.ui.graphics.vector.ImageVector) : LeagueGridVisual()
}

private fun leagueGridVisual(leagueId: String): LeagueGridVisual {
    return when (leagueId) {
        "wc2026" -> LeagueGridVisual.Icon(Icons.Default.EmojiEvents)
        "bundesliga" -> LeagueGridVisual.Emoji("🇩🇪")
        "premier-league" -> LeagueGridVisual.Emoji("🏴")
        "la-liga" -> LeagueGridVisual.Emoji("🇪🇸")
        "champions-league" -> LeagueGridVisual.Emoji("⭐")
        "custom-league" -> LeagueGridVisual.Icon(Icons.Default.Add)
        else -> LeagueGridVisual.Icon(Icons.Default.EmojiEvents)
    }
}
