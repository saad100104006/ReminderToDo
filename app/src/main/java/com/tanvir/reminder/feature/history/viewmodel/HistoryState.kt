package com.tanvir.reminder.feature.history.viewmodel

import com.tanvir.reminder.domain.model.Reminder

data class HistoryState(
    val reminders: List<Reminder> = emptyList()
)
