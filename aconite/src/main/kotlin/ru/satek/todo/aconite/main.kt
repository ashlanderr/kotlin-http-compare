package ru.satek.todo.aconite

import io.aconite.serializers.BuildInStringSerializers
import io.aconite.serializers.MoshiBodySerializer
import io.aconite.serializers.oneOf
import io.aconite.server.AconiteServer
import io.aconite.server.errors.LogErrorHandler
import io.aconite.server.handlers.VertxHandler
import ru.satek.todo.aconite.impl.AuthImpl
import ru.satek.todo.aconite.impl.BasicAuthStringSerializer
import ru.satek.todo.aconite.impl.TasksImpl
import ru.satek.todo.aconite.impl.TodoErrorHandler
import ru.satek.todo.domain.Executor
import ru.satek.todo.util.moshi.UuidAdapter

fun main(args: Array<String>) {
    val executor = Executor()

    val server = AconiteServer(
            bodySerializer = MoshiBodySerializer.Factory {
                add(UuidAdapter)
            },
            stringSerializer = oneOf(
                    BasicAuthStringSerializer,
                    BuildInStringSerializers
            ),
            errorHandler = TodoErrorHandler
    )
    server.register<AuthApi> { AuthImpl(executor) }
    server.register<TasksApi> { TasksImpl(executor) }

    VertxHandler.runServer(server, 8080)
}