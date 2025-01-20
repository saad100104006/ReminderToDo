package com.tanvir.reminder.feature.remiderconfirm.usecase

import com.tanvir.reminder.domain.model.Reminder
import com.tanvir.reminder.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddReminderUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    suspend fun addReminder(reminders: List<Reminder>): Flow<List<Reminder>> = repository.insertReminders(reminders)
}
