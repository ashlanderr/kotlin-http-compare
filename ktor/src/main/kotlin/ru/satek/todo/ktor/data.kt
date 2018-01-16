package ru.satek.todo.ktor

import io.ktor.auth.Principal
import ru.satek.todo.domain.AuthToken

data class TokenPrincipal(val token: AuthToken) : Principal