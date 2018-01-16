package ru.satek.todo.aconite.impl

import io.aconite.serializers.BuildInStringSerializers
import io.aconite.serializers.MoshiBodySerializer
import io.aconite.serializers.oneOf
import io.aconite.server.AconiteServer
import io.aconite.server.errors.LogErrorHandler
import io.aconite.server.handlers.VertxHandler
import ru.satek.todo.aconite.AuthApi
import ru.satek.todo.aconite.ItemsApi
import ru.satek.todo.domain.Executor
import ru.satek.todo.util.moshi.UuidAdapter

fun main(args: Array<String>) {
    val executor = Executor()

    val server = AconiteServer(
            bodySerializer = MoshiBodySerializer.Factory { add(UuidAdapter) },
            stringSerializer = oneOf(
                    BasicAuthStringSerializer,
                    BuildInStringSerializers
            ),
            errorHandler = LogErrorHandler()
    )
    server.register<AuthApi> { AuthImpl(executor) }
    server.register<ItemsApi> { ItemsImpl(executor) }

    VertxHandler.runServer(server, 8080)
}