package com.anton.to_do_list.data.network

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response

data class TaskDto(
    val id: String? = null,
    val name: String,
    val completed: Boolean,
    val photoBase64: String? = null
)
data class TaskResponseDto(
    val id: Int,
    val name: String,
    val completed: Boolean,
    val photoBase64: String?,
    val date: String
)

 data class TasksResponseDto(
    val values: List<TaskResponseDto>
 )

interface TaskApi {
    @POST("/api/getTasks")
    suspend fun getTasks(): Response<TasksResponseDto>

    @POST("/api/addTask")
    suspend fun addTask(@Body task: TaskDto): Response<TasksResponseDto>

    @POST("/api/updateTask")
    suspend fun updateTask(@Body task: TaskDto): Response<TasksResponseDto>
}
