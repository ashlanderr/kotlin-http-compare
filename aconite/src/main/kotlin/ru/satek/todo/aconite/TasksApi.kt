package ru.satek.todo.aconite

import io.aconite.annotations.Body
import io.aconite.annotations.GET
import io.aconite.annotations.POST
import io.aconite.annotations.Path
import ru.satek.todo.domain.TaskAndId
import ru.satek.todo.domain.TaskData
import java.util.*

interface TasksApi {
    @POST("/tasks")
    suspend fun addTask(@Body task: TaskData): UUID

    @GET("/tasks")
    suspend fun selectAll(): List<TaskAndId>

    @GET("/tasks/completed")
    suspend fun selectCompleted(): List<TaskAndId>

    @GET("/tasks/open")
    suspend fun selectOpen(): List<TaskAndId>

    @GET("/tasks/{id}")
    suspend fun selectById(@Path id: UUID): TaskData

    @POST("/tasks/{id}/complete")
    suspend fun complete(@Path id: UUID)
}