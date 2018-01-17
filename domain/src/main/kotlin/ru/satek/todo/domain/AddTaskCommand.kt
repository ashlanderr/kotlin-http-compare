package ru.satek.todo.domain

import java.util.*

class AddTaskCommand(private val user: UUID, private val task: TaskData) : Command<UUID> {
    @Throws(UserNotFound::class)
    suspend override fun execute(executor: Executor): UUID {
        assertUser(user, executor.existsUser)
        val id = UUID.randomUUID()
        executor.tasks[id] = task
        return id
    }
}