package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
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
import com.example.wmfunbett2026.data.model.MatchTippType
import com.example.wmfunbett2026.data.repository.FunBettRepository
import com.example.wmfunbett2026.ui.matchcenter.loadLeagueSummaries
import com.example.wmfunbett2026.ui.matchcenter.loadSelectableLeagueRoundOptions
import com.example.wmfunbett2026.ui.matchcenter.localizedLabel
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMatchSheet(
    lockedLeagueId: String? = null,
    onDismiss: () -> Unit,
    onCreate: (
        leagueId: String,
        leagueName: String,
        teamA: String,
        teamB: String,
        day: LocalDate,
        time: LocalTime,
        tippType: MatchTippType,
        note: String?
    ) -> Unit
) {
    FunBettRepository.dataVersion.intValue
    val leagueOptions = remember(FunBettRepository.dataVersion.intValue) {
        loadSelectableLeagueRoundOptions()
    }
    val lockedLeague = remember(lockedLeagueId, leagueOptions) {
        lockedLeagueId?.let { id -> leagueOptions.find { it.leagueId == id } }
    }
    val lockedLeagueName = remember(lockedLeagueId, leagueOptions) {
        lockedLeague?.displayName
            ?: lockedLeagueId?.let { id -> loadLeagueSummaries().find { it.id == id }?.name }
    }

    var selectedLeagueId by remember(lockedLeagueId, leagueOptions) {
        mutableStateOf(lockedLeagueId ?: leagueOptions.firstOrNull()?.leagueId.orEmpty())
    }
    var teamA by remember { mutableStateOf("") }
    var teamB by remember { mutableStateOf("") }
    var selectedDay by remember { mutableStateOf<LocalDate?>(null) }
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }
    var selectedTippType by remember { mutableStateOf<MatchTippType?>(null) }
    var note by remember { mutableStateOf("") }

    var leagueMenuExpanded by remember { mutableStateOf(false) }
    var tippTypeMenuExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val effectiveLeagueId = lockedLeagueId ?: selectedLeagueId
    val effectiveLeague = leagueOptions.find { it.leagueId == effectiveLeagueId }
    val canCreate = effectiveLeague != null &&
        teamA.isNotBlank() &&
        teamB.isNotBlank() &&
        selectedDay != null &&
        selectedTime != null &&
        selectedTippType != null

    FormBottomSheet(
        title = stringResource(R.string.add_match_sheet_title),
        onDismiss = onDismiss,
        primaryActionLabel = stringResource(R.string.create),
        onPrimaryAction = {
            val league = effectiveLeague ?: return@FormBottomSheet
            onCreate(
                league.leagueId,
                league.displayName,
                teamA.trim(),
                teamB.trim(),
                selectedDay!!,
                selectedTime!!,
                selectedTippType!!,
                note.trim().takeIf { it.isNotEmpty() }
            )
        },
        primaryActionEnabled = canCreate
    ) {
        if (lockedLeagueId != null && lockedLeagueName != null) {
            LockedLeagueInfoRow(leagueName = lockedLeagueName)
        } else if (lockedLeagueId != null) {
            LockedLeagueInfoRow(leagueName = lockedLeagueId)
        } else {
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

        FormOutlinedTextField(
            value = teamA,
            onValueChange = { teamA = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.team_a)) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        FormOutlinedTextField(
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
            placeholder = { Text(stringResource(R.string.add_match_day_hint)) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        FormPickerField(
            value = selectedTime?.format(AddMatchDisplayTimeFormatter).orEmpty(),
            onClick = { showTimePicker = true },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.add_match_time_label)) },
            placeholder = { Text(stringResource(R.string.add_match_time_hint)) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        ExposedDropdownMenuBox(
            expanded = tippTypeMenuExpanded,
            onExpandedChange = { tippTypeMenuExpanded = it }
        ) {
            FormOutlinedTextField(
                value = selectedTippType?.localizedLabel().orEmpty(),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                label = { Text(stringResource(R.string.add_match_tipp_type_label)) },
                placeholder = { Text(stringResource(R.string.add_match_tipp_type_hint)) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = tippTypeMenuExpanded)
                }
            )
            ExposedDropdownMenu(
                expanded = tippTypeMenuExpanded,
                onDismissRequest = { tippTypeMenuExpanded = false }
            ) {
                MatchTippType.entries.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.localizedLabel()) },
                        onClick = {
                            selectedTippType = type
                            tippTypeMenuExpanded = false
                        }
                    )
                }
            }
        }

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
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedDay = datePickerState.selectedDateMillis?.let { millis ->
                            Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
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
            }
        ) {
            DatePicker(state = datePickerState)
        }
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
