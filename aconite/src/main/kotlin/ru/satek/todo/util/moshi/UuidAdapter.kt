package ru.satek.todo.util.moshi

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.util.*

object UuidAdapter {
    @ToJson
    fun toJson(uuid: UUID): String = uuid.toString()

    @FromJson
    fun fromJson(string: String): UUID = UUID.fromString(string)
}