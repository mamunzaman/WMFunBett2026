package com.example.wmfunbett2026.ui.designsystem.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.ui.designsystem.layout.DefaultCardPadding
import com.example.wmfunbett2026.ui.theme.PrimaryText
import com.example.wmfunbett2026.ui.theme.SecondaryText

@Composable
fun AppEmptyState(
    message: String,
    modifier: Modifier = Modifier,
    title: String? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(DefaultCardPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        title?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryText,
                textAlign = TextAlign.Center
            )
        }
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = SecondaryText,
            textAlign = TextAlign.Center
        )
    }
}
