package com.example.wmfunbett2026.ui.components

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.ui.navigation.AppScreen
import com.example.wmfunbett2026.ui.theme.DarkNavy
import com.example.wmfunbett2026.ui.theme.PrimaryBlue
import com.example.wmfunbett2026.ui.theme.PrimaryText
import com.example.wmfunbett2026.ui.theme.SecondaryText

private val NavPillWidth = 300.dp
private val NavPillHeight = 72.dp
private val NavPillRadius = 36.dp
private val NavPillShape = RoundedCornerShape(NavPillRadius)
private val CenterFabSize = 68.dp
private val FabOverlap = 34.dp

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
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            DarkNavy.copy(alpha = 0.82f),
                            DarkNavy.copy(alpha = 0.94f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = PrimaryText.copy(alpha = 0.24f),
                    shape = NavPillShape
                )
        ) {
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
                        color = if (selectedScreen == AppScreen.WM2026) PrimaryBlue else SecondaryText
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
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            PrimaryBlue,
                            Color(0xFF0F3D8C)
                        )
                    )
                )
                .border(
                    width = 1.5.dp,
                    color = PrimaryText.copy(alpha = 0.28f),
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
                tint = PrimaryText
            )
        }
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
    val tint = if (selected) PrimaryBlue else SecondaryText
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
