package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.data.model.Game
import com.example.wmfunbett2026.data.model.parseScheduleParts
import com.example.wmfunbett2026.data.repository.FunBettRepository
import com.example.wmfunbett2026.ui.matchcenter.MatchTeamCountryCatalog
import com.example.wmfunbett2026.ui.matchcenter.loadLeagueSummaries
import com.example.wmfunbett2026.ui.matchcenter.loadSelectableLeagueRoundOptions
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val AddMatchDisplayDateFormatter =
    DateTimeFormatter.ofPattern("EEE, d MMM yyyy", Locale.getDefault())

private val AddMatchDisplayTimeFormatter =
    DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())

private val AddMatchStorageDateFormatter =
    DateTimeFormatter.ofPattern("EEE, d MMM yyyy", Locale.getDefault())

private val AddMatchStorageTimeFormatter =
    DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())

data class EditMatchSheetTarget(
    val gameId: String,
    val roundId: String,
    val dayName: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMatchSheet(
    lockedLeagueId: String? = null,
    editTarget: EditMatchSheetTarget? = null,
    onDismiss: () -> Unit,
    onCreate: (
        leagueId: String,
        leagueName: String,
        teamA: String,
        teamB: String,
        day: LocalDate,
        time: LocalTime,
        note: String?
    ) -> Unit,
    onSave: (
        gameId: String,
        roundId: String,
        leagueId: String,
        leagueName: String,
        teamA: String,
        teamB: String,
        day: LocalDate,
        time: LocalTime,
        note: String?
    ) -> Unit = { _, _, _, _, _, _, _, _, _ -> }
) {
    FunBettRepository.dataVersion.intValue
    val isEditMode = editTarget != null
    val editGame = remember(editTarget, FunBettRepository.dataVersion.intValue) {
        editTarget?.gameId?.let { FunBettRepository.getGame(it) }
    }
    val editSchedule = remember(editGame) {
        editGame?.parseScheduleParts()
    }
    val leagueOptions = remember(FunBettRepository.dataVersion.intValue) {
        loadSelectableLeagueRoundOptions()
    }
    val resolvedLockedLeagueId = lockedLeagueId ?: editTarget?.roundId?.let { roundId ->
        loadLeagueSummaries().find { it.roundId == roundId }?.id
    }
    val lockedLeague = remember(resolvedLockedLeagueId, leagueOptions) {
        resolvedLockedLeagueId?.let { id -> leagueOptions.find { it.leagueId == id } }
    }
    val lockedLeagueName = remember(resolvedLockedLeagueId, leagueOptions) {
        lockedLeague?.displayName
            ?: resolvedLockedLeagueId?.let { id -> loadLeagueSummaries().find { it.id == id }?.name }
    }

    var selectedLeagueId by remember(resolvedLockedLeagueId, leagueOptions, editTarget) {
        mutableStateOf(resolvedLockedLeagueId ?: leagueOptions.firstOrNull()?.leagueId.orEmpty())
    }
    var teamA by remember(editGame?.id) { mutableStateOf(editGame?.teamA.orEmpty()) }
    var teamB by remember(editGame?.id) { mutableStateOf(editGame?.teamB.orEmpty()) }
    var selectedDay by remember(editGame?.id) { mutableStateOf(editSchedule?.date) }
    var selectedTime by remember(editGame?.id) { mutableStateOf(editSchedule?.time) }
    var note by remember(editGame?.id) { mutableStateOf(editGame?.note.orEmpty()) }
    val initialDay = remember(editGame?.id) { editSchedule?.date }

    var leagueMenuExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var dayDateError by remember { mutableStateOf(false) }

    val today = remember { LocalDate.now() }
    val isDayValid = selectedDay?.let { day ->
        day == initialDay || FunBettRepository.isMatchDateAllowed(day, today)
    } == true
    val effectiveLeagueId = resolvedLockedLeagueId ?: selectedLeagueId
    val effectiveLeague = leagueOptions.find { it.leagueId == effectiveLeagueId }
    val canSave = (if (isEditMode) editGame != null else true) &&
        (effectiveLeague != null || isEditMode) &&
        teamA.isNotBlank() &&
        teamB.isNotBlank() &&
        isDayValid &&
        selectedDay != null &&
        selectedTime != null &&
        !dayDateError

    FormBottomSheet(
        title = stringResource(
            if (isEditMode) R.string.edit_match_sheet_title else R.string.add_match_sheet_title
        ),
        onDismiss = onDismiss,
        primaryActionLabel = stringResource(if (isEditMode) R.string.save else R.string.create),
        onPrimaryAction = {
            val day = selectedDay ?: return@FormBottomSheet
            val time = selectedTime ?: return@FormBottomSheet
            val trimmedNote = note.trim().takeIf { it.isNotEmpty() }
            val normalizedTeamA = MatchTeamCountryCatalog.normalizeForStorage(teamA)
            val normalizedTeamB = MatchTeamCountryCatalog.normalizeForStorage(teamB)
            if (isEditMode) {
                val target = editTarget ?: return@FormBottomSheet
                val leagueName = effectiveLeague?.displayName
                    ?: lockedLeagueName
                    ?: target.roundId
                onSave(
                    target.gameId,
                    target.roundId,
                    effectiveLeagueId,
                    leagueName,
                    normalizedTeamA,
                    normalizedTeamB,
                    day,
                    time,
                    trimmedNote
                )
            } else {
                val league = effectiveLeague ?: return@FormBottomSheet
                onCreate(
                    league.leagueId,
                    league.displayName,
                    normalizedTeamA,
                    normalizedTeamB,
                    day,
                    time,
                    trimmedNote
                )
            }
        },
        primaryActionEnabled = canSave
    ) {
        if (resolvedLockedLeagueId != null && lockedLeagueName != null) {
            LockedLeagueInfoRow(leagueName = lockedLeagueName)
        } else if (resolvedLockedLeagueId != null) {
            LockedLeagueInfoRow(leagueName = resolvedLockedLeagueId)
        } else if (!isEditMode) {
            ExposedDropdownMenuBox(
                expanded = leagueMenuExpanded,
                onExpandedChange = { leagueMenuExpanded = it }
            ) {
                FormOutlinedTextField(
                    value = effectiveLeague?.displayName.orEmpty(),
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    label = { Text(stringResource(R.string.add_match_round_label)) },
                    placeholder = { Text(stringResource(R.string.add_match_round_hint)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = leagueMenuExpanded)
                    }
                )
                ExposedDropdownMenu(
                    expanded = leagueMenuExpanded,
                    onDismissRequest = { leagueMenuExpanded = false }
                ) {
                    leagueOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.displayName) },
                            onClick = {
                                selectedLeagueId = option.leagueId
                                leagueMenuExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        FormTeamField(
            value = teamA,
            onValueChange = { teamA = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.team_a)) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        FormTeamField(
            value = teamB,
            onValueChange = { teamB = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.team_b)) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        FormPickerField(
            value = selectedDay?.format(AddMatchDisplayDateFormatter).orEmpty(),
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.add_match_day_label)) },
            placeholder = { Text(stringResource(R.string.add_match_day_hint)) },
            isError = dayDateError
        )

        if (dayDateError) {
            FormErrorText(text = stringResource(R.string.error_add_match_past_date))
        }

        Spacer(modifier = Modifier.height(10.dp))

        FormPickerField(
            value = selectedTime?.format(AddMatchDisplayTimeFormatter).orEmpty(),
            onClick = { showTimePicker = true },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.add_match_time_label)) },
            placeholder = { Text(stringResource(R.string.add_match_time_hint)) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        FormOutlinedTextField(
            value = note,
            onValueChange = { note = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.note_optional)) },
            singleLine = false,
            minLines = 2
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            selectableDates = remember(today) {
                object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        val date = Instant.ofEpochMilli(utcTimeMillis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        return FunBettRepository.isMatchDateAllowed(date, today)
                    }

                    override fun isSelectableYear(year: Int): Boolean = year >= today.year
                }
            }
        )
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val picked = datePickerState.selectedDateMillis?.let { millis ->
                            Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }
                        if (picked != null && FunBettRepository.isMatchDateAllowed(picked, today)) {
                            selectedDay = picked
                            dayDateError = false
                        } else if (picked != null) {
                            selectedDay = null
                            dayDateError = true
                        }
                        showDatePicker = false
                    }
                ) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            text = {
                DatePicker(state = datePickerState)
            }
        )
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedTime = LocalTime.of(
                            timePickerState.hour,
                            timePickerState.minute
                        )
                        showTimePicker = false
                    }
                ) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}
