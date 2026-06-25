package com.example.wmfunbett2026.ui.designsystem.chips

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.example.wmfunbett2026.ui.designsystem.layout.ChipCornerRadius
import com.example.wmfunbett2026.ui.designsystem.layout.ChipHorizontalPadding
import com.example.wmfunbett2026.ui.designsystem.layout.ChipVerticalPadding
import com.example.wmfunbett2026.ui.theme.JackpotGold

@Composable
fun AppAmountChip(
    amount: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = amount,
        modifier = modifier
            .background(JackpotGold.copy(alpha = 0.14f), RoundedCornerShape(ChipCornerRadius))
            .padding(horizontal = ChipHorizontalPadding, vertical = ChipVerticalPadding),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = JackpotGold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}
