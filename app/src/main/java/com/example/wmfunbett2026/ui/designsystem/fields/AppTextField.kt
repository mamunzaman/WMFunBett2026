package com.example.wmfunbett2026.ui.designsystem.fields

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import com.example.wmfunbett2026.ui.components.FormOutlinedTextField

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    readOnly: Boolean = false,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    FormOutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.appFieldModifier(),
        label = label,
        placeholder = placeholder,
        singleLine = singleLine,
        minLines = minLines,
        readOnly = readOnly,
        isError = isError,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon
    )
}

@Composable
fun AppFieldErrorText(
    text: String,
    modifier: Modifier = Modifier
) {
    com.example.wmfunbett2026.ui.components.FormErrorText(text = text, modifier = modifier)
}

@Composable
fun AppFieldLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    com.example.wmfunbett2026.ui.components.FormSectionLabel(text = text, modifier = modifier)
}
