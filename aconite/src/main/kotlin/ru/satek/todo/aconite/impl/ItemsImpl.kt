package ru.satek.todo.aconite.impl

import io.aconite.HttpException
import io.aconite.annotations.BeforeRequest
import io.aconite.annotations.Header
import io.aconite.serializers.Cookie
import ru.satek.todo.aconite.ItemsApi
import ru.satek.todo.domain.*
import java.util.*

class ItemsImpl(private val executor: Executor) : ItemsApi {
    lateinit var user: UUID

    @BeforeRequest
    @Suppress("unused")
    suspend fun parseAuth(@Header("Cookie") cookie: Cookie? = null) {
        user = cookie?.data?.get("Session")?.let { UUID.fromString(it) } ?: throw HttpException(401, "Unauthorized")
    }

    suspend override fun selectAllItems(): List<ItemData> {
        try {
            return executor.execute(SelectAllItemsQuery(user))
        } catch (ex: UserNotFound) {
            throw HttpException(403, "Forbidden", ex)
        }
    }

    suspend override fun addItem(item: ItemData) {
        try {
            return executor.execute(AddItemCommand(user, item))
        } catch (ex: UserNotFound) {
            throw HttpException(403, "Forbidden", ex)
        }
    }
}