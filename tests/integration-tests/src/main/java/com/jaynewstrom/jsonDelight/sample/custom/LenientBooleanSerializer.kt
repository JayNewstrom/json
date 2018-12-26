package com.jaynewstrom.jsonDelight.sample.custom

import com.fasterxml.jackson.core.JsonGenerator
import com.jaynewstrom.jsonDelight.runtime.JsonSerializer
import com.jaynewstrom.jsonDelight.runtime.JsonSerializerFactory

import java.io.IOException

internal object LenientBooleanSerializer : JsonSerializer<Boolean> {
    @Throws(IOException::class)
    override fun serialize(value: Boolean, jg: JsonGenerator, serializerFactory: JsonSerializerFactory) {
        jg.writeString(value.toString())
    }
}
