package ru.satek.todo.vertx.handlers

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.launch
import org.slf4j.LoggerFactory
import ru.satek.todo.util.moshi.UuidAdapter
import ru.satek.todo.vertx.HttpException
import java.util.*
import java.util.concurrent.ConcurrentHashMap

abstract class AbstractHandler {
    private companion object {
        private val logger = LoggerFactory.getLogger(AbstractHandler::class.java)
        private val moshi = Moshi.Builder()
                .add(UuidAdapter)
                .build()
        private val adapters = ConcurrentHashMap<Class<*>, JsonAdapter<*>>()
    }

    protected lateinit var context: RoutingContext
        private set

    protected val request: HttpServerRequest
        get() = context.request()

    protected val response: HttpServerResponse
        get() = context.response()

    fun start(dispatcher: CoroutineDispatcher, context: RoutingContext) {
        this.context = context

        launch(dispatcher) {
            try {
                handle()
            } catch (ex: HttpException) {
                context.response()
                        .setStatusCode(ex.code)
                        .end(ex.message)
            } catch (ex: Exception) {
                logger.error("Error in http handler", ex)
                context.response()
                        .setStatusCode(500)
                        .end("Internal server error")
            }
        }
    }

    protected fun success(body: Any?) {
        response.putHeader("Content-Type", "application/json")
                .end(toJson(body))
    }

    abstract suspend fun handle()

    protected fun retrieveUser(): UUID {
        val user = context.getCookie("Session")?.value ?: throw HttpException(401, "Unauthorized")
        return UUID.fromString(user)
    }

    @Suppress("UNCHECKED_CAST")
    protected fun <T> getJsonAdapter(clazz: Class<T>): JsonAdapter<T> = adapters.computeIfAbsent(clazz) { k ->
        moshi.adapter(k)
    } as JsonAdapter<T>

    protected inline fun <reified T> getJsonAdapter() = getJsonAdapter(T::class.java)

    protected inline fun <reified T> toJson(data: T?): String = getJsonAdapter<T>().toJson(data)

    protected inline fun <reified T> fromJson(data: String): T? = getJsonAdapter<T>().fromJson(data)
}