package com.jaynewstrom.json.runtime.internal

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.jaynewstrom.json.runtime.JsonDeserializer
import com.jaynewstrom.json.runtime.JsonDeserializerFactory
import com.jaynewstrom.json.runtime.JsonRegistrable
import com.jaynewstrom.json.runtime.JsonSerializer
import com.jaynewstrom.json.runtime.JsonSerializerFactory

import java.io.IOException

object StringJsonAdapter : JsonSerializer<String>, JsonDeserializer<String>, JsonRegistrable {
    @Throws(IOException::class)
    override fun serialize(value: String, jg: JsonGenerator, serializerFactory: JsonSerializerFactory) = jg.writeString(value)

    @Throws(IOException::class)
    override fun deserialize(jp: JsonParser, deserializerFactory: JsonDeserializerFactory): String = jp.text

    override fun modelClass(): Class<*> = String::class.java
}
