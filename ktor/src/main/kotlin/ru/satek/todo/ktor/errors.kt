package ru.satek.todo.ktor

import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.ApplicationFeature
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.util.AttributeKey

open class HttpException(val code: HttpStatusCode, message: String? = null, cause: Throwable? = null) : Exception(message, cause)

class Unauthorized(message: String? = null, cause: Throwable? = null) : HttpException(HttpStatusCode.Unauthorized, message, cause)

class Forbidden(message: String? = null, cause: Throwable? = null) : HttpException(HttpStatusCode.Forbidden, message, cause)

class BadRequest(message: String? = null, cause: Throwable? = null) : HttpException(HttpStatusCode.BadRequest, message, cause)

object ErrorHandler : ApplicationFeature<ApplicationCallPipeline, Unit, ErrorHandler> {
    override val key = AttributeKey<ErrorHandler>("errors")

    override fun install(pipeline: ApplicationCallPipeline, configure: Unit.() -> Unit): ErrorHandler {
        pipeline.intercept(ApplicationCallPipeline.Infrastructure) {
            try {
                proceed()
            } catch (ex: HttpException) {
                call.respond(ex.code, ex.message ?: ex.code.description)
            }
        }
        return this
    }
}