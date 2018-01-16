package ru.satek.todo.domain

interface Query<out R> {
    @Throws(DomainException::class)
    suspend fun execute(executor: Executor): R
}