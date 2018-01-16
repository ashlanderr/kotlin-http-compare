package ru.satek.todo.vertx.handlers

import ru.satek.todo.domain.Executor
import ru.satek.todo.domain.SelectAllItemsQuery
import ru.satek.todo.domain.UserNotFound
import ru.satek.todo.vertx.HttpException

class SelectAllItemsHandler(private val executor: Executor) : AbstractHandler() {
    suspend override fun handle() {
        try {
            val user = retrieveUser()
            val items = executor.execute(SelectAllItemsQuery(user))
            success(items)
        } catch (ex: UserNotFound) {
            throw HttpException(403, "Forbidden")
        }
    }
}