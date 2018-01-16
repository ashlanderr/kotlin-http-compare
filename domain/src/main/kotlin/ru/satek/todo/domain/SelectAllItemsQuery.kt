package ru.satek.todo.domain

import java.util.*

class SelectAllItemsQuery(private val user: UUID) : Query<List<ItemData>> {
    @Throws(UserNotFound::class)
    suspend override fun execute(executor: Executor): List<ItemData> {
        assertUser(user, executor.existsUser)
        return executor.items
    }
}