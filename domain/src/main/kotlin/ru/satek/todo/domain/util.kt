package ru.satek.todo.domain

import java.util.*

fun assertUser(a: UUID, b: UUID) {
    if (a != b) throw UserNotFound()
}