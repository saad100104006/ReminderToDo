package com.tanvir.reminder.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Reminder(
    val id: Long = 0,
    val title: String,
    val description: String,
    val recurrence: String,
    val endDate: Date,
    val isCompleted: Boolean,
    val time: Date,
    val isFromServer: Boolean
) : Parcelable
