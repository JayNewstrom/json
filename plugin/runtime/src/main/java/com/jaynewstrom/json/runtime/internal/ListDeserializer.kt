package com.jaynewstrom.json.runtime.internal

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.jaynewstrom.json.runtime.JsonDeserializer
import com.jaynewstrom.json.runtime.JsonDeserializerFactory

import java.io.IOException

class ListDeserializer<T>(private val elementFactory: JsonDeserializer<T>) : JsonDeserializer<List<T>> {
    @Throws(IOException::class)
    override fun deserialize(jp: JsonParser, deserializerFactory: JsonDeserializerFactory): List<T> {
        if (jp.currentToken() != JsonToken.START_ARRAY) {
            throw IOException("Expected data to start with an Array")
        }

        val list = ArrayList<T>()

        while (jp.nextToken() != JsonToken.END_ARRAY) {
            list.add(elementFactory.deserialize(jp, deserializerFactory))
        }

        return list.toList()
    }
}
