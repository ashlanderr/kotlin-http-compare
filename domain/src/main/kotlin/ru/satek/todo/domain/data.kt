package ru.satek.todo.domain

import java.util.*

data class TaskData(
        val content: String,
        val completed: Boolean
)

data class TaskAndId(
        val id: UUID,
        val content: String,
        val completed: Boolean
)

data class AuthToken(val user: UUID)