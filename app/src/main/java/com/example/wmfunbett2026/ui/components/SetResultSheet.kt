package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.data.model.Game
import com.example.wmfunbett2026.data.model.MatchStatus
import com.example.wmfunbett2026.ui.designsystem.buttons.AppBottomSheetPrimaryButton
import com.example.wmfunbett2026.ui.designsystem.buttons.AppBottomSheetSecondaryButton
import com.example.wmfunbett2026.ui.designsystem.chips.AppSegmentedStatusControl
import com.example.wmfunbett2026.ui.theme.Divider
import com.example.wmfunbett2026.ui.theme.SurfaceVariant
import com.example.wmfunbett2026.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetResultSheet(
    game: Game,
    onDismiss: () -> Unit,
    onSave: (teamAScore: Int?, teamBScore: Int?, status: MatchStatus) -> Unit
) {
    var scoreA by remember(game.id) {
        mutableStateOf(initialResultScoreText(game, isTeamA = true))
    }
    var scoreB by remember(game.id) {
        mutableStateOf(initialResultScoreText(game, isTeamA = false))
    }
    var status by remember(game.id) { mutableStateOf(game.status) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val resultErrors = rememberResultValidationErrors()
    val scoreDisplayMode = if (status == MatchStatus.NOT_STARTED) {
        MatchScoreDisplayMode.EMPTY_DASH
    } else {
        MatchScoreDisplayMode.NUMERIC
    }

    fun onStatusSelected(newStatus: MatchStatus) {
        val previousStatus = status
        status = newStatus
        errorMessage = null
        if (newStatus == MatchStatus.NOT_STARTED) return
        if (previousStatus == MatchStatus.NOT_STARTED) {
            if (scoreA.isBlank()) scoreA = "0"
            if (scoreB.isBlank()) scoreB = "0"
        }
    }

    AppBottomSheetContainer(
        onDismiss = onDismiss,
        footer = {
            AppBottomSheetPrimaryButton(
                text = stringResource(R.string.save_result),
                onClick = {
                    val validationError = validateResultForm(scoreA, scoreB, status, resultErrors)
                    if (validationError != null) {
                        errorMessage = validationError
                    } else if (status == MatchStatus.NOT_STARTED) {
                        onSave(null, null, status)
                    } else {
                        val parsedA = scoreA.trim().toIntOrNull() ?: 0
                        val parsedB = scoreB.trim().toIntOrNull() ?: 0
                        onSave(parsedA, parsedB, status)
                    }
                }
            )
            AppBottomSheetSecondaryButton(
                text = stringResource(R.string.cancel),
                onClick = onDismiss
            )
        }
    ) {
        AppBottomSheetHeader(
            title = stringResource(R.string.set_result),
            onCloseClick = onDismiss
        )

        MatchScoreInputCard(
            teamA = game.teamA,
            teamB = game.teamB,
            scoreA = scoreA,
            scoreB = scoreB,
            displayMode = scoreDisplayMode,
            onScoreAChange = {
                scoreA = it.filter { c -> c.isDigit() }.take(2)
                errorMessage = null
            },
            onScoreBChange = {
                scoreB = it.filter { c -> c.isDigit() }.take(2)
                errorMessage = null
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        FormSectionLabel(text = stringResource(R.string.status))
        Spacer(modifier = Modifier.height(8.dp))
        MatchStatusSegmentedRow(
            selected = status,
            onSelected = ::onStatusSelected
        )

        Spacer(modifier = Modifier.height(14.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceVariant.copy(alpha = 0.35f))
                .border(1.dp, Divider.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
        ) {
            SheetInfoCard(text = stringResource(R.string.set_result_info))
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(10.dp))
            FormErrorText(text = errorMessage.orEmpty())
        }
    }
}

@Composable
private fun MatchStatusSegmentedRow(
    selected: MatchStatus,
    onSelected: (MatchStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    AppSegmentedStatusControl(
        options = listOf(
            MatchStatus.NOT_STARTED,
            MatchStatus.LIVE,
            MatchStatus.FINISHED
        ),
        selected = selected,
        label = { option ->
            when (option) {
                MatchStatus.NOT_STARTED -> stringResource(R.string.status_not_started)
                MatchStatus.LIVE -> stringResource(R.string.status_live)
                MatchStatus.FINISHED -> stringResource(R.string.status_finished)
            }
        },
        onSelected = onSelected,
        modifier = modifier
    )
}

@Composable
fun SetResultDialog(
    game: Game,
    onDismiss: () -> Unit,
    onSave: (teamAScore: Int?, teamBScore: Int?, status: MatchStatus) -> Unit
) {
    SetResultSheet(game = game, onDismiss = onDismiss, onSave = onSave)
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

private fun initialResultScoreText(game: Game, isTeamA: Boolean): String {
    if (game.status == MatchStatus.NOT_STARTED) return ""
    val value = if (isTeamA) game.teamAScore else game.teamBScore
    return value?.toString() ?: "0"
}

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
