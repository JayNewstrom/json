package com.jaynewstrom.jsonDelight.retrofit

import com.fasterxml.jackson.core.JsonFactory
import com.jaynewstrom.jsonDelight.runtime.JsonSerializer
import com.jaynewstrom.jsonDelight.runtime.JsonSerializerFactory
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import retrofit2.Converter
import java.io.IOException

internal class RequestBodyConverter<T>(
    private val jsonFactory: JsonFactory,
    private val serializer: JsonSerializer<T>,
    private val serializerFactory: JsonSerializerFactory
) : Converter<T, RequestBody> {
    override fun convert(value: T): RequestBody {
        return JsonRequestBody(value, jsonFactory, serializer, serializerFactory)
    }
}

private val MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8")!!

private class JsonRequestBody<T> constructor(
    private val value: T,
    private val jsonFactory: JsonFactory,
    private val serializer: JsonSerializer<T>,
    private val serializerFactory: JsonSerializerFactory
) : RequestBody() {
    override fun contentType(): MediaType = MEDIA_TYPE

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        val generator = jsonFactory.createGenerator(sink.outputStream())
        serializer.serialize(value, generator, serializerFactory)
        generator.close()
    }
}
