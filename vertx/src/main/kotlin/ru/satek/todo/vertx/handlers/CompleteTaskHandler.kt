package ru.satek.todo.vertx.handlers

import ru.satek.todo.domain.*
import ru.satek.todo.vertx.HttpException
import java.util.*

class CompleteTaskHandler(private val executor: Executor) : AbstractTaskHandler() {
    suspend override fun handle() {
        val user = retrieveUser()
        val id = UUID.fromString(request.getParam("id"))
        executor.execute(CompleteTaskCommand(user, id))
        success("OK")
    }
}