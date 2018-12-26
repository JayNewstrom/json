package com.jaynewstrom.jsonDelight.runtime

import com.fasterxml.jackson.core.JsonParser

import java.io.IOException

interface JsonDeserializer<T> {
    @Throws(IOException::class)
    fun deserialize(jp: JsonParser, deserializerFactory: JsonDeserializerFactory): T
}
