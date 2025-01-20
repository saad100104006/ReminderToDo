package com.tanvir.reminder.domain.repository

import com.tanvir.reminder.domain.model.Reminder

interface ApiRepository {

     suspend fun getAllToDos(): List<Reminder>

}
