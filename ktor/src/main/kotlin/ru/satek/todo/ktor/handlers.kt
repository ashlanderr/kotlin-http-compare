package ru.satek.todo.ktor

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.authentication
import io.ktor.auth.basicAuthentication
import io.ktor.auth.principal
import io.ktor.pipeline.PipelineContext
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import ru.satek.todo.domain.*
import java.util.*

suspend fun PipelineContext<Unit, ApplicationCall>.addItem(executor: Executor) {
    try {
        val user = call.retrieveUser()
        val item = call.receive<ItemData>()
        executor.execute(AddItemCommand(user, item))
        call.respond("OK")
    } catch (ex: UserNotFound) {
        throw Forbidden(cause = ex)
    }
}

suspend fun PipelineContext<Unit, ApplicationCall>.selectAllItems(executor: Executor) {
    try {
        val user = call.retrieveUser()
        val items = executor.execute(SelectAllItemsQuery(user))
        call.respond(items)
    } catch (ex: UserNotFound) {
        throw Forbidden(cause = ex)
    }
}

fun Route.signIn(executor: Executor) {
    authentication {
        basicAuthentication("ktor") {
            try {
                val token = executor.execute(SignInCommand(it.name, it.password))
                TokenPrincipal(token)
            } catch (ex: WrongEmailOrPassword) {
                null
            }
        }
    }

    post {
        val token = call.principal<TokenPrincipal>()!!.token
        call.response.cookies.append("Session", token.user.toString())
        call.respond(token)
    }
}

private fun ApplicationCall.retrieveUser(): UUID {
    val user = this.request.cookies["Session"] ?: throw Unauthorized()
    return UUID.fromString(user)
}