package com.example.wmfunbett2026.ui.designsystem.fields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import com.example.wmfunbett2026.ui.designsystem.layout.DefaultFieldMinHeight
import com.example.wmfunbett2026.ui.designsystem.layout.FieldCornerRadius

internal val AppFieldShape = RoundedCornerShape(FieldCornerRadius)

internal fun Modifier.appFieldModifier(): Modifier = this
    .fillMaxWidth()
    .heightIn(min = DefaultFieldMinHeight)
