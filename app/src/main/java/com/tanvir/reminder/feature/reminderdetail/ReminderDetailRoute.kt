package com.tanvir.reminder.feature.reminderdetail

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tanvir.reminder.R
import com.tanvir.reminder.domain.model.Reminder
import com.tanvir.reminder.extension.toFormattedDateString
import com.tanvir.reminder.feature.addreminder.EndDatePickerDialog
import com.tanvir.reminder.feature.addreminder.TimePickerDialogComponent
import com.tanvir.reminder.feature.addreminder.model.CalendarInformation
import com.tanvir.reminder.feature.reminderdetail.viewmodel.ReminderDetailViewModel
import com.tanvir.reminder.util.HOUR_MINUTE_FORMAT
import com.tanvir.reminder.util.Recurrence
import com.tanvir.reminder.util.SnackbarUtil.Companion.showSnackbar
import com.tanvir.reminder.util.getRecurrenceList
import java.util.Calendar
import java.util.Date

@Composable
fun ReminderDetailRoute(
    reminderId: Long?,
    onBackClicked: () -> Unit,
    viewModel: ReminderDetailViewModel = hiltViewModel()
) {
    val reminder by viewModel.reminder.collectAsState()

    LaunchedEffect(Unit) {
        reminderId?.let {
            viewModel.getReminderById(it)
        }
    }

    reminder?.let {
        ReminderDetailScreen(
            reminder = it,
            viewModel = viewModel,
            onBackClicked = onBackClicked
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDetailScreen(
    reminder: Reminder,
    viewModel: ReminderDetailViewModel,
    onBackClicked: () -> Unit
) {
    var isCompletedTapped by remember(reminder.isCompleted) {
        mutableStateOf(reminder.isCompleted)
    }
    var isSkippedTapped by remember(reminder.isCompleted) {
        mutableStateOf(!reminder.isCompleted)
    }
    var title by remember { mutableStateOf(reminder.title) }

    var description by remember { mutableStateOf(reminder.description) }

    var endDate by rememberSaveable { mutableLongStateOf(reminder.endDate.time) }

    val selectedTimes = rememberSaveable(saver = CalendarInformation.getStateListSaver()) {
        mutableStateListOf(CalendarInformation(Calendar.getInstance()))
    }

    var recurrence by rememberSaveable { mutableStateOf(reminder.recurrence) }

    val context = LocalContext.current

    fun removeTime(time: CalendarInformation) {
        selectedTimes.remove(time)
    }

    LaunchedEffect(reminder) {
        isCompletedTapped = reminder.isCompleted
        isSkippedTapped = !reminder.isCompleted
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(vertical = 16.dp),
                navigationIcon = {
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween) {

                        FloatingActionButton(
                            modifier = Modifier,
                            onClick = {
                                onBackClicked()
                            },
                            elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = stringResource(id = R.string.back)
                            )
                        }

                        FloatingActionButton(
                            onClick = {
                                showSnackbar(context.getString(R.string.todo_task_deleted))
                                viewModel.deleteTask(reminder, isCompletedTapped, title = title, description = description, endDate = Date(endDate), time = Date(selectedTimes.first().getTimeInMillis()))
                                onBackClicked()
                            },
                            elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.delete)
                            )
                        }
                    }

                },
                title = {}
            )
        },
        bottomBar = {
            Column {
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    SegmentedButton(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        selected = isCompletedTapped,
                        shape = MaterialTheme.shapes.extraLarge,
                        onClick = {
                            isCompletedTapped = !isCompletedTapped
                            if (isCompletedTapped) {
                                isSkippedTapped = false
                            }
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.completed),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    SegmentedButton(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        selected = isSkippedTapped,
                        shape = MaterialTheme.shapes.extraLarge,
                        onClick = {
                            isSkippedTapped = !isSkippedTapped
                            if (isSkippedTapped) {
                                isCompletedTapped = false
                            }
                        }
                    ) {
                        Text(
                            text = stringResource(id = R.string.skipped),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                val context = LocalContext.current

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .height(56.dp),
                    onClick = {
                        showSnackbar(context.getString(R.string.todo_task_logged))
                        viewModel.updateReminder(reminder, isCompletedTapped, title = title, description = description, endDate = Date(endDate), time = Date(selectedTimes.first().getTimeInMillis()),  recurrence = recurrence)
                        onBackClicked()
                    },
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Text(
                        text = stringResource(R.string.update),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.title)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )


            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.description)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            EndDateTextField(reminder) {
                endDate = it
            }

            for (index in selectedTimes.indices) {
                TimerTextField(
                    isLastItem = selectedTimes.lastIndex == index,
                    isOnlyItem = selectedTimes.size == 1,
                    time = {
                        selectedTimes[index] = it
                    },
                    reminder = reminder,
                    onDeleteClick = { removeTime(selectedTimes[index]) },
                    logEvent = {
                    },
                )
            }

            RecurrenceDropdownMenu(reminder = reminder) { recurrence = it }

        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EndDateTextField(reminder: Reminder,endDate: (Long) -> Unit) {
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
        initialSelectedDateMillis = reminder.endDate.time,
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
        label = { Text(stringResource(R.string.end_date_new)) },
        onValueChange = {},
        trailingIcon = { Icons.Default.DateRange },
        interactionSource = interactionSource
    )
}

@Composable
fun TimerTextField(
    isLastItem: Boolean,
    isOnlyItem: Boolean,
    reminder: Reminder,
    time: (CalendarInformation) -> Unit,
    onDeleteClick: () -> Unit,
    logEvent: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed: Boolean by interactionSource.collectIsPressedAsState()
    val currentTime = CalendarInformation(millisecondsToCalendar(reminder.time.time))
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
        label = { Text(stringResource(R.string.time_s_for_todo)) },
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        value = selectedTime.getDateFormatted(HOUR_MINUTE_FORMAT),
        onValueChange = {},
        trailingIcon = {
            if (isLastItem && !isOnlyItem) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        },
        interactionSource = interactionSource
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurrenceDropdownMenu(reminder: Reminder,recurrence: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(id = R.string.recurrence),
            style = MaterialTheme.typography.bodyLarge
        )

        val options = getRecurrenceList().map { it.displayName }
        var expanded by remember { mutableStateOf(false) }
        var selectedOptionText by remember { mutableStateOf(reminder.recurrence) }
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

fun millisecondsToCalendar(milliseconds: Long): Calendar {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = milliseconds
    return calendar
}


