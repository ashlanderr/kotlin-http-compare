package ru.satek.todo.domain

import java.util.*

class SelectAllTasksQuery(private val user: UUID) : Query<List<TaskAndId>> {
    @Throws(UserNotFound::class)
    suspend override fun execute(executor: Executor): List<TaskAndId> {
        assertUser(user, executor.existsUser)
        return executor.tasks.map { TaskAndId(it.key, it.value.content, it.value.completed) }
    }
}