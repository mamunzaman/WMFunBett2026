package com.example.wmfunbett2026.ui.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.ui.theme.SheetBackdropOverlay

/** Tracks open modal sheets so the app shell can blur/dim content behind them. */
object ModalSheetBackdropState {
    private val openCount = mutableIntStateOf(0)

    val isActive: Boolean
        @Composable
        get() = openCount.intValue > 0

    internal fun push() {
        openCount.intValue++
    }

    internal fun pop() {
        if (openCount.intValue > 0) {
            openCount.intValue--
        }
    }
}

private val BackdropBlurRadius = 28.dp

@Composable
fun Modifier.modalSheetBackdropBlur(active: Boolean): Modifier {
    if (!active) return this
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        blur(
            radius = BackdropBlurRadius,
            edgeTreatment = BlurredEdgeTreatment.Unbounded
        )
    } else {
        this
    }
}

@Composable
fun ModalSheetBackdropOverlay(active: Boolean, modifier: Modifier = Modifier) {
    if (!active) return
    val overlayAlpha = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        SheetBackdropOverlay
    } else {
        SheetBackdropOverlay.copy(alpha = SheetBackdropOverlay.alpha + 0.12f)
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(overlayAlpha)
    )
}

/** Transparent — backdrop blur/dim is handled by [ModalSheetBackdropOverlay] in the app shell. */
val ModalSheetScrimColor = Color.Transparent
