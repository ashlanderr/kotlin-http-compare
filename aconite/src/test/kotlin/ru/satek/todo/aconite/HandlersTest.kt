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
import ru.satek.todo.domain.TaskData
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
        private lateinit var tasksApi: TasksApi

        private val user = "test@local"
        private val password = "qwerty"

        init {
            runBlocking {
                val response = authApi.signIn(BasicAuth(user, password))
                tasksApi = with(client.create<TasksApi>()) {
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
    fun testAddedTaskReturnsInSelection() = runBlocking {
        val task = TaskData(UUID.randomUUID().toString(), false)
        tasksApi.addTask(task)
        val tasks = tasksApi.selectAll()
        assertTrue(task in tasks.map { TaskData(it.content, it.completed) })
    }

    @Test
    fun testCompletedTaskInCompletedTasksList() = runBlocking {
        val task = TaskData(UUID.randomUUID().toString(), false)
        val id = tasksApi.addTask(task)
        tasksApi.complete(id)

        val tasks = tasksApi.selectCompleted()
        assertTrue(id in tasks.map { it.id })
    }

    @Test
    fun testOpenTaskNotInCompletedTasksList() = runBlocking {
        val task = TaskData(UUID.randomUUID().toString(), false)
        val id = tasksApi.addTask(task)

        val tasks = tasksApi.selectCompleted()
        assertTrue(id !in tasks.map { it.id })
    }

    @Test
    fun testOpenTaskInOpenTasksList() = runBlocking {
        val task = TaskData(UUID.randomUUID().toString(), false)
        val id = tasksApi.addTask(task)

        val tasks = tasksApi.selectOpen()
        assertTrue(id in tasks.map { it.id })
    }

    @Test
    fun testCompletedTaskNotInOpenTasksList() = runBlocking {
        val task = TaskData(UUID.randomUUID().toString(), false)
        val id = tasksApi.addTask(task)
        tasksApi.complete(id)

        val tasks = tasksApi.selectOpen()
        assertTrue(id !in tasks.map { it.id })
    }

    @Test
    fun testTaskSelectableById() = runBlocking {
        val task = TaskData(UUID.randomUUID().toString(), false)
        val id = tasksApi.addTask(task)
        val selected = tasksApi.selectById(id)

        assertEquals(task, selected)
    }

    @Test
    fun testTaskNotFound() = runBlocking {
        var error: HttpException? = null
        try {
            tasksApi.selectById(UUID.randomUUID())
        } catch (ex: HttpException) {
            error = ex
        }

        assertEquals(404, error?.code)
    }
}