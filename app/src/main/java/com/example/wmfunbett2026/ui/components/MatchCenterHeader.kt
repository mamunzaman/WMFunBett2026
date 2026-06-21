package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.ui.theme.DangerRed
import com.example.wmfunbett2026.ui.theme.GlassBorder
import com.example.wmfunbett2026.ui.theme.PremiumHeaderGradient
import com.example.wmfunbett2026.ui.theme.PrimaryBlue
import com.example.wmfunbett2026.ui.theme.TextPrimary
import com.example.wmfunbett2026.ui.theme.TextSecondary

@Composable
fun MatchCenterHeader(
    title: String,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    showSearchIcon: Boolean = false,
    matchdayFilterLabel: String? = null,
    onMatchdayFilterClick: (() -> Unit)? = null,
    matchdayFilterMenuExpanded: Boolean = false,
    onMatchdayFilterDismiss: () -> Unit = {},
    matchdayFilterOptions: List<String> = emptyList(),
    onMatchdayFilterOptionSelected: (Int) -> Unit = {},
    onSetResultClick: (() -> Unit)? = null,
    onWinnerShareSettingsClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    deleteEnabled: Boolean = true
) {
    var showMenu by remember { mutableStateOf(false) }
    val hasMenu = onSetResultClick != null ||
        onWinnerShareSettingsClick != null ||
        onDeleteClick != null

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
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                }
                else -> Spacer(modifier = Modifier.width(16.dp))
            }

            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (matchdayFilterLabel != null && onMatchdayFilterClick != null) {
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(PrimaryBlue.copy(alpha = 0.28f))
                            .border(1.dp, GlassBorder, RoundedCornerShape(999.dp))
                            .clickable(onClick = onMatchdayFilterClick)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = matchdayFilterLabel,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = TextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = matchdayFilterMenuExpanded,
                        onDismissRequest = onMatchdayFilterDismiss
                    ) {
                        matchdayFilterOptions.forEachIndexed { index, label ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = { onMatchdayFilterOptionSelected(index) }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(4.dp))
            }

            if (showSearchIcon) {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TextPrimary
                    )
                }
            } else if (hasMenu) {
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = TextPrimary
                        )
                    }
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
            } else {
                Spacer(modifier = Modifier.width(16.dp))
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
