package com.tanvir.reminder.feature.home.usecase

import com.tanvir.reminder.domain.model.Reminder
import com.tanvir.reminder.domain.repository.ReminderRepository
import javax.inject.Inject

class DeleteReminderUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    suspend fun deleteReminder(reminder: Reminder) {
        return repository.deleteReminder(reminder)
    }
}
