package com.jaynewstrom.json.sample

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonParser
import java.io.IOException
import java.io.StringWriter

class JsonTestHelper {
    private val jsonFactory = JsonFactory()
    private val deserializerFactory = RealJsonDeserializerFactory()
    private val serializerFactory = RealJsonSerializerFactory()

    fun <T> deserializeFile(modelClass: Class<T>, fileName: String, testObject: Any): T {
        try {
            val inputStream = testObject.javaClass.getResource(fileName)!!.openStream()
            return deserialize(modelClass, jsonFactory.createParser(inputStream))
        } catch (e: IOException) {
            throw AssertionError(e)
        }
    }

    fun <T> deserializeString(modelClass: Class<T>, json: String): T {
        try {
            return deserialize(modelClass, jsonFactory.createParser(json))
        } catch (e: IOException) {
            throw AssertionError(e)
        }
    }

    private fun <T> deserialize(modelClass: Class<T>, jsonParser: JsonParser): T {
        try {
            jsonParser.nextToken()
            val deserializer = deserializerFactory[modelClass]
            return deserializer!!.deserialize(jsonParser, deserializerFactory)
        } catch (e: IOException) {
            throw AssertionError(e)
        }
    }

    fun <T> serialize(model: T, modelClass: Class<T>): String {
        try {
            val sw = StringWriter()
            val generator = jsonFactory.createGenerator(sw)
            val serializer = serializerFactory[modelClass]
            serializer!!.serialize(model, generator, serializerFactory)
            generator.close()
            return sw.toString()
        } catch (e: IOException) {
            throw AssertionError(e)
        }
    }
}
