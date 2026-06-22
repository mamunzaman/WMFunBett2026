package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.ui.theme.DangerRed
import com.example.wmfunbett2026.ui.theme.PrimaryBlue
import com.example.wmfunbett2026.ui.theme.SheetBorderUnfocused
import com.example.wmfunbett2026.ui.theme.SheetChipUnselected
import com.example.wmfunbett2026.ui.theme.SheetOnSurface
import com.example.wmfunbett2026.ui.theme.SheetOnSurfaceVariant

private val FormFieldShape = RoundedCornerShape(12.dp)

@Composable
fun formTextFieldColors(isError: Boolean = false): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = PrimaryBlue,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    errorBorderColor = DangerRed,
    focusedLabelColor = PrimaryBlue,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    errorLabelColor = DangerRed,
    cursorColor = PrimaryBlue,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    errorTextColor = MaterialTheme.colorScheme.onSurface,
    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
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

@Composable
fun LockedLeagueInfoRow(
    leagueName: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                RoundedCornerShape(12.dp)
            )
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.add_match_locked_league, leagueName),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun FormPickerField(
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null
) {
    FormOutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        modifier = modifier.clickable(onClick = onClick),
        label = label,
        placeholder = placeholder
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
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            labelColor = MaterialTheme.colorScheme.onSurface
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = MaterialTheme.colorScheme.outline,
            selectedBorderColor = PrimaryBlue
        )
    )
}

@Composable
fun FormSectionCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                RoundedCornerShape(14.dp)
            )
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        content()
    }
}

@Composable
fun FormMatchdaySelectCard(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .background(
                color = if (selected) PrimaryBlue.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surfaceVariant,
                shape = shape
            )
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) PrimaryBlue else MaterialTheme.colorScheme.outline,
                shape = shape
            )
            .padding(horizontal = 12.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) PrimaryBlue else MaterialTheme.colorScheme.onSurface
        )
    }
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
