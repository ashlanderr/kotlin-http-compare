package ru.satek.todo.aconite.impl

import io.aconite.HttpException
import io.aconite.annotations.BeforeRequest
import io.aconite.annotations.Header
import io.aconite.serializers.Cookie
import ru.satek.todo.aconite.TasksApi
import ru.satek.todo.domain.*
import java.util.*

class TasksImpl(private val executor: Executor) : TasksApi {
    lateinit var user: UUID

    @BeforeRequest
    @Suppress("unused")
    suspend fun parseAuth(@Header("Cookie") cookie: Cookie? = null) {
        user = cookie?.data?.get("Session")?.let { UUID.fromString(it) } ?: throw HttpException(401, "Unauthorized")
    }

    suspend override fun addTask(task: TaskData): UUID {
        return executor.execute(AddTaskCommand(user, task))
    }

    suspend override fun selectAll(): List<TaskAndId> {
        return executor.execute(SelectAllTasksQuery(user))
    }

    suspend override fun selectCompleted(): List<TaskAndId> {
        return executor.execute(SelectCompletedTasksQuery(user))
    }

    suspend override fun selectOpen(): List<TaskAndId> {
        return executor.execute(SelectOpenTasksQuery(user))
    }

    suspend override fun selectById(id: UUID): TaskData {
        return executor.execute(SelectTaskByIdQuery(user, id))
    }

    suspend override fun complete(id: UUID) {
        return executor.execute(CompleteTaskCommand(user, id))
    }
}