package ru.satek.todo.aconite

import io.aconite.HttpException
import io.aconite.client.AconiteClient
import io.aconite.client.clients.VertxHttpClient
import io.aconite.serializers.BuildInStringSerializers
import io.aconite.serializers.MoshiBodySerializer
import io.aconite.serializers.oneOf
import kotlinx.coroutines.experimental.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.satek.todo.aconite.impl.BasicAuthStringSerializer
import ru.satek.todo.domain.ItemData
import ru.satek.todo.util.moshi.UuidAdapter
import java.util.*

class HandlersTest {
    companion object {
        private val client = AconiteClient(
                httpClient = VertxHttpClient(),
                bodySerializer = MoshiBodySerializer.Factory { add(UuidAdapter) },
                stringSerializer = oneOf(
                        BasicAuthStringSerializer,
                        BuildInStringSerializers
                )
        )

        private val address = "http://localhost:8080"
        private val authApi = client.create<AuthApi>()[address]
        private lateinit var itemsApi: ItemsApi

        private val user = "test@local"
        private val password = "qwerty"

        init {
            runBlocking {
                val response = authApi.signIn(BasicAuth(user, password))
                itemsApi = with(client.create<ItemsApi>()) {
                    setCookie(response.cookie)
                    get(address)
                }
            }
        }
    }

    @Test
    fun testAuthSuccess() = runBlocking<Unit> {
        authApi.signIn(BasicAuth(user, password))
    }

    @Test
    fun testAuthFailed() = runBlocking {
        var error: HttpException? = null
        try {
            authApi.signIn(BasicAuth("unknownUser", "123"))
        } catch (ex: HttpException) {
            error = ex
        }

        assertEquals(401, error?.code)
    }

    @Test
    fun testAddedItemReturnsInSelection() = runBlocking {
        val item = ItemData(UUID.randomUUID().toString())
        itemsApi.addItem(item)
        val items = itemsApi.selectAllItems()
        println(items)
        assertTrue(item in items)
    }
}