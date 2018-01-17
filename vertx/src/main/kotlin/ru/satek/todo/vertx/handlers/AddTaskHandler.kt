package ru.satek.todo.vertx.handlers

import ru.satek.todo.domain.AddTaskCommand
import ru.satek.todo.domain.Executor
import ru.satek.todo.domain.TaskData
import ru.satek.todo.domain.UserNotFound
import ru.satek.todo.vertx.HttpException

class AddTaskHandler(private val executor: Executor) : AbstractTaskHandler() {
    suspend override fun handle() {
        val user = retrieveUser()
        val task = fromJson<TaskData>(context.bodyAsString) ?: throw HttpException(400, "Body must not be empty")
        val id = executor.execute(AddTaskCommand(user, task))
        success(id)
    }
}