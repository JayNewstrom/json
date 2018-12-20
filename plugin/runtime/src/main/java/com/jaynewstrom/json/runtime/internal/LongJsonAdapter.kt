package com.jaynewstrom.json.runtime.internal

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.jaynewstrom.json.runtime.JsonDeserializer
import com.jaynewstrom.json.runtime.JsonDeserializerFactory
import com.jaynewstrom.json.runtime.JsonRegistrable
import com.jaynewstrom.json.runtime.JsonSerializer
import com.jaynewstrom.json.runtime.JsonSerializerFactory

import java.io.IOException

object LongJsonAdapter : JsonSerializer<Long>, JsonDeserializer<Long>, JsonRegistrable {
    @Throws(IOException::class)
    override fun serialize(value: Long, jg: JsonGenerator, serializerFactory: JsonSerializerFactory) = jg.writeNumber(value)

    @Throws(IOException::class)
    override fun deserialize(jp: JsonParser, deserializerFactory: JsonDeserializerFactory): Long = jp.longValue

    override fun modelClass(): Class<*> = Long::class.java
}
