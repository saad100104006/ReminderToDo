package com.tanvir.reminder.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.tanvir.reminder.data.entity.ToDoEntity
import com.tanvir.reminder.domain.model.Reminder
import com.tanvir.reminder.domain.model.ToDoApiResponse
import com.tanvir.reminder.util.Recurrence
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import kotlin.random.Random

fun ToDoEntity.toDo(): Reminder {
    return Reminder(
        id = id,
        title = title,
        description = description,
        recurrence = recurrence,
        endDate = endDate,
        time = reminderTime,
        isCompleted = taskDone,
        isFromServer = false
    )
}

fun Reminder.toDoEntity(): ToDoEntity {
    return ToDoEntity(
        id = id,
        title = title,
        description = description,
        recurrence = recurrence,
        endDate = endDate,
        reminderTime = time,
        taskDone = isCompleted,
    )
}

fun ToDoApiResponse.toTask(): Reminder {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Reminder(
            id = 10000 - id.toLong(),
            title = title,
            description = title,
            recurrence = Recurrence.Monthly.displayName,
            endDate = getRandomDateAfter30Days(),
            isCompleted = false,
            time = Date(),
            isFromServer = true

        )
    } else {
        Reminder(
            id = 10000 - id.toLong(),
            title = title.take(30),
            description = title,
            recurrence = Recurrence.Monthly.displayName,
            endDate = getDateAfter5Days(),
            isCompleted = false,
            time = Date(),
            isFromServer = true

        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getRandomDateAfter30Days(): Date {
    val currentDate = LocalDate.now()
    val dateAfter30Days = currentDate.plusDays(30)
    val randomDays = Random.nextLong(0, 60)
    val finalDate = dateAfter30Days.plusDays(randomDays)
    return Date.from(finalDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
}

fun getDateAfter5Days(): Date {
    val currentDate = Date()
    val calendar = Calendar.getInstance()
    calendar.time = currentDate
    calendar.add(Calendar.DAY_OF_MONTH, 5)
    return calendar.time
}
