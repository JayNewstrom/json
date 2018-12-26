package com.jaynewstrom.jsonDelight.sample.custom

import com.fasterxml.jackson.core.JsonGenerator
import com.jaynewstrom.jsonDelight.runtime.JsonSerializer
import com.jaynewstrom.jsonDelight.runtime.JsonSerializerFactory

import java.io.IOException
import java.util.Date

internal object DateSerializer : JsonSerializer<Date> {
    @Throws(IOException::class)
    override fun serialize(value: Date, jg: JsonGenerator, serializerFactory: JsonSerializerFactory) = jg.writeNumber(value.time)
}
