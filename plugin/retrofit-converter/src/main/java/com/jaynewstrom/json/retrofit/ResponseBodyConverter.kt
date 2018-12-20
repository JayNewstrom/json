package com.jaynewstrom.json.retrofit

import com.fasterxml.jackson.core.JsonFactory
import com.jaynewstrom.json.runtime.JsonDeserializer
import com.jaynewstrom.json.runtime.JsonDeserializerFactory
import okhttp3.ResponseBody
import retrofit2.Converter
import java.io.IOException

internal class ResponseBodyConverter<T>(
    private val jsonFactory: JsonFactory,
    private val deserializer: JsonDeserializer<T>,
    private val deserializerFactory: JsonDeserializerFactory
) : Converter<ResponseBody, T> {
    @Throws(IOException::class)
    override fun convert(responseBody: ResponseBody): T? {
        responseBody.use { value ->
            val jsonParser = jsonFactory.createParser(value.charStream())
            jsonParser.nextToken()
            val returnValue = deserializer.deserialize(jsonParser, deserializerFactory)
            jsonParser.close()
            return returnValue
        }
    }
}
