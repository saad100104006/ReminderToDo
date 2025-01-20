package com.tanvir.reminder.feature.medicationconfirm.usecase

import com.tanvir.reminder.domain.model.Reminder
import com.tanvir.reminder.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddReminderUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    suspend fun addMedication(reminders: List<Reminder>): Flow<List<Reminder>> = repository.insertReminders(reminders)
}
