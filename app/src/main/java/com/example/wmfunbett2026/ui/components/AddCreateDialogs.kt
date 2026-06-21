package com.example.wmfunbett2026.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.focusable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.wmfunbett2026.data.model.TimeScope
import com.example.wmfunbett2026.ui.theme.DangerRed
import com.example.wmfunbett2026.ui.theme.JackpotGold
import com.example.wmfunbett2026.ui.theme.PrimaryBlue
import com.example.wmfunbett2026.ui.theme.PrimaryText
import com.example.wmfunbett2026.ui.theme.SecondaryText
import com.example.wmfunbett2026.ui.theme.SurfaceDark
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private enum class MatchdayChoice(val label: String) {
    NONE(""),
    MD1("Matchday 1"),
    MD2("Matchday 2"),
    MD3("Matchday 3"),
    CUSTOM("")
}

private enum class DateChoice { NONE, TODAY, TOMORROW, CUSTOM }

private enum class TimeChoice(val label: String) {
    NONE(""),
    T1400("14:00"),
    T1700("17:00"),
    T2000("20:00"),
    CUSTOM("")
}

@Composable
fun AddTournamentDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, note: String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Tournament") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        errorMessage = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Name") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
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
                    if (name.isBlank()) {
                        errorMessage = "Name is required"
                    } else {
                        onSave(name, note.takeIf { it.isNotBlank() })
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

    LaunchedEffect(Unit) {
        blockInitialFocus.requestFocus()
        focusManager.clearFocus(force = true)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Game") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .height(0.dp)
                        .focusRequester(blockInitialFocus)
                        .focusable()
                )
                OutlinedTextField(
                    value = teamA,
                    onValueChange = {
                        teamA = it
                        errorMessage = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Team A") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = teamB,
                    onValueChange = {
                        teamB = it
                        errorMessage = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Team B") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(14.dp))
                PickerSectionLabel("Matchday")
                Spacer(modifier = Modifier.height(6.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PickerChip("Matchday 1", matchdayChoice == MatchdayChoice.MD1) {
                            matchdayChoice = MatchdayChoice.MD1
                            errorMessage = null
                        }
                        PickerChip("Matchday 2", matchdayChoice == MatchdayChoice.MD2) {
                            matchdayChoice = MatchdayChoice.MD2
                            errorMessage = null
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PickerChip("Matchday 3", matchdayChoice == MatchdayChoice.MD3) {
                            matchdayChoice = MatchdayChoice.MD3
                            errorMessage = null
                        }
                        PickerChip("Custom", matchdayChoice == MatchdayChoice.CUSTOM) {
                            matchdayChoice = MatchdayChoice.CUSTOM
                            errorMessage = null
                        }
                    }
                }
                if (matchdayChoice == MatchdayChoice.CUSTOM) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customMatchdayLabel,
                        onValueChange = {
                            customMatchdayLabel = it
                            errorMessage = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Custom matchday") },
                        placeholder = { Text("Round of 16") },
                        singleLine = true
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                PickerSectionLabel("Date (optional)")
                Spacer(modifier = Modifier.height(6.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PickerChip("Today", dateChoice == DateChoice.TODAY) {
                            dateChoice = DateChoice.TODAY
                            errorMessage = null
                        }
                        PickerChip("Tomorrow", dateChoice == DateChoice.TOMORROW) {
                            dateChoice = DateChoice.TOMORROW
                            errorMessage = null
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PickerChip("Custom", dateChoice == DateChoice.CUSTOM) {
                            dateChoice = DateChoice.CUSTOM
                            errorMessage = null
                        }
                    }
                }
                if (dateChoice == DateChoice.CUSTOM) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customDateLabel,
                        onValueChange = {
                            customDateLabel = it
                            errorMessage = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Custom date") },
                        placeholder = { Text("Sat 21 Jun") },
                        singleLine = true
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                PickerSectionLabel("Time (optional)")
                Spacer(modifier = Modifier.height(6.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PickerChip("14:00", timeChoice == TimeChoice.T1400) {
                            timeChoice = TimeChoice.T1400
                            errorMessage = null
                        }
                        PickerChip("17:00", timeChoice == TimeChoice.T1700) {
                            timeChoice = TimeChoice.T1700
                            errorMessage = null
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PickerChip("20:00", timeChoice == TimeChoice.T2000) {
                            timeChoice = TimeChoice.T2000
                            errorMessage = null
                        }
                        PickerChip("Custom", timeChoice == TimeChoice.CUSTOM) {
                            timeChoice = TimeChoice.CUSTOM
                            errorMessage = null
                        }
                    }
                }
                if (timeChoice == TimeChoice.CUSTOM) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customTimeLabel,
                        onValueChange = {
                            customTimeLabel = it
                            errorMessage = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Custom time") },
                        placeholder = { Text("20:00") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                val previewDate = resolveDateLabel(dateChoice, customDateLabel)
                val previewTime = resolveTimeLabel(timeChoice, customTimeLabel)
                if (previewDate != null || previewTime != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Schedule: ${listOfNotNull(previewDate, previewTime).joinToString(" · ")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryText
                    )
                }
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
                    val resolvedMatchday = resolveMatchdayLabel(matchdayChoice, customMatchdayLabel)
                    val resolvedDate = resolveDateLabel(dateChoice, customDateLabel)
                    val resolvedTime = resolveTimeLabel(timeChoice, customTimeLabel)
                    when {
                        teamA.isBlank() -> errorMessage = "Team A is required"
                        teamB.isBlank() -> errorMessage = "Team B is required"
                        resolvedMatchday == null -> errorMessage = "Select a matchday"
                        matchdayChoice == MatchdayChoice.CUSTOM && customMatchdayLabel.isBlank() ->
                            errorMessage = "Enter a custom matchday"
                        dateChoice == DateChoice.CUSTOM && customDateLabel.isBlank() ->
                            errorMessage = "Enter a custom date or pick Today/Tomorrow"
                        timeChoice == TimeChoice.CUSTOM && customTimeLabel.isBlank() ->
                            errorMessage = "Enter a custom time or pick a preset"
                        else -> onSave(
                            resolvedMatchday,
                            teamA,
                            teamB,
                            resolvedDate,
                            resolvedTime
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
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("No Tipp type available") },
            text = { Text("No Tipp type is available for this game anymore.") },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("OK")
                }
            }
        )
        return
    }

    var expanded by remember { mutableStateOf(false) }
    var selectedScope by remember(availableScopes) {
        mutableStateOf(initialScope)
    }
    val autoTitle = selectedScope.defaultTippTitle()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Tipp Group") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (noMatchTimeNote != null) {
                    Text(
                        text = noMatchTimeNote,
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryText
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedScope.label,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        label = { Text("Time scope") },
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
                    text = "Tipp title: $autoTitle",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryText
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(autoTitle, selectedScope) }) {
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
private fun PickerSectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = SecondaryText
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PickerChip(
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

private fun formatGameDateLabel(date: LocalDate): String =
    date.format(DateTimeFormatter.ofPattern("EEE d MMM", Locale.ENGLISH))

private fun resolveMatchdayLabel(choice: MatchdayChoice, customLabel: String): String? {
    return when (choice) {
        MatchdayChoice.NONE -> null
        MatchdayChoice.MD1, MatchdayChoice.MD2, MatchdayChoice.MD3 -> choice.label
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
        TimeChoice.T1400, TimeChoice.T1700, TimeChoice.T2000 -> choice.label
        TimeChoice.CUSTOM -> customLabel.trim().takeIf { it.isNotEmpty() }
    }
}
