package com.example.wmfunbett2026.ui.designsystem.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.ui.designsystem.layout.CardCornerRadius
import com.example.wmfunbett2026.ui.designsystem.layout.DefaultCardPadding
import com.example.wmfunbett2026.ui.theme.GlassBorder
import com.example.wmfunbett2026.ui.theme.MatchCardCompactSurface

@Composable
fun AppSurfaceCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CardCornerRadius))
            .background(MatchCardCompactSurface)
            .border(1.dp, GlassBorder, RoundedCornerShape(CardCornerRadius))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(DefaultCardPadding),
        content = content
    )
}
