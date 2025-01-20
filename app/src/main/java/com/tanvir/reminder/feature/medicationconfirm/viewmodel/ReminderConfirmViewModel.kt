package com.tanvir.reminder.feature.medicationconfirm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tanvir.reminder.ReminderNotificationService
import com.tanvir.reminder.feature.medicationconfirm.usecase.AddReminderUseCase
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

    fun addMedication(state: ReminderConfirmState) {
        viewModelScope.launch {
            val reminders = state.reminders
            addReminderUseCase.addMedication(reminders).collect { savedMedications ->
                // Schedule notifications for saved medications that have proper IDs
                savedMedications.forEach { medication ->
                    reminderNotificationService.scheduleNotification(
                        reminder = medication
                    )
                }
                _isReminderSaved.emit(Unit)
            }
        }
    }

}
