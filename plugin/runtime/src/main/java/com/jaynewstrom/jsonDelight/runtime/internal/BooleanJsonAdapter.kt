package com.jaynewstrom.jsonDelight.runtime.internal

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.jaynewstrom.jsonDelight.runtime.JsonDeserializer
import com.jaynewstrom.jsonDelight.runtime.JsonDeserializerFactory
import com.jaynewstrom.jsonDelight.runtime.JsonRegistrable
import com.jaynewstrom.jsonDelight.runtime.JsonSerializer
import com.jaynewstrom.jsonDelight.runtime.JsonSerializerFactory

import java.io.IOException

object BooleanJsonAdapter : JsonSerializer<Boolean>, JsonDeserializer<Boolean>, JsonRegistrable {
    @Throws(IOException::class)
    override fun serialize(value: Boolean, jg: JsonGenerator, serializerFactory: JsonSerializerFactory) = jg.writeBoolean(value)

    @Throws(IOException::class)
    override fun deserialize(jp: JsonParser, deserializerFactory: JsonDeserializerFactory): Boolean = jp.booleanValue

    override fun modelClass(): Class<*> = Boolean::class.java
}
