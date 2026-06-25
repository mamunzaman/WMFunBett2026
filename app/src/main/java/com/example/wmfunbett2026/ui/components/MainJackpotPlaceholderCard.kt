package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.ui.theme.DarkNavy
import com.example.wmfunbett2026.ui.theme.Divider
import com.example.wmfunbett2026.ui.theme.JackpotCardGradient
import com.example.wmfunbett2026.ui.theme.JackpotGold
import com.example.wmfunbett2026.ui.theme.PrimaryText
import com.example.wmfunbett2026.ui.theme.SecondaryText
import com.example.wmfunbett2026.ui.theme.Surface
import com.example.wmfunbett2026.ui.theme.TextMuted

private val MainJackpotCardShape = RoundedCornerShape(16.dp)
private val MainJackpotHorizontalPadding = 18.dp
private val MainJackpotIconSize = 72.dp
private val MainJackpotIconGap = 16.dp

@Composable
fun MainJackpotCard(
    amountText: String,
    descriptionText: String,
    statusText: String,
    modifier: Modifier = Modifier,
    titleText: String = stringResource(R.string.main_jackpot_title),
    isActive: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val borderColor = if (isActive) {
        JackpotGold.copy(alpha = 0.6f)
    } else {
        Divider.copy(alpha = 0.85f)
    }
    val titleColor = if (isActive) JackpotGold else TextMuted
    val amountColor = if (isActive) PrimaryText else SecondaryText
    val descriptionColor = if (isActive) SecondaryText else TextMuted
    val statusColor = if (isActive) SecondaryText else TextMuted
    val chevronColor = if (isActive) PrimaryText.copy(alpha = 0.72f) else TextMuted
    val footerDividerColor = if (isActive) {
        JackpotGold.copy(alpha = 0.2f)
    } else {
        Divider.copy(alpha = 0.55f)
    }
    val contentIndent = MainJackpotHorizontalPadding + MainJackpotIconSize + MainJackpotIconGap

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MainJackpotCardShape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .background(JackpotCardGradient)
            .background(DarkNavy.copy(alpha = if (isActive) 0.42f else 0.55f))
            .border(width = 1.dp, color = borderColor, shape = MainJackpotCardShape)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = MainJackpotHorizontalPadding,
                    end = MainJackpotHorizontalPadding,
                    top = 20.dp,
                    bottom = 18.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MainJackpotIconGap)
        ) {
            MainJackpotTrophyBadge(isActive = isActive)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = titleText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = amountText,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = amountColor
                )
                if (descriptionText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = descriptionText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = descriptionColor
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = chevronColor,
                modifier = Modifier.size(28.dp)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = MainJackpotHorizontalPadding)
        ) {
            Spacer(modifier = Modifier.width(contentIndent))
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = footerDividerColor,
                thickness = 1.dp
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = contentIndent,
                    end = MainJackpotHorizontalPadding,
                    top = 14.dp,
                    bottom = 16.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Savings,
                contentDescription = null,
                tint = if (isActive) JackpotGold else TextMuted,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = statusText,
                style = MaterialTheme.typography.bodyMedium,
                color = statusColor
            )
        }
    }
}

@Composable
private fun MainJackpotTrophyBadge(isActive: Boolean) {
    val ringColor = if (isActive) JackpotGold.copy(alpha = 0.75f) else Divider
    val fillColor = if (isActive) Surface.copy(alpha = 0.95f) else Surface.copy(alpha = 0.75f)
    val iconTint = if (isActive) JackpotGold else TextMuted
    val sparkleTint = if (isActive) JackpotGold.copy(alpha = 0.92f) else TextMuted.copy(alpha = 0.65f)

    Box(
        modifier = Modifier.size(MainJackpotIconSize),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(66.dp)
                .clip(CircleShape)
                .background(fillColor)
                .border(width = 2.dp, color = ringColor, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(38.dp)
            )
        }
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = sparkleTint,
            modifier = Modifier
                .size(12.dp)
                .align(Alignment.TopEnd)
                .offset(x = (-1).dp, y = 6.dp)
        )
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = sparkleTint.copy(alpha = sparkleTint.alpha * 0.75f),
            modifier = Modifier
                .size(9.dp)
                .align(Alignment.BottomStart)
                .offset(x = 8.dp, y = (-6).dp)
        )
    }
}

@Composable
fun MainJackpotPlaceholderCard(
    amountLabel: String?,
    modifier: Modifier = Modifier
) {
    MainJackpotCard(
        amountText = if (amountLabel != null) {
            stringResource(R.string.main_jackpot_amount_available, amountLabel)
        } else {
            stringResource(R.string.main_jackpot_carry_placeholder)
        },
        descriptionText = if (amountLabel != null) {
            stringResource(R.string.main_jackpot_description)
        } else {
            ""
        },
        statusText = stringResource(R.string.main_jackpot_coming_soon),
        isActive = true,
        modifier = modifier
    )
}
