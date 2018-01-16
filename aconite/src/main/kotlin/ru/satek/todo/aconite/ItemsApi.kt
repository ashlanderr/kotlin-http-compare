package ru.satek.todo.aconite

import io.aconite.annotations.Body
import io.aconite.annotations.GET
import io.aconite.annotations.POST
import ru.satek.todo.domain.ItemData

interface ItemsApi {
    @GET("/items")
    suspend fun selectAllItems(): List<ItemData>

    @POST("/items")
    suspend fun addItem(@Body item: ItemData)
}