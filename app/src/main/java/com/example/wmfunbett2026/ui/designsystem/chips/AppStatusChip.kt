package com.example.wmfunbett2026.ui.designsystem.chips

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.wmfunbett2026.data.model.MatchStatus
import com.example.wmfunbett2026.ui.components.MatchStatusBadge
import com.example.wmfunbett2026.ui.components.MatchStatusBadgeStyle

@Composable
fun AppStatusChip(
    status: MatchStatus,
    modifier: Modifier = Modifier,
    style: MatchStatusBadgeStyle = MatchStatusBadgeStyle.Pill
) {
    MatchStatusBadge(status = status, modifier = modifier, style = style)
}
