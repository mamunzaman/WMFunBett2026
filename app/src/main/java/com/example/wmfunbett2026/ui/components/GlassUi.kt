package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.ui.theme.DarkNavy
import com.example.wmfunbett2026.ui.theme.JackpotGold
import com.example.wmfunbett2026.ui.theme.PrimaryBlue
import com.example.wmfunbett2026.ui.theme.PrimaryText
import com.example.wmfunbett2026.ui.theme.SecondaryText
import com.example.wmfunbett2026.ui.theme.SurfaceDark

private val GlassShape = RoundedCornerShape(18.dp)
private val GlassChipShape = RoundedCornerShape(12.dp)
private val EntryGlassShape = RoundedCornerShape(16.dp)

@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = GlassShape,
    highlight: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val borderColor = if (highlight) {
        JackpotGold.copy(alpha = 0.45f)
    } else {
        PrimaryText.copy(alpha = 0.16f)
    }
    Column(
        modifier = modifier
            .clip(shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SurfaceDark.copy(alpha = if (highlight) 0.78f else 0.68f),
                        DarkNavy.copy(alpha = 0.88f)
                    )
                )
            )
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        PrimaryBlue.copy(alpha = 0.12f),
                        PrimaryBlue.copy(alpha = 0.02f)
                    )
                )
            )
            .border(width = 1.dp, color = borderColor, shape = shape)
    ) {
        content()
    }
}

@Composable
fun GlassScopePill(
    label: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = label,
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(PrimaryBlue.copy(alpha = 0.28f))
            .border(1.dp, PrimaryBlue.copy(alpha = 0.4f), RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 5.dp),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = PrimaryText
    )
}

@Composable
fun GlassStatChip(
    label: String,
    value: String,
    highlight: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(GlassChipShape)
            .background(
                brush = Brush.verticalGradient(
                    colors = if (highlight) {
                        listOf(
                            JackpotGold.copy(alpha = 0.14f),
                            JackpotGold.copy(alpha = 0.06f)
                        )
                    } else {
                        listOf(
                            PrimaryText.copy(alpha = 0.08f),
                            PrimaryText.copy(alpha = 0.03f)
                        )
                    }
                )
            )
            .border(
                width = 1.dp,
                color = if (highlight) JackpotGold.copy(alpha = 0.35f) else PrimaryText.copy(alpha = 0.12f),
                shape = GlassChipShape
            )
            .padding(horizontal = 10.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = SecondaryText
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = if (highlight) JackpotGold else PrimaryText
        )
    }
}

@Composable
fun GlassEntryCard(
    name: String,
    prediction: String,
    amountLabel: String,
    statusLabel: String,
    note: String? = null,
    adjustmentNotice: String? = null,
    isWinner: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isWinner) JackpotGold.copy(alpha = 0.7f) else PrimaryText.copy(alpha = 0.14f)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(EntryGlassShape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isWinner) {
                        listOf(
                            JackpotGold.copy(alpha = 0.16f),
                            SurfaceDark.copy(alpha = 0.72f)
                        )
                    } else {
                        listOf(
                            SurfaceDark.copy(alpha = 0.62f),
                            DarkNavy.copy(alpha = 0.82f)
                        )
                    }
                )
            )
            .border(width = if (isWinner) 1.5.dp else 1.dp, color = borderColor, shape = EntryGlassShape)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = PrimaryText
            )
            DetailStatusChip(label = statusLabel)
        }
        Text(
            text = prediction,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryText.copy(alpha = 0.94f)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Amount paid",
                style = MaterialTheme.typography.bodySmall,
                color = SecondaryText
            )
            Text(
                text = amountLabel,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = JackpotGold
            )
        }
        if (!adjustmentNotice.isNullOrBlank()) {
            Text(
                text = adjustmentNotice,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = JackpotGold.copy(alpha = 0.9f)
            )
        }
        if (!note.isNullOrBlank()) {
            Text(
                text = note,
                style = MaterialTheme.typography.bodySmall,
                color = SecondaryText.copy(alpha = 0.88f)
            )
        }
    }
}

@Composable
fun GlassPrimaryActionButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        PrimaryBlue.copy(alpha = 0.55f),
                        PrimaryBlue.copy(alpha = 0.35f)
                    )
                )
            )
            .border(1.dp, PrimaryBlue.copy(alpha = 0.55f), RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "+  $label",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryText
        )
    }
}
