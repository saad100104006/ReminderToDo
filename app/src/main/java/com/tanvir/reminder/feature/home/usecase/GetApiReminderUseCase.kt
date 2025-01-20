package com.tanvir.reminder.feature.home.usecase

import com.tanvir.reminder.domain.model.Reminder
import com.tanvir.reminder.domain.repository.ApiRepository
import javax.inject.Inject

class GetApiReminderUseCase @Inject constructor(
    private val repository: ApiRepository
) {

    suspend fun getReminders(): List<Reminder> {
        return repository.getAllToDos()
    }
}
