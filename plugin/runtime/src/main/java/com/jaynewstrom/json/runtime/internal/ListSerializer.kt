package com.jaynewstrom.json.runtime.internal

import com.fasterxml.jackson.core.JsonGenerator
import com.jaynewstrom.json.runtime.JsonSerializer
import com.jaynewstrom.json.runtime.JsonSerializerFactory

import java.io.IOException

class ListSerializer<T>(private val elementSerializer: JsonSerializer<T>) : JsonSerializer<List<T>> {
    @Throws(IOException::class)
    override fun serialize(value: List<T>, jg: JsonGenerator, serializerFactory: JsonSerializerFactory) {
        jg.writeStartArray()
        var i = 0
        val size = value.size
        while (i < size) {
            elementSerializer.serialize(value[i], jg, serializerFactory)
            i++
        }
        jg.writeEndArray()
    }
}
