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

class NotFound(message: String? = null, cause: Throwable? = null) : HttpException(HttpStatusCode.NotFound, message, cause)

class ErrorHandlerConfiguration {
    val converters = mutableMapOf<Class<*>, (Throwable) -> HttpException>()

    inline fun <reified T> on(noinline converter: (T) -> HttpException) {
        converters[T::class.java] = { converter(it as T) }
    }

    fun convert(ex: Throwable): HttpException? {
        return converters[ex.javaClass]?.invoke(ex)
    }
}

object ErrorHandler : ApplicationFeature<ApplicationCallPipeline, ErrorHandlerConfiguration, ErrorHandler> {
    override val key = AttributeKey<ErrorHandler>("errors")

    override fun install(pipeline: ApplicationCallPipeline, configure: ErrorHandlerConfiguration.() -> Unit): ErrorHandler {
        val configuration = ErrorHandlerConfiguration().apply(configure)

        pipeline.intercept(ApplicationCallPipeline.Infrastructure) {
            try {
                proceed()
            } catch (ex: HttpException) {
                call.respond(ex.code, ex.message ?: ex.code.description)
            } catch (ex: Exception) {
                val error = configuration.convert(ex) ?: throw ex
                call.respond(error.code, error.message ?: error.code.description)
            }
        }
        return this
    }
}