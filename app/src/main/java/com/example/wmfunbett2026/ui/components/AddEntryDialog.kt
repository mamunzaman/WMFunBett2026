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
import com.example.wmfunbett2026.ui.theme.DangerRed
import com.example.wmfunbett2026.ui.theme.PrimaryText
import com.example.wmfunbett2026.ui.theme.SecondaryText

private data class AddEntryFormState(
    val name: String = "",
    val scoreA: String = "",
    val scoreB: String = "",
    val amount: String = "",
    val note: String = ""
)

@Composable
fun AddEntryDialog(
    teamA: String,
    teamB: String,
    onDismiss: () -> Unit,
    onSave: (name: String, prediction: String, amount: Double, note: String?) -> Unit
) {
    var form by remember { mutableStateOf(AddEntryFormState()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add Entry") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = form.name,
                    onValueChange = {
                        form = form.copy(name = it)
                        errorMessage = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Name") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Prediction",
                    style = MaterialTheme.typography.labelMedium,
                    color = SecondaryText
                )
                Spacer(modifier = Modifier.height(8.dp))
                ScorePredictionRow(
                    teamA = teamA,
                    teamB = teamB,
                    scoreA = form.scoreA,
                    scoreB = form.scoreB,
                    onScoreAChange = {
                        form = form.copy(scoreA = it.filter { c -> c.isDigit() }.take(2))
                        errorMessage = null
                    },
                    onScoreBChange = {
                        form = form.copy(scoreB = it.filter { c -> c.isDigit() }.take(2))
                        errorMessage = null
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = form.amount,
                    onValueChange = {
                        form = form.copy(amount = it)
                        errorMessage = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Amount €") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = form.note,
                    onValueChange = { form = form.copy(note = it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Note (optional)") },
                    minLines = 2
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
                    val validationError = validateAddEntryForm(form)
                    if (validationError != null) {
                        errorMessage = validationError
                    } else {
                        val amount = form.amount.replace(',', '.').toDoubleOrNull() ?: return@TextButton
                        val prediction = "${form.scoreA}-${form.scoreB}"
                        onSave(
                            form.name,
                            prediction,
                            amount,
                            form.note.takeIf { it.isNotBlank() }
                        )
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

@Composable
private fun ScorePredictionRow(
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
            text = " - ",
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

private fun validateAddEntryForm(form: AddEntryFormState): String? {
    if (form.name.isBlank()) return "Name is required"
    if (form.scoreA.isBlank() || form.scoreB.isBlank()) return "Both scores are required"
    val scoreA = form.scoreA.toIntOrNull()
    val scoreB = form.scoreB.toIntOrNull()
    if (scoreA == null || scoreB == null) return "Scores must be numbers"
    if (scoreA < 0 || scoreB < 0) return "Scores must be 0 or more"
    val amount = form.amount.replace(',', '.').toDoubleOrNull()
    if (amount == null || amount <= 0.0) return "Amount must be a number greater than 0"
    return null
}
