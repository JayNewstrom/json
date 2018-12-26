package com.jaynewstrom.jsonDelight.runtime.internal

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.jaynewstrom.jsonDelight.runtime.JsonDeserializer
import com.jaynewstrom.jsonDelight.runtime.JsonDeserializerFactory
import com.jaynewstrom.jsonDelight.runtime.JsonRegistrable
import com.jaynewstrom.jsonDelight.runtime.JsonSerializer
import com.jaynewstrom.jsonDelight.runtime.JsonSerializerFactory

import java.io.IOException

object DoubleJsonAdapter : JsonSerializer<Double>, JsonDeserializer<Double>, JsonRegistrable {
    @Throws(IOException::class)
    override fun serialize(value: Double, jg: JsonGenerator, serializerFactory: JsonSerializerFactory) = jg.writeNumber(value)

    @Throws(IOException::class)
    override fun deserialize(jp: JsonParser, deserializerFactory: JsonDeserializerFactory): Double = jp.doubleValue

    override fun modelClass(): Class<*> = Double::class.java
}
