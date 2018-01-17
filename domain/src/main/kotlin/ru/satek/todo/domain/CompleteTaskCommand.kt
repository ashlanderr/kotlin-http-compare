package ru.satek.todo.domain

import java.util.*

class CompleteTaskCommand(private val user: UUID, private val taskId: UUID) : Command<Unit> {
    @Throws(UserNotFound::class, TaskNotFound::class)
    suspend override fun execute(executor: Executor) {
        assertUser(user, executor.existsUser)
        val task = executor.tasks[taskId] ?: throw TaskNotFound()
        executor.tasks[taskId] = task.copy(completed = true)
    }
}