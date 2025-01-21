package com.tanvir.reminder.feature.reminderdetail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tanvir.reminder.ReminderNotificationService
import com.tanvir.reminder.domain.model.Reminder
import com.tanvir.reminder.feature.home.usecase.DeleteReminderUseCase
import com.tanvir.reminder.feature.home.usecase.UpdateReminderUseCase
import com.tanvir.reminder.feature.reminderdetail.usecase.GetReminderUseCase
import com.tanvir.reminder.util.Recurrence
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ReminderDetailViewModel @Inject constructor(
    private val getReminderUseCase: GetReminderUseCase,
    private val updateReminderUseCase: UpdateReminderUseCase,
    private val deleteReminderUseCase: DeleteReminderUseCase,
    private val reminderNotificationService: ReminderNotificationService
) : ViewModel() {
    private val _reminder = MutableStateFlow<Reminder?>(null)
    val reminder = _reminder.asStateFlow()

    fun getReminderById(id: Long) {
        viewModelScope.launch {
            _reminder.value = getReminderUseCase(id)
        }
    }

    fun updateReminder(reminder: Reminder, isReminderTaken: Boolean, title: String, description: String, endDate: Date, time: Date, recurrence: String) {
        viewModelScope.launch {
            updateReminderUseCase.updateReminder(reminder.copy(title = title,description = description, isCompleted = isReminderTaken, endDate = endDate, time = time, recurrence = recurrence))
            reminderNotificationService.scheduleNotification(
                reminder = reminder.copy(title = title,description = description, isCompleted = isReminderTaken, endDate = endDate, time = time, recurrence = recurrence)
            )

        }
    }

    fun deleteTask(reminder: Reminder, isReminderTaken: Boolean, title: String, description: String, endDate: Date, time: Date) {
        viewModelScope.launch {
            deleteReminderUseCase.deleteReminder(reminder.copy(title = title,description = description, isCompleted = isReminderTaken, endDate = endDate, time = time))
            delay(1000)
        }
    }
}
