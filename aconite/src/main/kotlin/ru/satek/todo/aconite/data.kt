package ru.satek.todo.aconite

import io.aconite.annotations.Body
import io.aconite.annotations.Header
import io.aconite.annotations.ResponseClass
import io.aconite.serializers.Cookie
import ru.satek.todo.domain.AuthToken

@ResponseClass
data class SignInResponse(
        @Body val token: AuthToken,
        @Header("Set-Cookie") val cookie: Cookie
)

data class BasicAuth(val user: String, val password: String)