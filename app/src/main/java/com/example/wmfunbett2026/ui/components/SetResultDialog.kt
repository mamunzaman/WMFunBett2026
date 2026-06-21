package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.data.model.Game
import com.example.wmfunbett2026.data.model.MatchStatus
import com.example.wmfunbett2026.ui.theme.DangerRed
import com.example.wmfunbett2026.ui.theme.JackpotGold
import com.example.wmfunbett2026.ui.theme.PrimaryBlue
import com.example.wmfunbett2026.ui.theme.PrimaryText
import com.example.wmfunbett2026.ui.theme.SecondaryText
import com.example.wmfunbett2026.ui.theme.SurfaceDark

@Composable
fun SetResultDialog(
    game: Game,
    onDismiss: () -> Unit,
    onSave: (teamAScore: Int?, teamBScore: Int?, status: MatchStatus) -> Unit
) {
    var scoreA by remember(game.id) {
        mutableStateOf(game.teamAScore?.toString().orEmpty())
    }
    var scoreB by remember(game.id) {
        mutableStateOf(game.teamBScore?.toString().orEmpty())
    }
    var status by remember(game.id) { mutableStateOf(game.status) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Result") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Status",
                    style = MaterialTheme.typography.labelMedium,
                    color = SecondaryText
                )
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatusChip(
                            label = MatchStatus.NOT_STARTED.label,
                            selected = status == MatchStatus.NOT_STARTED
                        ) {
                            status = MatchStatus.NOT_STARTED
                            errorMessage = null
                        }
                        StatusChip(
                            label = MatchStatus.LIVE.label,
                            selected = status == MatchStatus.LIVE
                        ) {
                            status = MatchStatus.LIVE
                            errorMessage = null
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatusChip(
                            label = MatchStatus.FINISHED.label,
                            selected = status == MatchStatus.FINISHED
                        ) {
                            status = MatchStatus.FINISHED
                            errorMessage = null
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Score",
                    style = MaterialTheme.typography.labelMedium,
                    color = SecondaryText
                )
                Spacer(modifier = Modifier.height(8.dp))
                ResultScoreRow(
                    teamA = game.teamA,
                    teamB = game.teamB,
                    scoreA = scoreA,
                    scoreB = scoreB,
                    onScoreAChange = {
                        scoreA = it.filter { c -> c.isDigit() }.take(2)
                        errorMessage = null
                    },
                    onScoreBChange = {
                        scoreB = it.filter { c -> c.isDigit() }.take(2)
                        errorMessage = null
                    }
                )
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = errorMessage.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = DangerRed
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val validationError = validateResultForm(scoreA, scoreB, status)
                    if (validationError != null) {
                        errorMessage = validationError
                    } else {
                        val parsedA = scoreA.trim().takeIf { it.isNotEmpty() }?.toIntOrNull()
                        val parsedB = scoreB.trim().takeIf { it.isNotEmpty() }?.toIntOrNull()
                        onSave(parsedA, parsedB, status)
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatusChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = PrimaryBlue,
            selectedLabelColor = PrimaryText,
            containerColor = SurfaceDark,
            labelColor = SecondaryText
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = SecondaryText.copy(alpha = 0.35f),
            selectedBorderColor = JackpotGold.copy(alpha = 0.6f)
        )
    )
}

@Composable
private fun ResultScoreRow(
    teamA: String,
    teamB: String,
    scoreA: String,
    scoreB: String,
    onScoreAChange: (String) -> Unit,
    onScoreBChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = teamA,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End
        )
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedTextField(
            value = scoreA,
            onValueChange = onScoreAChange,
            modifier = Modifier.width(52.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Text(
            text = " : ",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = PrimaryText
        )
        OutlinedTextField(
            value = scoreB,
            onValueChange = onScoreBChange,
            modifier = Modifier.width(52.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = teamB,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun validateResultForm(scoreA: String, scoreB: String, status: MatchStatus): String? {
    val trimmedA = scoreA.trim()
    val trimmedB = scoreB.trim()

    if (status == MatchStatus.NOT_STARTED) {
        if (trimmedA.isNotEmpty()) {
            val value = trimmedA.toIntOrNull() ?: return "Team A score must be a number"
            if (value < 0) return "Scores must be 0 or more"
        }
        if (trimmedB.isNotEmpty()) {
            val value = trimmedB.toIntOrNull() ?: return "Team B score must be a number"
            if (value < 0) return "Scores must be 0 or more"
        }
        return null
    }

    if (trimmedA.isBlank() || trimmedB.isBlank()) {
        return "Both scores are required for Live or Finished"
    }
    val valueA = trimmedA.toIntOrNull() ?: return "Team A score must be a number"
    val valueB = trimmedB.toIntOrNull() ?: return "Team B score must be a number"
    if (valueA < 0 || valueB < 0) return "Scores must be 0 or more"
    return null
}
