package com.example.wmfunbett2026.ui.designsystem.fields

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.wmfunbett2026.ui.components.FormPickerField

@Composable
fun AppReadOnlyField(
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    isError: Boolean = false
) {
    FormPickerField(
        value = value,
        onClick = onClick,
        modifier = modifier.appFieldModifier(),
        label = label,
        placeholder = placeholder,
        isError = isError
    )
}
