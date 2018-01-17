package ru.satek.todo.vertx.handlers

import ru.satek.todo.domain.Executor
import ru.satek.todo.domain.SelectTaskByIdQuery
import java.util.*

class SelectTaskByIdHandler(private val executor: Executor) : AbstractTaskHandler() {
    suspend override fun handle() {
        val user = retrieveUser()
        val id = UUID.fromString(request.getParam("id"))
        val task = executor.execute(SelectTaskByIdQuery(user, id))
        success(task)
    }
}