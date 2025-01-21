package com.tanvir.reminder.feature.home

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.tanvir.reminder.R
import com.tanvir.reminder.domain.model.Reminder
import com.tanvir.reminder.feature.home.model.CalendarModel
import com.tanvir.reminder.feature.home.viewmodel.HomeState
import com.tanvir.reminder.feature.home.viewmodel.HomeViewModel
import com.tanvir.reminder.util.SnackbarUtil.Companion.showSnackbar
import java.util.Date

@Composable
fun HomeRoute(
    navController: NavController,
    askNotificationPermission: Boolean,
    askAlarmPermission: Boolean,
    navigateToReminderDetail: (Reminder) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.homeUiState.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    LaunchedEffect(errorMessage) {
       if(!errorMessage.isNullOrEmpty()){
           showSnackbar(errorMessage)
       }
    }
    PermissionAlarmDialog(
        askAlarmPermission = askAlarmPermission,
    )
    PermissionDialog(
        askNotificationPermission = askNotificationPermission
    )
    HomeScreen(
        modifier = modifier,
        navController = navController,
        state = state,
        navigateToReminderDetail = navigateToReminderDetail,
        onDateSelected = viewModel::selectDate,
        onSelectedDate = { viewModel.updateSelectedDate(it) }
    )
}

@Composable
fun HomeScreen(
    modifier: Modifier,
    navController: NavController,
    state: HomeState,
    navigateToReminderDetail: (Reminder) -> Unit,
    onDateSelected: (CalendarModel.DateModel) -> Unit,
    onSelectedDate: (Date) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DailyReminders(
            navController = navController,
            state = state,
            navigateToReminderDetail = navigateToReminderDetail,
            onSelectedDate = onSelectedDate,
            onDateSelected = onDateSelected
        )
    }
}

@Composable
fun DailyReminders(
    navController: NavController,
    state: HomeState,
    navigateToReminderDetail: (Reminder) -> Unit,
    onSelectedDate: (Date) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    onDateSelected: (CalendarModel.DateModel) -> Unit
) {
    var showAccessibilityDialog by remember { mutableStateOf(false) }
    val toDoList by viewModel.toDoList.collectAsStateWithLifecycle()
    val context = LocalContext.current

    if(showAccessibilityDialog){
        EnableAccessibilityDialog(onConfirm = {
            showAccessibilityDialog = false
            openAccessibilitySettings(context)
        }) {showAccessibilityDialog = false }
    }
    LaunchedEffect(Unit) {
        viewModel.fetchToDos()
    }

    Text(
        modifier = Modifier.padding(top = 8.dp),
        text = stringResource(R.string.app_name),
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.displaySmall
    )


    LazyColumn(
        modifier = Modifier,
    ) {
        items(
            items = state.reminders,
            itemContent = {
                ReminderCard(
                    reminder = it,
                    navigateToReminderDetail = { reminder ->
                        if(isAccessibilityServiceEnabled(context)) {
                            showAccessibilityDialog = false
                            textToSpeech(context, "title" + reminder.title + "description" + reminder.description)
                            navigateToReminderDetail(reminder)
                        } else {
                            showAccessibilityDialog = true
                        }
                    }
                )
            }
        )

        items(
            items = toDoList,
            itemContent = {
                ReminderCard(
                    reminder = it,
                    navigateToReminderDetail = {
                        if(isAccessibilityServiceEnabled(context)) {
                            showAccessibilityDialog = false
                            textToSpeech(context, "title" + it.title + "description" + it.description)
                            showSnackbar(
                                context.getString(R.string.server_todo_reminders_can_t_be_modified)
                            )
                        } else {
                            showAccessibilityDialog = true
                        }
                    }
                )
            }
        )
    }
}

sealed class ReminderListItem {
    data class OverviewItem(
        val remindersToday: List<Reminder>,
        val isReminderListEmpty: Boolean
    ) : ReminderListItem()

    data class ReminderItem(val reminder: Reminder) : ReminderListItem()
    data class HeaderItem(val headerText: String) : ReminderListItem()
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionDialog(
    askNotificationPermission: Boolean
) {
    if (askNotificationPermission && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)) {
        val notificationPermissionState =
            rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS) { isGranted ->
            }
        if (!notificationPermissionState.status.isGranted) {
            val openAlertDialog = remember { mutableStateOf(true) }

            when {
                openAlertDialog.value -> {
                    AlertDialog(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = stringResource(R.string.notifications)
                            )
                        },
                        title = {
                            Text(text = stringResource(R.string.notification_permission_required))
                        },
                        text = {
                            Text(text = stringResource(R.string.notification_permission_required_description_message))
                        },
                        onDismissRequest = {
                            openAlertDialog.value = false
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    notificationPermissionState.launchPermissionRequest()
                                    openAlertDialog.value = false
                                }
                            ) {
                                Text(stringResource(R.string.allow))
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionAlarmDialog(
    askAlarmPermission: Boolean
) {
    val context = LocalContext.current
    val alarmManager = ContextCompat.getSystemService(context, AlarmManager::class.java)
    if (askAlarmPermission && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)) {
        val alarmPermissionState =
            rememberPermissionState(Manifest.permission.SCHEDULE_EXACT_ALARM) { isGranted ->
            }
        if (alarmManager?.canScheduleExactAlarms() == false) {
            val openAlertDialog = remember { mutableStateOf(true) }

            when {
                openAlertDialog.value -> {


                    AlertDialog(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = stringResource(R.string.alarms)
                            )
                        },
                        title = {
                            Text(text = stringResource(R.string.alarms_permission_required))
                        },
                        text = {
                            Text(text = stringResource(R.string.alarms_permission_required_description_message))
                        },
                        onDismissRequest = {
                            openAlertDialog.value = false
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    Intent().also { intent ->
                                        intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                                        context.startActivity(intent)
                                    }

                                    openAlertDialog.value = false
                                }
                            ) {
                                Text(stringResource(R.string.allow))
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EnableAccessibilityDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(stringResource(R.string.enable_accessibility_service_for_text_to_speech_feature))
        },
        text = {
            Text(stringResource(R.string.to_listen_your_reminder_please_enable_the_accessibility_service_in_your_device_settings))
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.enable))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

private fun isAccessibilityServiceEnabled(context: Context): Boolean {
    val serviceName = "com.tanvir.reminder.TTSAccessibilityService"
    val enabledServices =
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
    return !TextUtils.isEmpty(enabledServices) && enabledServices.contains(serviceName)
}

private fun openAccessibilitySettings(context: Context) {
    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
    context.startActivity(intent)
}

private fun textToSpeech(context: Context, text: String){
    val event =
        AccessibilityEvent.obtain(AccessibilityEvent.TYPE_ANNOUNCEMENT)
            .apply {
                className = "com.tanvir.reminder.MainActivity"
                packageName = packageName
                isEnabled = true
                this.text.add(text)
            }

    val accessibilityManager =
        context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    accessibilityManager.sendAccessibilityEvent(event)
}
