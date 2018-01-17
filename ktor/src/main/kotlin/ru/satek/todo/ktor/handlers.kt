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

suspend fun PipelineContext<Unit, ApplicationCall>.addTask(executor: Executor) {
    val user = call.retrieveUser()
    val task = call.receive<TaskData>()
    val id = executor.execute(AddTaskCommand(user, task))
    call.respond(id)
}

suspend fun PipelineContext<Unit, ApplicationCall>.selectAllTasks(executor: Executor) {
    val user = call.retrieveUser()
    val tasks = executor.execute(SelectAllTasksQuery(user))
    call.respond(tasks)
}

suspend fun PipelineContext<Unit, ApplicationCall>.selectCompletedTasks(executor: Executor) {
    val user = call.retrieveUser()
    val tasks = executor.execute(SelectCompletedTasksQuery(user))
    call.respond(tasks)
}

suspend fun PipelineContext<Unit, ApplicationCall>.selectOpenTasks(executor: Executor) {
    val user = call.retrieveUser()
    val tasks = executor.execute(SelectOpenTasksQuery(user))
    call.respond(tasks)
}

suspend fun PipelineContext<Unit, ApplicationCall>.selectTaskById(executor: Executor) {
    val user = call.retrieveUser()
    val taskId = UUID.fromString(call.parameters["id"])
    val task = executor.execute(SelectTaskByIdQuery(user, taskId))
    call.respond(task)
}

suspend fun PipelineContext<Unit, ApplicationCall>.completeTask(executor: Executor) {
    val user = call.retrieveUser()
    val taskId = UUID.fromString(call.parameters["id"])
    executor.execute(CompleteTaskCommand(user, taskId))
    call.respond("OK")
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