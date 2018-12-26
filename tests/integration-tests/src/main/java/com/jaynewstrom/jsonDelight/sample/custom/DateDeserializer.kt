package com.jaynewstrom.jsonDelight.sample.custom

import com.fasterxml.jackson.core.JsonParser
import com.jaynewstrom.jsonDelight.runtime.JsonDeserializer
import com.jaynewstrom.jsonDelight.runtime.JsonDeserializerFactory

import java.io.IOException
import java.util.Date

internal object DateDeserializer : JsonDeserializer<Date> {
    @Throws(IOException::class)
    override fun deserialize(jp: JsonParser, deserializerFactory: JsonDeserializerFactory): Date = Date(jp.longValue)
}
