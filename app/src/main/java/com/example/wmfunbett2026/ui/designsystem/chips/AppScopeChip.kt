package com.example.wmfunbett2026.ui.designsystem.chips

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.wmfunbett2026.ui.components.GlassScopePill

@Composable
fun AppScopeChip(
    label: String,
    modifier: Modifier = Modifier
) {
    GlassScopePill(label = label, modifier = modifier)
}
