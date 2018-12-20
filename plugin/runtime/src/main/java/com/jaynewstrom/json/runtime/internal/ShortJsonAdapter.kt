package com.jaynewstrom.json.runtime.internal

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.jaynewstrom.json.runtime.JsonDeserializer
import com.jaynewstrom.json.runtime.JsonDeserializerFactory
import com.jaynewstrom.json.runtime.JsonRegistrable
import com.jaynewstrom.json.runtime.JsonSerializer
import com.jaynewstrom.json.runtime.JsonSerializerFactory

import java.io.IOException

object ShortJsonAdapter : JsonSerializer<Short>, JsonDeserializer<Short>, JsonRegistrable {
    @Throws(IOException::class)
    override fun serialize(value: Short, jg: JsonGenerator, serializerFactory: JsonSerializerFactory) = jg.writeNumber(value)

    @Throws(IOException::class)
    override fun deserialize(jp: JsonParser, deserializerFactory: JsonDeserializerFactory): Short = jp.shortValue

    override fun modelClass(): Class<*> = Short::class.java
}
