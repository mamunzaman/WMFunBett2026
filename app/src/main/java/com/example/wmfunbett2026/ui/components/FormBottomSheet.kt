package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.ui.designsystem.buttons.AppPrimaryButton
import com.example.wmfunbett2026.ui.designsystem.buttons.AppSecondaryButton
import com.example.wmfunbett2026.ui.theme.FormSheetTheme
import com.example.wmfunbett2026.ui.theme.SheetSurface
import com.example.wmfunbett2026.ui.theme.TextPrimary
private val AppSheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)

/**
 * Shared modal bottom sheet for all slide-up forms.
 * Registers with [ModalSheetBackdropState] so [MainActivity] can blur content behind the sheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormBottomSheet(
    title: String,
    onDismiss: () -> Unit,
    primaryActionLabel: String,
    onPrimaryAction: () -> Unit,
    primaryActionEnabled: Boolean = true,
    showCancel: Boolean = true,
    headerActions: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    DisposableEffect(Unit) {
        ModalSheetBackdropState.push()
        onDispose { ModalSheetBackdropState.pop() }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val maxScrollHeight = LocalConfiguration.current.screenHeightDp.dp * 0.55f

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SheetSurface,
        contentColor = TextPrimary,
        scrimColor = ModalSheetScrimColor,
        shape = AppSheetShape,
        tonalElevation = 0.dp,
        dragHandle = { SheetDragHandle() }
    ) {
        FormSheetTheme {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 4.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    headerActions?.invoke()
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = maxScrollHeight)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    content = content
                )
                Spacer(modifier = Modifier.height(8.dp))
                AppPrimaryButton(
                    text = primaryActionLabel,
                    onClick = onPrimaryAction,
                    enabled = primaryActionEnabled,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                if (showCancel) {
                    AppSecondaryButton(
                        text = stringResource(R.string.cancel),
                        onClick = onDismiss,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
