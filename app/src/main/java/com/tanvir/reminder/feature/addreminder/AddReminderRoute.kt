package com.tanvir.reminder.feature.addreminder

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tanvir.reminder.domain.model.Reminder
import com.tanvir.reminder.extension.toFormattedDateString
import com.tanvir.reminder.feature.addreminder.model.CalendarInformation
import com.tanvir.reminder.feature.addreminder.viewmodel.AddReminderViewModel
import com.tanvir.reminder.util.HOUR_MINUTE_FORMAT
import com.tanvir.reminder.util.Recurrence
import com.tanvir.reminder.util.SnackbarUtil.Companion.showSnackbar
import com.tanvir.reminder.util.getRecurrenceList
import java.util.Calendar
import java.util.Date
import com.tanvir.reminder.R

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AddReminderRoute(onBackClicked = {}, navigateToReminderConfirm = {})
}

@Composable
fun AddReminderRoute(
    onBackClicked: () -> Unit,
    navigateToReminderConfirm: (List<Reminder>) -> Unit,
    viewModel: AddReminderViewModel = hiltViewModel()
) {
    AddToDoScreen(onBackClicked, viewModel, navigateToReminderConfirm)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToDoScreen(
    onBackClicked: () -> Unit,
    viewModel: AddReminderViewModel,
    navigateToDoConfirm: (List<Reminder>) -> Unit,
) {
    var toDoTitle by rememberSaveable { mutableStateOf("") }
    var toDoDescription by rememberSaveable { mutableStateOf("") }
    var recurrence by rememberSaveable { mutableStateOf(Recurrence.FifteenMinutes.name) }
    var endDate by rememberSaveable { mutableLongStateOf(Date().time) }
    val selectedTimes = rememberSaveable(saver = CalendarInformation.getStateListSaver()) {
        mutableStateListOf(CalendarInformation(Calendar.getInstance()))
    }
    val context = LocalContext.current

    fun removeTime(time: CalendarInformation) {
        selectedTimes.remove(time)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .padding(vertical = 16.dp),
                navigationIcon = {
                    FloatingActionButton(
                        onClick = {
                            onBackClicked()
                        },
                        elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                title = {
                    Text(
                        modifier = Modifier.padding(4.dp),
                        text = "Add ToDo",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.displaySmall
                    )
                }
            )
        },
        bottomBar = {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(56.dp),
                onClick = {
                    validateReminder(
                        title = toDoTitle,
                        description = toDoDescription?:"",
                        recurrence = recurrence,
                        endDate = endDate,
                        selectedTimes = selectedTimes.first(),
                        onInvalidate = {
                            val invalidatedValue = context.getString(it)
                            showSnackbar(
                                context.getString(
                                    R.string.value_is_empty,
                                    invalidatedValue
                                )
                            )
                        },
                        onValidate = {
                            navigateToDoConfirm(it)
                        },
                        viewModel = viewModel
                    )
                },
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Text(
                    text = stringResource(id = R.string.next),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = "Title",
                style = MaterialTheme.typography.bodyLarge
            )
            TextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = toDoTitle,
                onValueChange = { toDoTitle = it },
                placeholder = {
                    Text(
                        text = "e.g. Take a walk"
                    )
                },
            )

            Spacer(modifier = Modifier.padding(4.dp))

            var isMaxDoseError by rememberSaveable { mutableStateOf(false) }
            val maxDose = 400


            Column{
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.bodyLarge
                )
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = toDoDescription,
                    onValueChange = {
                        if (it.length < maxDose) {
                            isMaxDoseError = false
                            toDoDescription = it
                        } else {
                            isMaxDoseError = true
                        }
                    },
                    trailingIcon = {
                        if (isMaxDoseError) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = stringResource(R.string.error),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    placeholder = {
                        Text(
                            text = "e.g. I need to go out for a walk at 5 PM sharp"
                        )
                    },
                    isError = isMaxDoseError
                )
            }
            Spacer(modifier = Modifier.padding(4.dp))

            RecurrenceDropdownMenu { recurrence = it }

            if (isMaxDoseError) {
                Text(
                    text = "You can't write more than 400 characters",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Spacer(modifier = Modifier.padding(4.dp))
            EndDateTextField { endDate = it }

            Spacer(modifier = Modifier.padding(4.dp))
            Text(
                text = stringResource(R.string.times_for_todo),
                style = MaterialTheme.typography.bodyLarge
            )

            for (index in selectedTimes.indices) {
                TimerTextField(
                    isLastItem = selectedTimes.lastIndex == index,
                    isOnlyItem = selectedTimes.size == 1,
                    time = {
                        selectedTimes[index] = it
                    },
                    onDeleteClick = { removeTime(selectedTimes[index]) },
                    logEvent = {
                    },
                )
            }
        }
    }
}

private fun validateReminder(
    title: String,
    description: String,
    recurrence: String,
    endDate: Long,
    selectedTimes: CalendarInformation,
    onInvalidate: (Int) -> Unit,
    onValidate: (List<Reminder>) -> Unit,
    viewModel: AddReminderViewModel
) {
    if (title.isEmpty()) {
        onInvalidate(R.string.reminder_name)
        return
    }

    if (description.isEmpty()) {
        onInvalidate(R.string.description)
        return
    }

    if (endDate < 1) {
        onInvalidate(R.string.end_date)
        return
    }

    val reminders =
        viewModel.createReminders(title, description, recurrence, Date(endDate), selectedTimes)

    onValidate(reminders)
}

private fun handleSelection(
    isSelected: Boolean,
    selectionCount: Int,
    canSelectMoreTimesOfDay: Boolean,
    onStateChange: (Int, Boolean) -> Unit,
    onShowMaxSelectionError: () -> Unit
) {
    if (isSelected) {
        onStateChange(selectionCount - 1, !isSelected)
    } else {
        if (canSelectMoreTimesOfDay) {
            onStateChange(selectionCount + 1, !isSelected)
        } else {
            onShowMaxSelectionError()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurrenceDropdownMenu(recurrence: (String) -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(id = R.string.recurrence),
            style = MaterialTheme.typography.bodyLarge
        )

        val options = getRecurrenceList().map { it.displayName }
        var expanded by remember { mutableStateOf(false) }
        var selectedOptionText by remember { mutableStateOf(options[0]) }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            TextField(
                modifier = Modifier.menuAnchor(),
                readOnly = true,
                value = selectedOptionText,
                onValueChange = {},
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                options.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            selectedOptionText = selectionOption
                            recurrence(selectionOption)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EndDateTextField(endDate: (Long) -> Unit) {
    Text(
        text = stringResource(id = R.string.end_date),
        style = MaterialTheme.typography.bodyLarge
    )

    var shouldDisplay by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed: Boolean by interactionSource.collectIsPressedAsState()
    if (isPressed) {
        shouldDisplay = true
    }

    val today = Calendar.getInstance()
    today.set(Calendar.HOUR_OF_DAY, 0)
    today.set(Calendar.MINUTE, 0)
    today.set(Calendar.SECOND, 0)
    today.set(Calendar.MILLISECOND, 0)
    val currentDayMillis = today.timeInMillis
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= currentDayMillis
            }
        }
    )

    var selectedDate by rememberSaveable {
        mutableStateOf(
            datePickerState.selectedDateMillis?.toFormattedDateString() ?: ""
        )
    }

    EndDatePickerDialog(
        state = datePickerState,
        shouldDisplay = shouldDisplay,
        onConfirmClicked = { selectedDateInMillis ->
            selectedDate = selectedDateInMillis.toFormattedDateString()
            endDate(selectedDateInMillis)
        },
        dismissRequest = {
            shouldDisplay = false
        }
    )

    TextField(
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        value = selectedDate,
        onValueChange = {},
        trailingIcon = { Icons.Default.DateRange },
        interactionSource = interactionSource
    )
}

@Composable
fun TimerTextField(
    isLastItem: Boolean,
    isOnlyItem: Boolean,
    time: (CalendarInformation) -> Unit,
    onDeleteClick: () -> Unit,
    logEvent: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed: Boolean by interactionSource.collectIsPressedAsState()
    val currentTime = CalendarInformation(Calendar.getInstance())
    var selectedTime by rememberSaveable(
        stateSaver = CalendarInformation.getStateSaver()
    ) { mutableStateOf(currentTime) }

    TimePickerDialogComponent(
        showDialog = isPressed,
        selectedDate = selectedTime,
        onSelectedTime = {
            logEvent.invoke()
            selectedTime = it
            time(it)
        }
    )

    TextField(
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        value = selectedTime.getDateFormatted(HOUR_MINUTE_FORMAT),
        onValueChange = {},
        trailingIcon = {
            // TODO: Make delete action work properly
            if (isLastItem && !isOnlyItem) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        },
        interactionSource = interactionSource
    )
}
