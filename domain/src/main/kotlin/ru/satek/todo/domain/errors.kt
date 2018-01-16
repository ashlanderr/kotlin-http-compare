package ru.satek.todo.domain

open class DomainException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)

class WrongEmailOrPassword : DomainException()

class UserNotFound : DomainException()