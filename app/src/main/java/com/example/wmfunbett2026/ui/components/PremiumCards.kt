package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.data.model.Game
import com.example.wmfunbett2026.data.model.MatchStatus
import com.example.wmfunbett2026.data.model.Round
import com.example.wmfunbett2026.data.model.toEuroLabel
import com.example.wmfunbett2026.ui.matchcenter.MatchCardDisplayMode
import com.example.wmfunbett2026.ui.matchcenter.MatchCardWinnerSide
import com.example.wmfunbett2026.ui.matchcenter.MatchCenterOutcomeBadge
import com.example.wmfunbett2026.ui.matchcenter.centerScoreText
import com.example.wmfunbett2026.ui.matchcenter.isMatchCardExpandedLayout
import com.example.wmfunbett2026.ui.matchcenter.matchCardSurfaceColor
import com.example.wmfunbett2026.ui.matchcenter.matchCardWinnerSide
import com.example.wmfunbett2026.ui.matchcenter.primaryTippLabel
import com.example.wmfunbett2026.ui.matchcenter.resolveMatchStatusBadge
import com.example.wmfunbett2026.ui.matchcenter.shouldUseDetailUpcomingLayout
import com.example.wmfunbett2026.ui.matchcenter.teamFlagEmoji
import com.example.wmfunbett2026.ui.theme.DangerRed
import com.example.wmfunbett2026.ui.theme.Divider
import com.example.wmfunbett2026.ui.theme.GlassBorder
import com.example.wmfunbett2026.ui.theme.JackpotCardGradient
import com.example.wmfunbett2026.ui.theme.JackpotGold
import com.example.wmfunbett2026.ui.theme.MatchCardCompactSurface
import com.example.wmfunbett2026.ui.theme.PrimaryBlueBright
import com.example.wmfunbett2026.ui.theme.PrimaryBlue
import com.example.wmfunbett2026.ui.theme.TextMuted
import com.example.wmfunbett2026.ui.theme.TextPrimary
import com.example.wmfunbett2026.ui.theme.TextSecondary
import com.example.wmfunbett2026.ui.theme.WinnerGreen
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.HorizontalDivider

private val PremiumCardShape = RoundedCornerShape(20.dp)

@Composable
fun MatchCardShell(
    modifier: Modifier = Modifier,
    game: Game? = null,
    displayMode: MatchCardDisplayMode = MatchCardDisplayMode.LIST,
    content: @Composable ColumnScope.() -> Unit
) {
    val surfaceColor = game?.let { matchCardSurfaceColor(it, displayMode) } ?: MatchCardCompactSurface
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = PremiumCardShape, ambientColor = Color.Black.copy(0.35f))
            .clip(PremiumCardShape)
            .background(surfaceColor)
            .border(width = 1.dp, color = GlassBorder, shape = PremiumCardShape),
        content = content
    )
}

@Composable
private fun MatchCardContainer(
    game: Game,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = PremiumCardShape, ambientColor = Color.Black.copy(0.35f))
            .clip(PremiumCardShape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .background(matchCardSurfaceColor(game, MatchCardDisplayMode.LIST))
            .border(width = 1.dp, color = GlassBorder, shape = PremiumCardShape)
            .padding(16.dp),
        content = content
    )
}

@Composable
fun PremiumCard(
    modifier: Modifier = Modifier,
    gradient: Brush? = null,
    borderColor: Color = Divider,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = PremiumCardShape
    Column(
        modifier = modifier
            .shadow(elevation = 6.dp, shape = shape, ambientColor = Color.Black.copy(0.35f))
            .clip(shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .then(
                if (gradient != null) {
                    Modifier.background(gradient)
                } else {
                    Modifier.background(com.example.wmfunbett2026.ui.theme.Surface)
                }
            )
            .border(width = 1.dp, color = borderColor, shape = shape)
            .padding(16.dp),
        content = content
    )
}

@Composable
fun JackpotSummaryCard(
    jackpotAmount: String,
    openRoundsCount: Int,
    activeGamesCount: Int,
    modifier: Modifier = Modifier
) {
    PremiumCard(
        modifier = modifier.fillMaxWidth(),
        gradient = JackpotCardGradient,
        borderColor = GlassBorder
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.total_jackpot),
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary
                )
                Text(
                    text = jackpotAmount,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = JackpotGold
                )
            }
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = JackpotGold.copy(alpha = 0.35f),
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            JackpotStatChip(
                icon = {
                    Icon(
                        Icons.Outlined.HourglassEmpty,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                },
                label = stringResource(R.string.open_rounds_pill, openRoundsCount),
                modifier = Modifier.weight(1f)
            )
            JackpotStatChip(
                icon = {
                    Icon(
                        Icons.Outlined.SportsSoccer,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                },
                label = stringResource(R.string.active_games_pill, activeGamesCount),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun JackpotStatChip(
    icon: @Composable () -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(TextPrimary.copy(alpha = 0.06f))
            .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        icon()
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchCard(
    game: Game,
    matchdayLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    MatchCardContainer(
        game = game,
        onClick = onClick,
        modifier = modifier
    ) {
        MatchCardContent(game = game, matchdayLabel = matchdayLabel)
    }
}

@Composable
fun MatchCardContent(
    game: Game,
    matchdayLabel: String,
    modifier: Modifier = Modifier,
    bottomMetaOverride: String? = null,
    displayMode: MatchCardDisplayMode = MatchCardDisplayMode.LIST
) {
    when {
        shouldUseDetailUpcomingLayout(game, displayMode) -> {
            DetailUpcomingMatchCardContent(
                game = game,
                matchdayLabel = matchdayLabel,
                modifier = modifier
            )
        }
        isMatchCardExpandedLayout(game) -> {
            ExpandedActiveMatchCardContent(
                game = game,
                matchdayLabel = matchdayLabel,
                modifier = modifier,
                bottomMetaOverride = bottomMetaOverride,
                displayMode = displayMode
            )
        }
        else -> {
            CompactMatchCardContent(
                game = game,
                matchdayLabel = matchdayLabel,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun DetailUpcomingMatchCardContent(
    game: Game,
    matchdayLabel: String,
    modifier: Modifier = Modifier
) {
    val statusBadge = resolveMatchStatusBadge(game, MatchCardDisplayMode.DETAIL)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MatchdayBadge(label = matchdayLabel)
            MatchStatusPill(badge = statusBadge)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "${teamFlagEmoji(game.teamA)} ${game.teamA}  vs  ${teamFlagEmoji(game.teamB)} ${game.teamB}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(16.dp)
            )
            Text(
                text = game.dateTimeLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = matchdayLabel,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )

        if (game.status == MatchStatus.FINISHED && game.hasResult) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = game.centerScoreText(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun ExpandedActiveMatchCardContent(
    game: Game,
    matchdayLabel: String,
    modifier: Modifier = Modifier,
    bottomMetaOverride: String? = null,
    displayMode: MatchCardDisplayMode = MatchCardDisplayMode.LIST
) {
    val outcomeBadge = resolveMatchStatusBadge(game, displayMode)
    val tippLabel = game.primaryTippLabel()
    val amountLabel = game.totalKasse.toEuroLabel()
    val winnerSide = game.matchCardWinnerSide()

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MatchdayBadge(label = matchdayLabel)
            MatchStatusPill(badge = outcomeBadge)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ActiveTeamRow(
                    teamName = game.teamA,
                    highlighted = winnerSide == MatchCardWinnerSide.TEAM_A
                )
                ActiveTeamRow(
                    teamName = game.teamB,
                    highlighted = winnerSide == MatchCardWinnerSide.TEAM_B
                )
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.match_card_scoreline),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = TextMuted,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = game.centerScoreText(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = GlassBorder, thickness = 1.dp)
        Spacer(modifier = Modifier.height(12.dp))

        if (bottomMetaOverride != null) {
            Text(
                text = bottomMetaOverride,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (tippLabel != null) {
                        Text(
                            text = tippLabel,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = " · ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                    Text(
                        text = amountLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = JackpotGold
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .size(16.dp)
                    )
                    Text(
                        text = game.dateTimeLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun CompactMatchCardContent(
    game: Game,
    matchdayLabel: String,
    modifier: Modifier = Modifier
) {
    val isFinished = game.status == MatchStatus.FINISHED
    val winnerSide = game.matchCardWinnerSide()
    val titleColor = if (isFinished) TextPrimary else PrimaryBlueBright

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        OverlappingTeamFlags(
            teamA = game.teamA,
            teamB = game.teamB,
            winnerSide = if (isFinished) winnerSide else null
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = game.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = titleColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = game.dateTimeLabel,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            if (isFinished && game.hasResult) {
                Text(
                    text = game.centerScoreText(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            Text(
                text = matchdayLabel,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun ActiveTeamRow(
    teamName: String,
    highlighted: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(TextPrimary.copy(alpha = 0.08f))
                .then(
                    if (highlighted) {
                        Modifier.border(1.dp, WinnerGreen.copy(alpha = 0.55f), CircleShape)
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = teamFlagEmoji(teamName),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }
        Text(
            text = teamName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (highlighted) FontWeight.Bold else FontWeight.SemiBold,
            color = if (highlighted) WinnerGreen.copy(alpha = 0.95f) else TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun OverlappingTeamFlags(
    teamA: String,
    teamB: String,
    winnerSide: MatchCardWinnerSide?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(width = 54.dp, height = 36.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        TeamFlagCircle(
            flag = teamFlagEmoji(teamA),
            highlighted = winnerSide == MatchCardWinnerSide.TEAM_A,
            modifier = Modifier.align(Alignment.CenterStart)
        )
        TeamFlagCircle(
            flag = teamFlagEmoji(teamB),
            highlighted = winnerSide == MatchCardWinnerSide.TEAM_B,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = 22.dp)
        )
    }
}

@Composable
private fun TeamFlagCircle(
    flag: String,
    highlighted: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(TextPrimary.copy(alpha = 0.08f))
            .border(
                width = if (highlighted) 1.5.dp else 1.dp,
                color = if (highlighted) WinnerGreen.copy(alpha = 0.6f) else GlassBorder,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = flag,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MatchdayBadge(label: String) {
    Text(
        text = label,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(PrimaryBlue.copy(alpha = 0.32f))
            .padding(horizontal = 12.dp, vertical = 5.dp),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = TextPrimary
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentCard(
    round: Round,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    PremiumCard(
        modifier = modifier.fillMaxWidth(),
        gradient = Brush.verticalGradient(
            colors = listOf(
                com.example.wmfunbett2026.ui.theme.SurfaceVariant,
                com.example.wmfunbett2026.ui.theme.SurfaceVariant.copy(alpha = 0.92f)
            )
        ),
        borderColor = Divider,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue.copy(alpha = 0.35f))
                    .border(1.dp, PrimaryBlue.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = JackpotGold.copy(alpha = 0.9f),
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = round.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = subtitle ?: round.note.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = TextSecondary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeagueSummaryCard(
    name: String,
    matchCount: Int,
    activeMatchCount: Int,
    tippGroupCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    PremiumCard(
        modifier = modifier.fillMaxWidth(),
        gradient = Brush.verticalGradient(
            colors = listOf(
                com.example.wmfunbett2026.ui.theme.SurfaceVariant,
                com.example.wmfunbett2026.ui.theme.SurfaceVariant.copy(alpha = 0.92f)
            )
        ),
        borderColor = Divider,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue.copy(alpha = 0.35f))
                    .border(1.dp, PrimaryBlue.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = JackpotGold.copy(alpha = 0.9f),
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = stringResource(
                        R.string.league_card_stats,
                        matchCount,
                        activeMatchCount,
                        tippGroupCount
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = TextSecondary
                )
            }
        }
    }
}
