package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.focusable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.R
import com.example.wmfunbett2026.data.model.TimeScope
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private enum class MatchdayChoice(val storedLabel: String) {
    NONE(""),
    MD1("Matchday 1"),
    MD2("Matchday 2"),
    MD3("Matchday 3"),
    CUSTOM("")
}

private enum class DateChoice { NONE, TODAY, TOMORROW, CUSTOM }

private enum class TimeChoice(val storedLabel: String) {
    NONE(""),
    T1800("18:00"),
    T2000("20:00"),
    T2100("21:00"),
    CUSTOM("")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTournamentDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, note: String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val errorNameRequired = stringResource(R.string.error_name_required)

    FormBottomSheet(
        title = stringResource(R.string.add_tournament),
        onDismiss = onDismiss,
        primaryActionLabel = stringResource(R.string.save),
        onPrimaryAction = {
            if (name.isBlank()) {
                errorMessage = errorNameRequired
            } else {
                onSave(name, note.takeIf { it.isNotBlank() })
            }
        }
    ) {
        FormOutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                errorMessage = null
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.name)) }
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
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(10.dp))
            FormErrorText(text = errorMessage.orEmpty())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGameDialog(
    onDismiss: () -> Unit,
    onSave: (dayLabel: String, teamA: String, teamB: String, dateLabel: String?, timeLabel: String?) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val blockInitialFocus = remember { FocusRequester() }

    var teamA by remember { mutableStateOf("") }
    var teamB by remember { mutableStateOf("") }
    var matchdayChoice by remember { mutableStateOf(MatchdayChoice.NONE) }
    var customMatchdayLabel by remember { mutableStateOf("") }
    var dateChoice by remember { mutableStateOf(DateChoice.NONE) }
    var customDateLabel by remember { mutableStateOf("") }
    var timeChoice by remember { mutableStateOf(TimeChoice.NONE) }
    var customTimeLabel by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val errorTeamA = stringResource(R.string.error_team_a_required)
    val errorTeamB = stringResource(R.string.error_team_b_required)
    val errorSelectMatchday = stringResource(R.string.error_select_matchday)
    val errorCustomMatchday = stringResource(R.string.error_custom_matchday)
    val errorCustomDate = stringResource(R.string.error_custom_date)
    val errorCustomTime = stringResource(R.string.error_custom_time)

    LaunchedEffect(Unit) {
        blockInitialFocus.requestFocus()
        focusManager.clearFocus(force = true)
    }

    FormBottomSheet(
        title = stringResource(R.string.add_game),
        onDismiss = onDismiss,
        primaryActionLabel = stringResource(R.string.save),
        onPrimaryAction = {
            val resolvedMatchday = resolveMatchdayLabel(matchdayChoice, customMatchdayLabel)
            val resolvedDate = resolveDateLabel(dateChoice, customDateLabel)
            val resolvedTime = resolveTimeLabel(timeChoice, customTimeLabel)
            errorMessage = when {
                teamA.isBlank() -> errorTeamA
                teamB.isBlank() -> errorTeamB
                resolvedMatchday == null -> errorSelectMatchday
                matchdayChoice == MatchdayChoice.CUSTOM && customMatchdayLabel.isBlank() ->
                    errorCustomMatchday
                dateChoice == DateChoice.CUSTOM && customDateLabel.isBlank() ->
                    errorCustomDate
                timeChoice == TimeChoice.CUSTOM && customTimeLabel.isBlank() ->
                    errorCustomTime
                else -> {
                    onSave(resolvedMatchday, teamA, teamB, resolvedDate, resolvedTime)
                    null
                }
            }
        }
    ) {
        Box(
            modifier = Modifier
                .height(0.dp)
                .focusRequester(blockInitialFocus)
                .focusable()
        )

        FormSectionCard(title = stringResource(R.string.matchday)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FormMatchdaySelectCard(
                    label = stringResource(R.string.matchday_1),
                    selected = matchdayChoice == MatchdayChoice.MD1,
                    onClick = {
                        matchdayChoice = MatchdayChoice.MD1
                        errorMessage = null
                    },
                    modifier = Modifier.weight(1f)
                )
                FormMatchdaySelectCard(
                    label = stringResource(R.string.matchday_2),
                    selected = matchdayChoice == MatchdayChoice.MD2,
                    onClick = {
                        matchdayChoice = MatchdayChoice.MD2
                        errorMessage = null
                    },
                    modifier = Modifier.weight(1f)
                )
                FormMatchdaySelectCard(
                    label = stringResource(R.string.matchday_3),
                    selected = matchdayChoice == MatchdayChoice.MD3,
                    onClick = {
                        matchdayChoice = MatchdayChoice.MD3
                        errorMessage = null
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            FormFilterChip(
                label = stringResource(R.string.other_matchday),
                selected = matchdayChoice == MatchdayChoice.CUSTOM
            ) {
                matchdayChoice = MatchdayChoice.CUSTOM
                errorMessage = null
            }
            if (matchdayChoice == MatchdayChoice.CUSTOM) {
                FormOutlinedTextField(
                    value = customMatchdayLabel,
                    onValueChange = {
                        customMatchdayLabel = it
                        errorMessage = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.matchday_name)) },
                    placeholder = { Text("Round of 16") }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        FormSectionCard(title = stringResource(R.string.teams)) {
            FormOutlinedTextField(
                value = teamA,
                onValueChange = {
                    teamA = it
                    errorMessage = null
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.team_a)) }
            )
            FormOutlinedTextField(
                value = teamB,
                onValueChange = {
                    teamB = it
                    errorMessage = null
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.team_b)) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        FormSectionCard(title = stringResource(R.string.date)) {
            val resolvedPreview = resolveDateLabel(dateChoice, customDateLabel)
            if (resolvedPreview != null) {
                Text(
                    text = stringResource(R.string.selected_date, resolvedPreview),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            } else {
                Text(
                    text = stringResource(R.string.date_pick_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FormFilterChip(
                    label = stringResource(R.string.today),
                    selected = dateChoice == DateChoice.TODAY
                ) {
                    dateChoice = DateChoice.TODAY
                    errorMessage = null
                }
                FormFilterChip(
                    label = stringResource(R.string.tomorrow),
                    selected = dateChoice == DateChoice.TOMORROW
                ) {
                    dateChoice = DateChoice.TOMORROW
                    errorMessage = null
                }
                FormFilterChip(
                    label = stringResource(R.string.other_date),
                    selected = dateChoice == DateChoice.CUSTOM
                ) {
                    dateChoice = DateChoice.CUSTOM
                    errorMessage = null
                }
            }
            if (dateChoice == DateChoice.CUSTOM) {
                FormOutlinedTextField(
                    value = customDateLabel,
                    onValueChange = {
                        customDateLabel = it
                        errorMessage = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.other_date)) },
                    placeholder = { Text("Sat 21 Jun") }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        FormSectionCard(title = stringResource(R.string.time)) {
            val resolvedTime = resolveTimeLabel(timeChoice, customTimeLabel)
            if (resolvedTime != null) {
                Text(
                    text = stringResource(R.string.selected_time, resolvedTime),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            } else {
                Text(
                    text = stringResource(R.string.time_pick_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FormFilterChip(
                    label = "18:00",
                    selected = timeChoice == TimeChoice.T1800
                ) {
                    timeChoice = TimeChoice.T1800
                    errorMessage = null
                }
                FormFilterChip(
                    label = "20:00",
                    selected = timeChoice == TimeChoice.T2000
                ) {
                    timeChoice = TimeChoice.T2000
                    errorMessage = null
                }
                FormFilterChip(
                    label = "21:00",
                    selected = timeChoice == TimeChoice.T2100
                ) {
                    timeChoice = TimeChoice.T2100
                    errorMessage = null
                }
            }
            FormFilterChip(
                label = stringResource(R.string.custom_time),
                selected = timeChoice == TimeChoice.CUSTOM
            ) {
                timeChoice = TimeChoice.CUSTOM
                errorMessage = null
            }
            if (timeChoice == TimeChoice.CUSTOM) {
                FormOutlinedTextField(
                    value = customTimeLabel,
                    onValueChange = {
                        customTimeLabel = it
                        errorMessage = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.custom_time)) },
                    placeholder = { Text("20:00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(10.dp))
            FormErrorText(text = errorMessage.orEmpty())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTippGroupDialog(
    availableScopes: List<TimeScope>,
    noMatchTimeNote: String? = null,
    onDismiss: () -> Unit,
    onSave: (title: String, timeScope: TimeScope) -> Unit
) {
    val initialScope = availableScopes.firstOrNull()

    if (initialScope == null) {
        FormBottomSheet(
            title = stringResource(R.string.no_tipp_type_available),
            onDismiss = onDismiss,
            primaryActionLabel = stringResource(R.string.ok),
            onPrimaryAction = onDismiss,
            showCancel = false
        ) {
            Text(
                text = stringResource(R.string.no_tipp_type_message),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    var expanded by remember { mutableStateOf(false) }
    var selectedScope by remember(availableScopes) {
        mutableStateOf(initialScope)
    }
    val autoTitle = selectedScope.defaultTippTitle()

    FormBottomSheet(
        title = stringResource(R.string.add_tipp_group),
        onDismiss = onDismiss,
        primaryActionLabel = stringResource(R.string.save),
        onPrimaryAction = { onSave(autoTitle, selectedScope) }
    ) {
        if (noMatchTimeNote != null) {
            Text(
                text = noMatchTimeNote,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            FormOutlinedTextField(
                value = selectedScope.label,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                label = { Text(stringResource(R.string.time_scope)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                availableScopes.forEach { scope ->
                    DropdownMenuItem(
                        text = { Text(scope.label) },
                        onClick = {
                            selectedScope = scope
                            expanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.tipp_title_label, autoTitle),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun formatGameDateLabel(date: LocalDate): String =
    date.format(DateTimeFormatter.ofPattern("EEE d MMM", Locale.ENGLISH))

private fun resolveMatchdayLabel(choice: MatchdayChoice, customLabel: String): String? {
    return when (choice) {
        MatchdayChoice.NONE -> null
        MatchdayChoice.MD1, MatchdayChoice.MD2, MatchdayChoice.MD3 -> choice.storedLabel
        MatchdayChoice.CUSTOM -> customLabel.trim().takeIf { it.isNotEmpty() }
    }
}

private fun resolveDateLabel(choice: DateChoice, customLabel: String): String? {
    return when (choice) {
        DateChoice.NONE -> null
        DateChoice.TODAY -> formatGameDateLabel(LocalDate.now())
        DateChoice.TOMORROW -> formatGameDateLabel(LocalDate.now().plusDays(1))
        DateChoice.CUSTOM -> customLabel.trim().takeIf { it.isNotEmpty() }
    }
}

private fun resolveTimeLabel(choice: TimeChoice, customLabel: String): String? {
    return when (choice) {
        TimeChoice.NONE -> null
        TimeChoice.T1800, TimeChoice.T2000, TimeChoice.T2100 -> choice.storedLabel
        TimeChoice.CUSTOM -> customLabel.trim().takeIf { it.isNotEmpty() }
    }
}
