package ru.satek.todo.vertx.handlers

import ru.satek.todo.domain.TaskNotFound
import ru.satek.todo.domain.UserNotFound
import ru.satek.todo.vertx.HttpException

abstract class AbstractTaskHandler : AbstractHandler() {
    suspend override fun interceptor(inner: suspend () -> Unit) = super.interceptor {
        try {
            inner()
        } catch (ex: UserNotFound) {
            throw HttpException(403, "Forbidden")
        } catch (ex: TaskNotFound) {
            throw HttpException(404, "Task not found")
        }
    }
}