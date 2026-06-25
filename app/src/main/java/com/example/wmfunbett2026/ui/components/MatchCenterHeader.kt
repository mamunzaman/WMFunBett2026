package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import com.example.wmfunbett2026.ui.designsystem.buttons.AppIconButton
import com.example.wmfunbett2026.ui.designsystem.buttons.AppMenuButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.ui.theme.DangerRed
import com.example.wmfunbett2026.ui.theme.PremiumHeaderGradient
import com.example.wmfunbett2026.ui.theme.TextPrimary
import com.example.wmfunbett2026.ui.theme.TextSecondary

@Composable
fun MatchCenterHeader(
    title: String,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    showSearchIcon: Boolean = false,
    jackpotAmountLabel: String? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    onSetResultClick: (() -> Unit)? = null,
    onWinnerShareSettingsClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    deleteEnabled: Boolean = true
) {
    var showMenu by remember { mutableStateOf(false) }
    val hasMenu = onSetResultClick != null ||
        onWinnerShareSettingsClick != null ||
        onDeleteClick != null
    val showJackpot = !jackpotAmountLabel.isNullOrBlank()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(PremiumHeaderGradient)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(64.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when {
                onBackClick != null -> {
                    AppIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        onClick = onBackClick,
                        filled = false,
                        iconTint = TextPrimary
                    )
                }
                else -> Spacer(modifier = Modifier.width(16.dp))
            }

            Text(
                text = title,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = if (showJackpot || trailingContent != null || showSearchIcon || hasMenu) {
                        HeaderActionSpacing
                    } else {
                        0.dp
                    }),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            when {
                trailingContent != null || showJackpot || showSearchIcon || hasMenu -> {
                    Row(
                        modifier = Modifier.padding(end = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(HeaderActionSpacing)
                    ) {
                        if (showJackpot) {
                            HeaderJackpotAction(amountLabel = jackpotAmountLabel!!)
                        }
                        when {
                            trailingContent != null -> trailingContent()
                            showSearchIcon -> {
                                AppIconButton(
                                    icon = Icons.Default.Search,
                                    contentDescription = stringResource(R.string.search),
                                    onClick = { },
                                    filled = false,
                                    iconTint = TextPrimary
                                )
                            }
                            hasMenu -> {
                                Box {
                                    AppMenuButton(
                                        onClick = { showMenu = true },
                                        filled = false,
                                        iconTint = TextPrimary
                                    )
                                    DropdownMenu(
                                        expanded = showMenu,
                                        onDismissRequest = { showMenu = false }
                                    ) {
                                        if (onWinnerShareSettingsClick != null) {
                                            DropdownMenuItem(
                                                text = { Text(stringResource(R.string.winner_share_settings)) },
                                                onClick = {
                                                    showMenu = false
                                                    onWinnerShareSettingsClick()
                                                }
                                            )
                                        }
                                        if (onSetResultClick != null) {
                                            DropdownMenuItem(
                                                text = { Text(stringResource(R.string.set_result)) },
                                                onClick = {
                                                    showMenu = false
                                                    onSetResultClick()
                                                }
                                            )
                                        }
                                        if (onDeleteClick != null && deleteEnabled) {
                                            DropdownMenuItem(
                                                text = { Text(stringResource(R.string.delete), color = DangerRed) },
                                                onClick = {
                                                    showMenu = false
                                                    onDeleteClick()
                                                }
                                            )
                                        } else if (onDeleteClick != null) {
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        text = stringResource(R.string.delete_entries_first),
                                                        color = TextSecondary
                                                    )
                                                },
                                                onClick = { showMenu = false },
                                                enabled = false
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else -> Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}

@Composable
fun MatchCenterBreadcrumb(
    breadcrumbs: List<String>,
    modifier: Modifier = Modifier
) {
    if (breadcrumbs.size <= 1) return
    Text(
        text = breadcrumbs.joinToString("  ›  "),
        modifier = modifier
            .fillMaxWidth()
            .background(PremiumHeaderGradient)
            .padding(horizontal = 20.dp)
            .padding(bottom = 10.dp),
        style = MaterialTheme.typography.labelMedium,
        color = TextPrimary.copy(alpha = 0.82f),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}
