package com.tanvir.reminder.feature.addreminder.viewmodel

import androidx.lifecycle.ViewModel
import com.tanvir.reminder.domain.model.Reminder
import com.tanvir.reminder.feature.addreminder.model.CalendarInformation
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddReminderViewModel @Inject constructor(
) : ViewModel() {

    fun createReminders(
        title: String,
        description: String,
        recurrence: String,
        endDate: Date,
        reminderTimes: CalendarInformation,
        startDate: Date = Date()
    ): List<Reminder> {

        // Determine the recurrence interval based on the selected recurrence
        val interval: Double = when (recurrence) {
            "15 Minutes" -> 0.0104
            "30 Minutes" -> 0.0208
            "45 Minutes" -> 0.0312
            "Hourly" -> 0.0417
            "Daily" -> 1.0
            "Weekly" -> 7.0
            "Monthly" -> 30.0
            else -> {
                0.0104
            }
        }

        val oneDayInMillis = 86400 * 1000 // Number of milliseconds in one day
        val numOccurrences =
            ((endDate.time + oneDayInMillis - startDate.time) / (interval * oneDayInMillis)).toInt() + 1

        val reminders = mutableListOf<Reminder>()
        val calendar = Calendar.getInstance()
        calendar.time = startDate
        val reminder = Reminder(
            id = 0,
            title = title,
            description = description,
            recurrence = recurrence,
            endDate = endDate,
            isCompleted = false,
            time = getReminderTime(reminderTimes, calendar),
            isFromServer = false
        )
        reminders.add(reminder)

        // Increment the date based on the recurrence interval
        if (interval < 1) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        } else {
            calendar.add(Calendar.DAY_OF_YEAR, interval.toInt())
        }

        return reminders
    }

    private fun getReminderTime(reminderTime: CalendarInformation, calendar: Calendar): Date {
        calendar.set(Calendar.HOUR_OF_DAY, reminderTime.dateInformation.hour)
        calendar.set(Calendar.MINUTE, reminderTime.dateInformation.minute)
        return calendar.time
    }
}
