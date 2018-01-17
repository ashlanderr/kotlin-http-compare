package ru.satek.todo.vertx.handlers

import ru.satek.todo.domain.Executor
import ru.satek.todo.domain.SelectAllTasksQuery
import ru.satek.todo.domain.SelectOpenTasksQuery
import ru.satek.todo.domain.UserNotFound
import ru.satek.todo.vertx.HttpException

class SelectOpenTasksHandler(private val executor: Executor) : AbstractTaskHandler() {
    suspend override fun handle() {
        val user = retrieveUser()
        val tasks = executor.execute(SelectOpenTasksQuery(user))
        success(tasks)
    }
}