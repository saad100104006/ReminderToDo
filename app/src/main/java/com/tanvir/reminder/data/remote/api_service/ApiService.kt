package com.tanvir.reminder.data.remote.api_service

import com.tanvir.reminder.domain.model.ToDoApiResponse
import retrofit2.http.GET

interface ApiService {

    @GET("/todos")
    suspend fun getToDos(): List<ToDoApiResponse>

}