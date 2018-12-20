package com.jaynewstrom.json.runtime.internal

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.jaynewstrom.json.runtime.JsonDeserializer
import com.jaynewstrom.json.runtime.JsonDeserializerFactory
import com.jaynewstrom.json.runtime.JsonRegistrable
import com.jaynewstrom.json.runtime.JsonSerializer
import com.jaynewstrom.json.runtime.JsonSerializerFactory

import java.io.IOException

object BooleanJsonAdapter : JsonSerializer<Boolean>, JsonDeserializer<Boolean>, JsonRegistrable {
    @Throws(IOException::class)
    override fun serialize(value: Boolean, jg: JsonGenerator, serializerFactory: JsonSerializerFactory) = jg.writeBoolean(value)

    @Throws(IOException::class)
    override fun deserialize(jp: JsonParser, deserializerFactory: JsonDeserializerFactory): Boolean = jp.booleanValue

    override fun modelClass(): Class<*> = Boolean::class.java
}
