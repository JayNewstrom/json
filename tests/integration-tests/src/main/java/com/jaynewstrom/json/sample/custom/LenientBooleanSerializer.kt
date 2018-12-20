package com.jaynewstrom.json.sample.custom

import com.fasterxml.jackson.core.JsonGenerator
import com.jaynewstrom.json.runtime.JsonSerializer
import com.jaynewstrom.json.runtime.JsonSerializerFactory

import java.io.IOException

internal object LenientBooleanSerializer : JsonSerializer<Boolean> {
    @Throws(IOException::class)
    override fun serialize(value: Boolean, jg: JsonGenerator, serializerFactory: JsonSerializerFactory) {
        jg.writeString(value.toString())
    }
}
