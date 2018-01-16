package ru.satek.todo.vertx.handlers

import io.vertx.ext.web.Cookie
import ru.satek.todo.domain.Executor
import ru.satek.todo.domain.SignInCommand
import ru.satek.todo.domain.WrongEmailOrPassword
import ru.satek.todo.vertx.BasicAuth
import ru.satek.todo.vertx.HttpException
import java.util.*

class SignInHandler(private val executor: Executor) : AbstractHandler() {
    companion object {
        private val decoder = Base64.getDecoder()
    }

    private fun parseBasicAuth(): BasicAuth {
        val header = request.getHeader("Authorization") ?: throw HttpException(401, "Unauthorized")
        val (prefix, data) = header.split(" ")
        if (prefix != "Basic") throw HttpException(400, "Bad authorization header")
        val userAndPassword = String(decoder.decode(data)).split(':')
        if (userAndPassword.size != 2) throw HttpException(400, "Bad authorization header")
        val (user, password) = userAndPassword
        return BasicAuth(user, password)
    }

    suspend override fun handle() {
        val auth = parseBasicAuth()
        try {
            val token = executor.execute(SignInCommand(auth.user, auth.password))
            context.addCookie(Cookie.cookie("Session", token.user.toString()))
            success(token)
        } catch (ex: WrongEmailOrPassword) {
            throw HttpException(401, "Wrong email or password", ex)
        }
    }
}