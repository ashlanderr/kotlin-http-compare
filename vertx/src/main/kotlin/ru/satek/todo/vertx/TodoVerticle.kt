package ru.satek.todo.vertx

import io.vertx.core.AbstractVerticle
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CookieHandler
import io.vertx.ext.web.handler.LoggerHandler
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.experimental.CoroutineDispatcher
import ru.satek.todo.domain.Executor
import ru.satek.todo.vertx.handlers.*

class TodoVerticle(private val executor: Executor) : AbstractVerticle() {
    private lateinit var dispatcher: CoroutineDispatcher

    private inner class RouteScope(private val router: Router, private val url: String) {
        fun get(factory: () -> AbstractHandler) {
            router.get(url).handler { context ->
                factory().start(dispatcher, context)
            }
        }

        fun post(factory: () -> AbstractHandler) {
            router.post(url).handler { context ->
                factory().start(dispatcher, context)
            }
        }
    }

    override fun start() {
        dispatcher = vertx.dispatcher()
        val router = Router.router(vertx).apply {
            install(BodyHandler.create())
            install(CookieHandler.create())
            install(LoggerHandler.create())

            route("/auth/sign-in") {
                post { SignInHandler(executor) }
            }
            route("/tasks") {
                get { SelectAllTasksHandler(executor) }
                post { AddTaskHandler(executor) }
            }
            get("/tasks/completed") { SelectCompletedTasksHandler(executor) }
            get("/tasks/open") { SelectOpenTasksHandler(executor) }
            get("/tasks/:id") { SelectTaskByIdHandler(executor) }
            post("/tasks/:id/complete") { CompleteTaskHandler(executor) }
        }

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(8080)
    }

    private fun Router.install(handler: Handler<RoutingContext>) {
        this.route().handler(handler)
    }

    private fun Router.route(url: String, builder: RouteScope.() -> Unit) {
        RouteScope(this, url).builder()
    }

    private fun Router.get(url: String, handler: () -> AbstractHandler) {
        route(url) { get(handler) }
    }

    private fun Router.post(url: String, handler: () -> AbstractHandler) {
        route(url) { post(handler) }
    }
}

fun main(args: Array<String>) {
    System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory")

    val vertx = Vertx.vertx()
    val executor = Executor()
    vertx.deployVerticle(TodoVerticle(executor))
}