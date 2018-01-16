package ru.satek.todo.domain

import java.util.*

class Executor {
    internal val existsUser = UUID.randomUUID()
    internal val email = "test@local"
    internal val password = "qwerty"
    internal val items = mutableListOf<ItemData>()

    @Throws(DomainException::class)
    suspend fun <R> execute(command: Command<R>): R {
        return command.execute(this)
    }

    @Throws(DomainException::class)
    suspend fun <R> execute(query: Query<R>): R {
        return query.execute(this)
    }
}