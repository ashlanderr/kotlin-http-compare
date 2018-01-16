package ru.satek.todo.domain

interface Command<out R> {
    @Throws(DomainException::class)
    suspend fun execute(executor: Executor): R
}