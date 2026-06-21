package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.background
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
import com.example.wmfunbett2026.ui.theme.SheetOnSurface
import com.example.wmfunbett2026.ui.theme.SheetOnSurfaceVariant
import com.example.wmfunbett2026.ui.theme.SheetScrimColor
import com.example.wmfunbett2026.ui.theme.SheetSurface
import com.example.wmfunbett2026.ui.theme.SurfaceDark

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
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val maxScrollHeight = LocalConfiguration.current.screenHeightDp.dp * 0.55f
    val sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SheetSurface,
        contentColor = SheetOnSurface,
        scrimColor = SheetScrimColor,
        shape = sheetShape,
        dragHandle = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(sheetShape)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                SurfaceDark.copy(alpha = 0.95f),
                                DarkNavy.copy(alpha = 0.98f)
                            )
                        )
                    )
                    .padding(top = 12.dp, bottom = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BottomSheetDefaults.DragHandle(color = SecondaryText.copy(alpha = 0.55f))
            }
        }
    ) {
        FormSheetTheme {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                SurfaceDark.copy(alpha = 0.96f),
                                DarkNavy
                            )
                        )
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
}
