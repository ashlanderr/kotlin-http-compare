package ru.satek.todo.domain

import java.util.*

class SelectTaskByIdQuery(private val user: UUID, private val taskId: UUID) : Query<TaskData> {
    @Throws(UserNotFound::class, TaskNotFound::class)
    suspend override fun execute(executor: Executor): TaskData {
        assertUser(user, executor.existsUser)
        return executor.tasks[taskId] ?: throw TaskNotFound()
    }
}