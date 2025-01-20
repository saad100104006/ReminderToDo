package com.tanvir.reminder.feature.reminderdetail.usecase

import com.tanvir.reminder.domain.model.Reminder
import com.tanvir.reminder.domain.repository.ReminderRepository
import javax.inject.Inject

class GetReminderUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    suspend operator fun invoke(id: Long): Reminder? = repository.getRemindersById(id)
}
