package com.jaynewstrom.json.runtime

import com.fasterxml.jackson.core.JsonGenerator

import java.io.IOException

interface JsonSerializer<T> {
    @Throws(IOException::class)
    fun serialize(value: T, jg: JsonGenerator, serializerFactory: JsonSerializerFactory)
}
