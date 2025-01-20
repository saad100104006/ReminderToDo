package com.tanvir.reminder.data.repository

import com.tanvir.reminder.data.ReminderDao
import com.tanvir.reminder.data.mapper.toDo
import com.tanvir.reminder.data.mapper.toDoEntity
import com.tanvir.reminder.domain.model.Reminder
import com.tanvir.reminder.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class ReminderRepositoryImpl(
    private val dao: ReminderDao,
) : ReminderRepository {

    override suspend fun insertReminders(reminders: List<Reminder>): Flow<List<Reminder>> = flow {
        val savedIds = reminders.map { reminder ->
            dao.insertReminder(reminder.toDoEntity())
        }
        // Get the saved reminders with their IDs
        val savedReminders = reminders.mapIndexed { index, reminder ->
            reminder.copy(id = savedIds[index])
        }
        emit(savedReminders)
    }

    override suspend fun deleteReminder(reminder: Reminder) {
        dao.deleteReminder(reminder.toDoEntity())
    }

    override suspend fun updateReminders(reminder: Reminder) {
        dao.updateReminder(reminder.toDoEntity())
    }

    override fun getAllReminders(): Flow<List<Reminder>> {
        return dao.getAllReminder().map { reminders ->
            reminders.map { it.toDo() }
        }
    }

    override fun getRemindersForDate(date: String): Flow<List<Reminder>> {
        return dao.getRemindersForDate(
            date = date
        ).map { reminders ->
            reminders.map { it.toDo() }
        }
    }

    override suspend fun getRemindersById(id: Long): Reminder? {
        return dao.getReminderById(id)?.toDo()
    }
}
