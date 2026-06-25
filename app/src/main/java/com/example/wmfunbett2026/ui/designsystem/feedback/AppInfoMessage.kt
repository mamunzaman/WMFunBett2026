package com.example.wmfunbett2026.ui.designsystem.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.ui.designsystem.layout.CardCornerRadius
import com.example.wmfunbett2026.ui.designsystem.layout.DefaultCardPadding
import com.example.wmfunbett2026.ui.designsystem.layout.IconButtonSize
import com.example.wmfunbett2026.ui.theme.DangerRed
import com.example.wmfunbett2026.ui.theme.JackpotGold
import com.example.wmfunbett2026.ui.theme.PrimaryBlue
import com.example.wmfunbett2026.ui.theme.PrimaryBlueBright
import com.example.wmfunbett2026.ui.theme.PrimaryText
import com.example.wmfunbett2026.ui.theme.SecondaryText
import com.example.wmfunbett2026.ui.theme.TextSecondary

enum class AppInfoMessageStyle {
    Info,
    Warning,
    Accent
}

@Composable
fun AppInfoMessage(
    title: String,
    message: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    style: AppInfoMessageStyle = AppInfoMessageStyle.Info
) {
    val backgroundColor = when (style) {
        AppInfoMessageStyle.Info -> PrimaryBlue.copy(alpha = 0.14f)
        AppInfoMessageStyle.Warning -> DangerRed.copy(alpha = 0.12f)
        AppInfoMessageStyle.Accent -> JackpotGold.copy(alpha = 0.12f)
    }
    val borderColor = when (style) {
        AppInfoMessageStyle.Info -> PrimaryBlue.copy(alpha = 0.34f)
        AppInfoMessageStyle.Warning -> DangerRed.copy(alpha = 0.35f)
        AppInfoMessageStyle.Accent -> JackpotGold.copy(alpha = 0.38f)
    }
    val iconBackground = when (style) {
        AppInfoMessageStyle.Info -> PrimaryBlue.copy(alpha = 0.24f)
        AppInfoMessageStyle.Warning -> DangerRed.copy(alpha = 0.2f)
        AppInfoMessageStyle.Accent -> JackpotGold.copy(alpha = 0.2f)
    }
    val iconTint = when (style) {
        AppInfoMessageStyle.Info -> PrimaryBlueBright
        AppInfoMessageStyle.Warning -> DangerRed
        AppInfoMessageStyle.Accent -> JackpotGold
    }
    val shape = RoundedCornerShape(CardCornerRadius)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(backgroundColor)
            .border(1.dp, borderColor, shape)
            .padding(DefaultCardPadding),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(IconButtonSize)
                .clip(CircleShape)
                .background(iconBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = PrimaryText
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}
