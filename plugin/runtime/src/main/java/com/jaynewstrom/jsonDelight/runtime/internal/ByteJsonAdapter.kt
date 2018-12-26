package com.jaynewstrom.jsonDelight.runtime.internal

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.jaynewstrom.jsonDelight.runtime.JsonDeserializer
import com.jaynewstrom.jsonDelight.runtime.JsonDeserializerFactory
import com.jaynewstrom.jsonDelight.runtime.JsonRegistrable
import com.jaynewstrom.jsonDelight.runtime.JsonSerializer
import com.jaynewstrom.jsonDelight.runtime.JsonSerializerFactory

import java.io.IOException

object ByteJsonAdapter : JsonSerializer<Byte>, JsonDeserializer<Byte>, JsonRegistrable {
    @Throws(IOException::class)
    override fun serialize(value: Byte, jg: JsonGenerator, serializerFactory: JsonSerializerFactory) = jg.writeNumber(value.toShort())

    @Throws(IOException::class)
    override fun deserialize(jp: JsonParser, deserializerFactory: JsonDeserializerFactory): Byte = jp.byteValue

    override fun modelClass(): Class<*> = Byte::class.java
}
