package com.example.wmfunbett2026.ui.designsystem.buttons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.ui.theme.PrimaryBlue
import com.example.wmfunbett2026.ui.theme.TextSecondary

enum class StepperDirection {
    Decrement,
    Increment
}

private val StepperButtonSize = 32.dp

@Composable
fun AppStepperIconButton(
    direction: StepperDirection,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    AppIconButton(
        icon = when (direction) {
            StepperDirection.Decrement -> Icons.Default.Remove
            StepperDirection.Increment -> Icons.Default.Add
        },
        contentDescription = contentDescription,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        filled = false,
        iconTint = when (direction) {
            StepperDirection.Decrement -> TextSecondary
            StepperDirection.Increment -> PrimaryBlue
        },
        buttonSize = StepperButtonSize
    )
}
