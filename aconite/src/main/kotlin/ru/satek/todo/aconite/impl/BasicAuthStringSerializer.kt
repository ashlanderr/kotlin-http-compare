package ru.satek.todo.aconite.impl

import io.aconite.HttpException
import io.aconite.StringSerializer
import ru.satek.todo.aconite.BasicAuth
import java.util.*
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KType

object BasicAuthStringSerializer : StringSerializer, StringSerializer.Factory {
    private val decoder = Base64.getDecoder()
    private val encoder = Base64.getEncoder()

    override fun deserialize(s: String): Any? {
        val (prefix, data) = s.split(" ")
        if (prefix != "Basic") throw HttpException(400, "Bad authorization header")
        val userAndPassword = String(decoder.decode(data)).split(':')
        if (userAndPassword.size != 2) throw HttpException(400, "Bad authorization header")
        val (user, password) = userAndPassword
        return BasicAuth(user, password)
    }

    override fun serialize(obj: Any?): String? {
        val auth = obj as BasicAuth
        return "Basic " + encoder.encodeToString("${auth.user}:${auth.password}".toByteArray())
    }

    override fun create(annotations: KAnnotatedElement, type: KType): StringSerializer? {
        return if (type.classifier == BasicAuth::class) this else null
    }
}