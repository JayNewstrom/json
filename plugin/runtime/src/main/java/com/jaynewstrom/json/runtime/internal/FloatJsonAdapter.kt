package com.jaynewstrom.json.runtime.internal

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.jaynewstrom.json.runtime.JsonDeserializer
import com.jaynewstrom.json.runtime.JsonDeserializerFactory
import com.jaynewstrom.json.runtime.JsonRegistrable
import com.jaynewstrom.json.runtime.JsonSerializer
import com.jaynewstrom.json.runtime.JsonSerializerFactory

import java.io.IOException

object FloatJsonAdapter : JsonSerializer<Float>, JsonDeserializer<Float>, JsonRegistrable {
    @Throws(IOException::class)
    override fun serialize(value: Float, jg: JsonGenerator, serializerFactory: JsonSerializerFactory) = jg.writeNumber(value)

    @Throws(IOException::class)
    override fun deserialize(jp: JsonParser, deserializerFactory: JsonDeserializerFactory): Float = jp.floatValue

    override fun modelClass(): Class<*> = Float::class.java
}
