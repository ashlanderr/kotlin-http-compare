package ru.satek.todo.domain

import java.util.*

class SelectOpenTasksQuery(private val user: UUID) : Query<List<TaskAndId>> {
    @Throws(UserNotFound::class)
    suspend override fun execute(executor: Executor): List<TaskAndId> {
        assertUser(executor.existsUser, user)
        return executor.tasks
                .filterNot { it.value.completed }
                .map { TaskAndId(it.key, it.value.content, it.value.completed) }
    }
}