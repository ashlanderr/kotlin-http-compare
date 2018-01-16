package ru.satek.todo.aconite.impl

import io.aconite.HttpException
import io.aconite.serializers.Cookie
import ru.satek.todo.aconite.AuthApi
import ru.satek.todo.aconite.BasicAuth
import ru.satek.todo.aconite.SignInResponse
import ru.satek.todo.domain.Executor
import ru.satek.todo.domain.SignInCommand
import ru.satek.todo.domain.WrongEmailOrPassword

class AuthImpl(private val executor: Executor) : AuthApi {
    suspend override fun signIn(auth: BasicAuth): SignInResponse {
        try {
            val token = executor.execute(SignInCommand(auth.user, auth.password))
            return SignInResponse(
                    token = token,
                    cookie = Cookie(mapOf(
                            "Session" to token.user.toString()
                    ))
            )
        } catch (ex: WrongEmailOrPassword) {
            throw HttpException(401, "Wrong email or password")
        }
    }
}