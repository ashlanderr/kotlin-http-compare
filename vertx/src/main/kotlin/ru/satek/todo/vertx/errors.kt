package ru.satek.todo.vertx

class HttpException(val code: Int, message: String, cause: Throwable? = null) : Exception(message, cause)

