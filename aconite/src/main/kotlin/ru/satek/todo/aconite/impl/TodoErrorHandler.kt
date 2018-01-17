package ru.satek.todo.aconite.impl

import io.aconite.HttpException
import io.aconite.Response
import io.aconite.server.ErrorHandler
import io.aconite.server.errors.LogErrorHandler
import io.aconite.toResponse
import ru.satek.todo.domain.TaskNotFound
import ru.satek.todo.domain.UserNotFound

object TodoErrorHandler : ErrorHandler {
    private val logHandler = LogErrorHandler()

    override fun handle(ex: Throwable): Response {
        return when (ex) {
            is UserNotFound -> HttpException(403, "Forbidden").toResponse()
            is TaskNotFound -> HttpException(404, "Task not found").toResponse()
            else -> logHandler.handle(ex)
        }
    }
}