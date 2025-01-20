package com.tanvir.reminder.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class ToDoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val recurrence: String,
    val endDate: Date,
    val taskDone: Boolean,
    val reminderTime: Date
)
