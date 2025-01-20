package com.tanvir.reminder.domain.repository

import com.tanvir.reminder.domain.model.Reminder
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {

    suspend fun insertReminders(reminders: List<Reminder>): Flow<List<Reminder>>

    suspend fun updateReminders(reminder: Reminder)

    suspend fun deleteReminder(reminder: Reminder)

    fun getAllReminders(): Flow<List<Reminder>>

    fun getRemindersForDate(date: String): Flow<List<Reminder>>

    suspend fun getRemindersById(id: Long): Reminder?
}
