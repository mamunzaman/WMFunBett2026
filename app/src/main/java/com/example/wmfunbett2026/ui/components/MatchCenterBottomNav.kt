package com.example.wmfunbett2026.ui.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.ui.navigation.AppScreen
import com.example.wmfunbett2026.ui.theme.DarkNavy
import com.example.wmfunbett2026.ui.theme.FabGradient
import com.example.wmfunbett2026.ui.theme.GlassBorder
import com.example.wmfunbett2026.ui.theme.PrimaryBlue
import com.example.wmfunbett2026.ui.theme.TextPrimary
import com.example.wmfunbett2026.ui.theme.TextSecondary

private val NavPillWidth = 300.dp
private val NavPillHeight = 72.dp
private val NavPillRadius = 36.dp
private val NavPillShape = RoundedCornerShape(NavPillRadius)
private val CenterFabSize = 68.dp
private val FabOverlap = 34.dp
private val NavPillBlurRadius = 24.dp
private val NavPillTintAlpha = 0.58f
private val NavPillFallbackAlpha = 0.72f

/** Space to reserve in scroll content so items clear the floating nav. */
val MatchCenterBottomNavReservedHeight = 148.dp

@Composable
fun MatchCenterBottomNav(
    selectedScreen: AppScreen,
    onScreenSelected: (AppScreen) -> Unit,
    onCenterAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tippsLabel = stringResource(R.string.nav_tipps)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .width(NavPillWidth)
                .height(NavPillHeight)
                .clip(NavPillShape)
                .border(
                    width = 1.dp,
                    color = GlassBorder,
                    shape = NavPillShape
                )
        ) {
            NavGlassPillBackground(modifier = Modifier.fillMaxSize())
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                BottomNavItem(
                    label = stringResource(R.string.nav_home),
                    icon = Icons.Default.Home,
                    selected = selectedScreen == AppScreen.Home,
                    onClick = { onScreenSelected(AppScreen.Home) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 10.dp)
                )
                Box(
                    modifier = Modifier
                        .width(76.dp)
                        .padding(bottom = 8.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Text(
                        text = tippsLabel,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (selectedScreen == AppScreen.WM2026) {
                            FontWeight.SemiBold
                        } else {
                            FontWeight.Normal
                        },
                        color = if (selectedScreen == AppScreen.WM2026) PrimaryBlue else TextSecondary
                    )
                }
                BottomNavItem(
                    label = stringResource(R.string.nav_settings),
                    icon = Icons.Default.Settings,
                    selected = selectedScreen == AppScreen.Settings,
                    onClick = { onScreenSelected(AppScreen.Settings) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 10.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .offset(y = -(NavPillHeight - FabOverlap))
                .size(CenterFabSize)
                .clip(CircleShape)
                .background(FabGradient)
                .border(
                    width = 1.5.dp,
                    color = GlassBorder,
                    shape = CircleShape
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onCenterAddClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.add_action),
                modifier = Modifier.size(32.dp),
                tint = TextPrimary
            )
        }
    }
}

@Composable
private fun NavGlassPillBackground(modifier: Modifier = Modifier) {
    val glassModifier = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Modifier.blur(
            radius = NavPillBlurRadius,
            edgeTreatment = BlurredEdgeTreatment.Rectangle
        )
    } else {
        Modifier
    }
    Box(
        modifier = modifier
            .then(glassModifier)
            .background(DarkNavy.copy(alpha = navGlassAlpha()))
    )
}

private fun navGlassAlpha(): Float {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        NavPillTintAlpha
    } else {
        NavPillFallbackAlpha
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tint = if (selected) PrimaryBlue else TextSecondary
    Column(
        modifier = modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = tint
        )
    }
}
