package com.tanvir.reminder.feature.home.viewmodel

import com.tanvir.reminder.domain.model.Reminder

data class HomeState(
    val greeting: String = "",
    val userName: String = "",
    val lastSelectedDate: String,
    val reminders: List<Reminder> = emptyList()
)
