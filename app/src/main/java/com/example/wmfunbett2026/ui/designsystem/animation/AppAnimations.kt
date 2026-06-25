package com.example.wmfunbett2026.ui.designsystem.animation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntSize
import com.example.wmfunbett2026.ui.designsystem.tokens.AppTokens

fun appPressTween(
    durationMillis: Int = AppTokens.PressAnimationDuration
): TweenSpec<Float> = tween(durationMillis = durationMillis, easing = FastOutSlowInEasing)

fun appExpandTween(
    durationMillis: Int = AppTokens.DefaultAnimationDuration
): TweenSpec<IntSize> = tween(durationMillis = durationMillis, easing = FastOutSlowInEasing)

fun appFadeTween(
    durationMillis: Int = AppTokens.FadeAnimationDuration
): TweenSpec<Float> = tween(durationMillis = durationMillis, easing = FastOutSlowInEasing)

@Composable
fun rememberAppPressScale(isPressed: Boolean, enabled: Boolean = true): Float {
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) AppTokens.PressScale else 1f,
        animationSpec = appPressTween(),
        label = "appPressScale"
    )
    return scale
}

fun Modifier.appPressGraphicsLayer(scale: Float): Modifier = graphicsLayer {
    scaleX = scale
    scaleY = scale
}
