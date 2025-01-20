package com.tanvir.reminder.feature.home.usecase

import com.tanvir.reminder.domain.model.Reminder
import com.tanvir.reminder.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRemindersUseCase @Inject constructor(
    private val repository: ReminderRepository
) {

    fun getReminders(date: String? = null): Flow<List<Reminder>> {
        return if (date != null) {
            repository.getRemindersForDate(date)
        } else repository.getAllReminders()
    }
}
