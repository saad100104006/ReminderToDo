package com.tanvir.reminder.feature.remiderconfirm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tanvir.reminder.ReminderNotificationService
import com.tanvir.reminder.feature.remiderconfirm.usecase.AddReminderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderConfirmViewModel @Inject constructor(
    private val addReminderUseCase: AddReminderUseCase,
    private val reminderNotificationService: ReminderNotificationService
) : ViewModel() {
    private val _isReminderSaved = MutableSharedFlow<Unit>()
    val isReminderSaved = _isReminderSaved.asSharedFlow()

    fun addReminder(state: ReminderConfirmState) {
        viewModelScope.launch {
            val reminders = state.reminders
            addReminderUseCase.addReminder(reminders).collect { savedReminders ->
                // Schedule notifications for saved reminders that have proper IDs
                savedReminders.forEach { reminder ->
                    reminderNotificationService.scheduleNotification(
                        reminder = reminder
                    )
                }
                _isReminderSaved.emit(Unit)
            }
        }
    }

}
