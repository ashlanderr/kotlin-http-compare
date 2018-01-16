package ru.satek.todo.ktor

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.jackson.JacksonConverter
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.slf4j.LoggerFactory
import ru.satek.todo.domain.Executor

fun main(args: Array<String>) {
    val executor = Executor()

    (LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger).level = Level.TRACE

    val server = embeddedServer(Netty, 8080) {
        install(ErrorHandler)

        install(CallLogging)

        install(ContentNegotiation) {
            register(ContentType.Application.Json, JacksonConverter())
        }

        routing {
            route("/auth/sign-in") {
                signIn(executor)
            }
            route("/items") {
                get { selectAllItems(executor) }
                post { addItem(executor) }
            }
        }
    }
    server.start(true)
}

