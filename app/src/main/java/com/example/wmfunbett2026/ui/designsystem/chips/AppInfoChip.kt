package com.example.wmfunbett2026.ui.designsystem.chips

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.example.wmfunbett2026.ui.designsystem.layout.ChipCornerRadius
import com.example.wmfunbett2026.ui.designsystem.layout.ChipHorizontalPadding
import com.example.wmfunbett2026.ui.designsystem.layout.ChipVerticalPadding
import com.example.wmfunbett2026.ui.theme.SecondaryText
import com.example.wmfunbett2026.ui.theme.WinnerGreen

@Composable
fun AppInfoChip(
    label: String,
    modifier: Modifier = Modifier,
    highlight: Boolean = false
) {
    val background = if (highlight) WinnerGreen.copy(alpha = 0.16f) else SecondaryText.copy(alpha = 0.14f)
    val textColor = if (highlight) WinnerGreen else SecondaryText
    Text(
        text = label,
        modifier = modifier
            .background(background, RoundedCornerShape(ChipCornerRadius))
            .padding(horizontal = ChipHorizontalPadding, vertical = ChipVerticalPadding),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Medium,
        color = textColor,
        textAlign = TextAlign.Center,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}
