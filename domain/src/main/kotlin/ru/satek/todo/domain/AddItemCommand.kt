package ru.satek.todo.domain

import java.util.*

class AddItemCommand(private val user: UUID, private val item: ItemData) : Command<Unit> {
    @Throws(UserNotFound::class)
    suspend override fun execute(executor: Executor) {
        assertUser(user, executor.existsUser)
        executor.items.add(item)
    }
}