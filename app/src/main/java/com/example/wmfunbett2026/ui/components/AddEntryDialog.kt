package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.data.jackpot.EntryJoinBreakdown
import com.example.wmfunbett2026.data.jackpot.JackpotCarryItem
import com.example.wmfunbett2026.data.jackpot.JackpotChainCalculator
import com.example.wmfunbett2026.data.model.toEuroLabel
import com.example.wmfunbett2026.ui.theme.JackpotGold

private data class AddEntryFormState(
    val name: String = "",
    val scoreA: String = "",
    val scoreB: String = "",
    val currentRoundAmount: String = "",
    val note: String = ""
)

private data class ResolvedEntryPayment(
    val previousBuyIn: Double,
    val currentRoundAmount: Double,
    val totalRequired: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEntryDialog(
    teamA: String,
    teamB: String,
    carryItems: List<JackpotCarryItem>,
    existingRoundAmount: Double?,
    onDismiss: () -> Unit,
    onSave: (
        name: String,
        prediction: String,
        totalPaid: Double,
        currentRoundAmount: Double,
        note: String?
    ) -> Unit
) {
    val defaultRoundText = formatAmountInput(existingRoundAmount)
    var form by remember(existingRoundAmount, carryItems) {
        mutableStateOf(AddEntryFormState(currentRoundAmount = defaultRoundText))
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val roundAmountLocked = existingRoundAmount != null

    val previousBuyIn = remember(carryItems) {
        carryItems.sumOf { it.requiredAmountPerPerson }
    }
    val inputRoundAmount = form.currentRoundAmount.replace(',', '.').toDoubleOrNull()
    val currentRoundAmount = existingRoundAmount ?: inputRoundAmount ?: 0.0
    val breakdown = remember(carryItems, currentRoundAmount) {
        JackpotChainCalculator.buildEntryJoinBreakdown(carryItems, currentRoundAmount)
    }
    val totalRequired = breakdown.totalRequired
    val canSave = resolveEntryPayment(
        previousBuyIn = previousBuyIn,
        existingRoundAmount = existingRoundAmount,
        inputRoundAmount = inputRoundAmount
    ) != null
    val primaryLabel = if (totalRequired > 0.0) {
        stringResource(R.string.add_entry_with_amount, totalRequired.toEuroLabel())
    } else {
        stringResource(R.string.add_entry)
    }
    val validationErrors = rememberValidationErrors()

    FormBottomSheet(
        title = stringResource(R.string.add_entry),
        onDismiss = onDismiss,
        primaryActionLabel = primaryLabel,
        primaryActionEnabled = canSave,
        onPrimaryAction = {
            val payment = resolveEntryPayment(
                previousBuyIn = previousBuyIn,
                existingRoundAmount = existingRoundAmount,
                inputRoundAmount = inputRoundAmount
            )
            val validationError = validateAddEntryForm(form, payment, previousBuyIn, validationErrors)
            if (validationError != null) {
                errorMessage = validationError
            } else if (payment != null) {
                val prediction = "${form.scoreA}-${form.scoreB}"
                onSave(
                    form.name,
                    prediction,
                    payment.totalRequired,
                    payment.currentRoundAmount,
                    form.note.takeIf { it.isNotBlank() }
                )
            }
        }
    ) {
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            EntryPaymentBreakdown(
                breakdown = breakdown,
                modifier = Modifier.padding(16.dp)
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        FormOutlinedTextField(
            value = form.name,
            onValueChange = {
                form = form.copy(name = it)
                errorMessage = null
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.name)) }
        )
        Spacer(modifier = Modifier.height(14.dp))
        FormSectionLabel(text = stringResource(R.string.prediction))
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
        if (roundAmountLocked) {
            Text(
                text = stringResource(
                    R.string.current_round_amount_locked,
                    existingRoundAmount!!.toEuroLabel()
                ),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.round_amount_locked_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            FormOutlinedTextField(
                value = form.currentRoundAmount,
                onValueChange = {
                    form = form.copy(currentRoundAmount = it)
                    errorMessage = null
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.current_round_amount_euro)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }
        if (previousBuyIn > 0.0) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(
                    R.string.required_total_inline,
                    totalRequired.toEuroLabel()
                ),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = JackpotGold
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        FormOutlinedTextField(
            value = form.note,
            onValueChange = { form = form.copy(note = it) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.note_optional)) },
            singleLine = false,
            minLines = 2
        )
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(10.dp))
            FormErrorText(text = errorMessage.orEmpty())
        }
    }
}

@Composable
private fun EntryPaymentBreakdown(
    breakdown: EntryJoinBreakdown,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        FormSectionLabel(text = stringResource(R.string.payment_breakdown))
        Spacer(modifier = Modifier.height(8.dp))
        if (breakdown.carryItems.isNotEmpty()) {
            Text(
                text = stringResource(R.string.previous_unpaid_rounds),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            breakdown.carryItems.forEach { item ->
                Text(
                    text = "${item.sourceGameLabel} · ${item.sourceTippGroupTitle} · ${item.requiredAmountPerPerson.toEuroLabel()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        BreakdownLine(
            label = stringResource(R.string.previous_jackpot_buy_in),
            value = breakdown.previousJackpotBuyIn.toEuroLabel()
        )
        BreakdownLine(
            label = stringResource(R.string.current_round_amount),
            value = breakdown.currentRoundEntryAmount.toEuroLabel()
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        BreakdownLine(
            label = stringResource(R.string.required_total),
            value = breakdown.totalRequired.toEuroLabel(),
            emphasized = true
        )
    }
}

@Composable
private fun BreakdownLine(
    label: String,
    value: String,
    emphasized: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (emphasized) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
            fontWeight = if (emphasized) FontWeight.Bold else FontWeight.Normal,
            color = if (emphasized) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
        Text(
            text = value,
            style = if (emphasized) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = if (emphasized) JackpotGold else MaterialTheme.colorScheme.onSurface
        )
    }
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
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End
        )
        Spacer(modifier = Modifier.width(8.dp))
        FormOutlinedTextField(
            value = scoreA,
            onValueChange = onScoreAChange,
            modifier = Modifier.width(52.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Text(
            text = " - ",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        FormOutlinedTextField(
            value = scoreB,
            onValueChange = onScoreBChange,
            modifier = Modifier.width(52.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = teamB,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun formatAmountInput(amount: Double?): String {
    if (amount == null) return ""
    return if (amount % 1.0 == 0.0) amount.toInt().toString() else amount.toString()
}

private fun resolveEntryPayment(
    previousBuyIn: Double,
    existingRoundAmount: Double?,
    inputRoundAmount: Double?
): ResolvedEntryPayment? {
    val currentRoundAmount = existingRoundAmount ?: inputRoundAmount ?: return null
    if (currentRoundAmount <= 0.0) return null
    val totalRequired = previousBuyIn + currentRoundAmount
    if (previousBuyIn > 0.0 && totalRequired <= previousBuyIn) return null
    return ResolvedEntryPayment(
        previousBuyIn = previousBuyIn,
        currentRoundAmount = currentRoundAmount,
        totalRequired = totalRequired
    )
}

private data class EntryValidationErrors(
    val nameRequired: String,
    val scoresRequired: String,
    val scoresNumbers: String,
    val scoresNonNegative: String,
    val roundAmount: String,
    val roundAmountWithCarry: String
)

@Composable
private fun rememberValidationErrors(): EntryValidationErrors = EntryValidationErrors(
    nameRequired = stringResource(R.string.error_name_required),
    scoresRequired = stringResource(R.string.error_scores_required),
    scoresNumbers = stringResource(R.string.error_scores_numbers),
    scoresNonNegative = stringResource(R.string.error_scores_non_negative),
    roundAmount = stringResource(R.string.error_round_amount),
    roundAmountWithCarry = stringResource(R.string.error_round_amount_with_carry)
)

private fun validateAddEntryForm(
    form: AddEntryFormState,
    payment: ResolvedEntryPayment?,
    previousBuyIn: Double,
    errors: EntryValidationErrors
): String? {
    if (form.name.isBlank()) return errors.nameRequired
    if (form.scoreA.isBlank() || form.scoreB.isBlank()) return errors.scoresRequired
    val scoreA = form.scoreA.toIntOrNull()
    val scoreB = form.scoreB.toIntOrNull()
    if (scoreA == null || scoreB == null) return errors.scoresNumbers
    if (scoreA < 0 || scoreB < 0) return errors.scoresNonNegative
    if (payment == null) {
        return if (previousBuyIn > 0.0) errors.roundAmountWithCarry else errors.roundAmount
    }
    return null
}
