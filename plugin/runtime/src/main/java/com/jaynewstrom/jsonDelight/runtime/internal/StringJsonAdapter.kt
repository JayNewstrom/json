package com.jaynewstrom.jsonDelight.runtime.internal

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.jaynewstrom.jsonDelight.runtime.JsonDeserializer
import com.jaynewstrom.jsonDelight.runtime.JsonDeserializerFactory
import com.jaynewstrom.jsonDelight.runtime.JsonRegistrable
import com.jaynewstrom.jsonDelight.runtime.JsonSerializer
import com.jaynewstrom.jsonDelight.runtime.JsonSerializerFactory

import java.io.IOException

object StringJsonAdapter : JsonSerializer<String>, JsonDeserializer<String>, JsonRegistrable {
    @Throws(IOException::class)
    override fun serialize(value: String, jg: JsonGenerator, serializerFactory: JsonSerializerFactory) = jg.writeString(value)

    @Throws(IOException::class)
    override fun deserialize(jp: JsonParser, deserializerFactory: JsonDeserializerFactory): String = jp.text

    override fun modelClass(): Class<*> = String::class.java
}
