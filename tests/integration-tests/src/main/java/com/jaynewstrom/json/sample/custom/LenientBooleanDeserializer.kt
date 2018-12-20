package com.jaynewstrom.json.sample.custom

import com.fasterxml.jackson.core.JsonParser
import com.jaynewstrom.json.runtime.JsonDeserializer
import com.jaynewstrom.json.runtime.JsonDeserializerFactory

import java.io.IOException

internal object LenientBooleanDeserializer : JsonDeserializer<Boolean> {
    @Throws(IOException::class)
    override fun deserialize(jp: JsonParser, deserializerFactory: JsonDeserializerFactory): Boolean = jp.valueAsBoolean
}
