package com.tanvir.reminder.domain.model
import com.google.gson.annotations.SerializedName


data class ToDoApiResponse(
    @SerializedName("userId") val userId: Int,
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("completed") val completed: Boolean
)
