package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.ui.theme.DarkNavy
import com.example.wmfunbett2026.ui.theme.FormSheetTheme
import com.example.wmfunbett2026.ui.theme.PrimaryBlue
import com.example.wmfunbett2026.ui.theme.PrimaryText
import com.example.wmfunbett2026.ui.theme.SecondaryText
import com.example.wmfunbett2026.ui.theme.SheetOnSurfaceVariant
import com.example.wmfunbett2026.ui.theme.SheetSurface
import com.example.wmfunbett2026.ui.theme.SurfaceDark

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
        contentColor = PrimaryText,
        scrimColor = ModalSheetScrimColor,
        shape = AppSheetShape,
        dragHandle = {
            AppModalSheetDragHandle()
        }
    ) {
        AppModalSheetSurface(title = title, maxScrollHeight = maxScrollHeight) {
            content()
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onPrimaryAction,
                enabled = primaryActionEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    contentColor = PrimaryText,
                    disabledContainerColor = PrimaryBlue.copy(alpha = 0.38f),
                    disabledContentColor = PrimaryText.copy(alpha = 0.6f)
                )
            ) {
                Text(primaryActionLabel)
            }
            if (showCancel) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 4.dp),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = SheetOnSurfaceVariant
                    )
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppModalSheetDragHandle() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppSheetShape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SurfaceDark.copy(alpha = 0.98f),
                        DarkNavy.copy(alpha = 0.96f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        PrimaryText.copy(alpha = 0.22f),
                        PrimaryText.copy(alpha = 0.06f)
                    )
                ),
                shape = AppSheetShape
            )
            .padding(top = 12.dp, bottom = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BottomSheetDefaults.DragHandle(color = SecondaryText.copy(alpha = 0.55f))
    }
}

@Composable
private fun AppModalSheetSurface(
    title: String,
    maxScrollHeight: androidx.compose.ui.unit.Dp,
    content: @Composable ColumnScope.() -> Unit
) {
    FormSheetTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(AppSheetShape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            SurfaceDark.copy(alpha = 0.97f),
                            DarkNavy.copy(alpha = 0.99f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = PrimaryBlue.copy(alpha = 0.28f),
                    shape = AppSheetShape
                )
                .navigationBarsPadding()
                .imePadding()
        ) {
            Text(
                text = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = PrimaryText
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = maxScrollHeight)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                content = content
            )
        }
    }
}
