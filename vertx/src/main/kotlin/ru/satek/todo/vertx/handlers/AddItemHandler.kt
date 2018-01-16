package ru.satek.todo.vertx.handlers

import ru.satek.todo.domain.AddItemCommand
import ru.satek.todo.domain.Executor
import ru.satek.todo.domain.ItemData
import ru.satek.todo.domain.UserNotFound
import ru.satek.todo.vertx.HttpException

class AddItemHandler(private val executor: Executor) : AbstractHandler() {
    suspend override fun handle() {
        try {
            val user = retrieveUser()
            val item = fromJson<ItemData>(context.bodyAsString) ?: throw HttpException(400, "Body must not be empty")
            executor.execute(AddItemCommand(user, item))
            response.end("OK")
        } catch (ex: UserNotFound) {
            throw HttpException(403, "Forbidden")
        }
    }
}