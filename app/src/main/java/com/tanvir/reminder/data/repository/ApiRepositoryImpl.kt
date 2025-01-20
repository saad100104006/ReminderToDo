package com.tanvir.reminder.data.repository

import com.tanvir.reminder.domain.model.Reminder
import com.tanvir.reminder.domain.repository.ApiRepository
import com.tanvir.reminder.data.remote.api_service.ApiService
import com.tanvir.reminder.data.mapper.toTask

class ApiRepositoryImpl(
    private val api: ApiService,
) : ApiRepository {

    override suspend fun getAllToDos(): List<Reminder> {
        return api.getToDos().map {
            it.toTask()
        }
    }
}
