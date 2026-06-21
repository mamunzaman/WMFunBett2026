package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.ui.theme.DangerRed
import com.example.wmfunbett2026.ui.theme.PrimaryBlue
import com.example.wmfunbett2026.ui.theme.SheetChipUnselected
import com.example.wmfunbett2026.ui.theme.SheetOnSurface
import com.example.wmfunbett2026.ui.theme.SheetOnSurfaceVariant
import com.example.wmfunbett2026.ui.theme.SheetBorderUnfocused

private val FormFieldShape = RoundedCornerShape(12.dp)

@Composable
fun formTextFieldColors(isError: Boolean = false): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = PrimaryBlue,
    unfocusedBorderColor = SheetBorderUnfocused,
    errorBorderColor = DangerRed,
    focusedLabelColor = PrimaryBlue,
    unfocusedLabelColor = SheetOnSurfaceVariant,
    errorLabelColor = DangerRed,
    cursorColor = PrimaryBlue,
    focusedTextColor = SheetOnSurface,
    unfocusedTextColor = SheetOnSurface,
    errorTextColor = SheetOnSurface,
    focusedPlaceholderColor = SheetOnSurfaceVariant,
    unfocusedPlaceholderColor = SheetOnSurfaceVariant
)

@Composable
fun FormOutlinedTextField(
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
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        singleLine = singleLine,
        minLines = minLines,
        readOnly = readOnly,
        isError = isError,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        shape = FormFieldShape,
        colors = formTextFieldColors(isError = isError)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormFilterChip(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = PrimaryBlue,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            containerColor = SheetChipUnselected,
            labelColor = SheetOnSurface
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = SheetBorderUnfocused,
            selectedBorderColor = PrimaryBlue
        )
    )
}

@Composable
fun FormSectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun FormErrorText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.bodySmall,
        color = DangerRed
    )
}
