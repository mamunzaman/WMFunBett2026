package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.wmfunbett2026.data.model.Game
import com.example.wmfunbett2026.data.model.MatchStatus

@OptIn(ExperimentalMaterial3Api::class)
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
    val resultErrors = rememberResultValidationErrors()

    FormBottomSheet(
        title = stringResource(R.string.set_result),
        onDismiss = onDismiss,
        primaryActionLabel = stringResource(R.string.save),
        onPrimaryAction = {
            val validationError = validateResultForm(scoreA, scoreB, status, resultErrors)
            if (validationError != null) {
                errorMessage = validationError
            } else {
                val parsedA = scoreA.trim().takeIf { it.isNotEmpty() }?.toIntOrNull()
                val parsedB = scoreB.trim().takeIf { it.isNotEmpty() }?.toIntOrNull()
                onSave(parsedA, parsedB, status)
            }
        }
    ) {
        FormSectionLabel(text = stringResource(R.string.status))
        Spacer(modifier = Modifier.height(6.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FormFilterChip(
                    label = stringResource(R.string.status_not_started),
                    selected = status == MatchStatus.NOT_STARTED
                ) {
                    status = MatchStatus.NOT_STARTED
                    errorMessage = null
                }
                FormFilterChip(
                    label = stringResource(R.string.status_live),
                    selected = status == MatchStatus.LIVE
                ) {
                    status = MatchStatus.LIVE
                    errorMessage = null
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FormFilterChip(
                    label = stringResource(R.string.status_finished),
                    selected = status == MatchStatus.FINISHED
                ) {
                    status = MatchStatus.FINISHED
                    errorMessage = null
                }
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        FormSectionLabel(text = stringResource(R.string.score))
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
            FormErrorText(text = errorMessage.orEmpty())
        }
    }
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
            text = " : ",
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

private data class ResultValidationErrors(
    val teamAScoreNumber: String,
    val teamBScoreNumber: String,
    val scoresNonNegative: String,
    val scoresRequiredLiveFinished: String
)

@Composable
private fun rememberResultValidationErrors(): ResultValidationErrors = ResultValidationErrors(
    teamAScoreNumber = stringResource(R.string.error_team_a_score_number),
    teamBScoreNumber = stringResource(R.string.error_team_b_score_number),
    scoresNonNegative = stringResource(R.string.error_scores_non_negative),
    scoresRequiredLiveFinished = stringResource(R.string.error_scores_required_live_finished)
)

private fun validateResultForm(
    scoreA: String,
    scoreB: String,
    status: MatchStatus,
    errors: ResultValidationErrors
): String? {
    val trimmedA = scoreA.trim()
    val trimmedB = scoreB.trim()

    if (status == MatchStatus.NOT_STARTED) {
        if (trimmedA.isNotEmpty()) {
            val value = trimmedA.toIntOrNull() ?: return errors.teamAScoreNumber
            if (value < 0) return errors.scoresNonNegative
        }
        if (trimmedB.isNotEmpty()) {
            val value = trimmedB.toIntOrNull() ?: return errors.teamBScoreNumber
            if (value < 0) return errors.scoresNonNegative
        }
        return null
    }

    if (trimmedA.isBlank() || trimmedB.isBlank()) {
        return errors.scoresRequiredLiveFinished
    }
    val valueA = trimmedA.toIntOrNull() ?: return errors.teamAScoreNumber
    val valueB = trimmedB.toIntOrNull() ?: return errors.teamBScoreNumber
    if (valueA < 0 || valueB < 0) return errors.scoresNonNegative
    return null
}
