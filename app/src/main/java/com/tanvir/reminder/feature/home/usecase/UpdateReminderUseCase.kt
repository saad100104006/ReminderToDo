package com.tanvir.reminder.feature.home.usecase

import com.tanvir.reminder.domain.model.Reminder
import com.tanvir.reminder.domain.repository.ReminderRepository
import javax.inject.Inject

class UpdateReminderUseCase @Inject constructor(
    private val repository: ReminderRepository
) {

    suspend fun updateReminder(reminder: Reminder) {
        return repository.updateReminders(reminder)
    }
}
