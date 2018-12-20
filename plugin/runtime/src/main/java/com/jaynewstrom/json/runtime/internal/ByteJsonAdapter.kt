package com.jaynewstrom.json.runtime.internal

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.jaynewstrom.json.runtime.JsonDeserializer
import com.jaynewstrom.json.runtime.JsonDeserializerFactory
import com.jaynewstrom.json.runtime.JsonRegistrable
import com.jaynewstrom.json.runtime.JsonSerializer
import com.jaynewstrom.json.runtime.JsonSerializerFactory

import java.io.IOException

object ByteJsonAdapter : JsonSerializer<Byte>, JsonDeserializer<Byte>, JsonRegistrable {
    @Throws(IOException::class)
    override fun serialize(value: Byte, jg: JsonGenerator, serializerFactory: JsonSerializerFactory) = jg.writeNumber(value.toShort())

    @Throws(IOException::class)
    override fun deserialize(jp: JsonParser, deserializerFactory: JsonDeserializerFactory): Byte = jp.byteValue

    override fun modelClass(): Class<*> = Byte::class.java
}
