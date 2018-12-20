package com.jaynewstrom.json.retrofit

import com.fasterxml.jackson.core.JsonFactory
import com.jaynewstrom.json.runtime.JsonDeserializer
import com.jaynewstrom.json.runtime.JsonDeserializerFactory
import com.jaynewstrom.json.runtime.JsonSerializer
import com.jaynewstrom.json.runtime.JsonSerializerFactory
import com.jaynewstrom.json.runtime.internal.ListDeserializer
import com.jaynewstrom.json.runtime.internal.ListSerializer
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class JsonConverterFactory private constructor(
    private val jsonFactory: JsonFactory,
    private val serializerFactory: JsonSerializerFactory,
    private val deserializerFactory: JsonDeserializerFactory
) : Converter.Factory() {
    companion object {
        @JvmStatic fun create(
            jsonFactory: JsonFactory,
            serializerFactory: JsonSerializerFactory,
            deserializerFactory: JsonDeserializerFactory
        ): Converter.Factory {
            return JsonConverterFactory(jsonFactory, serializerFactory, deserializerFactory)
        }
    }

    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>, retrofit: Retrofit): Converter<ResponseBody, *>? {
        val deserializer = deserializerForType(type)
        return if (deserializer != null) {
            ResponseBodyConverter(jsonFactory, deserializer, deserializerFactory)
        } else {
            super.responseBodyConverter(type, annotations, retrofit)
        }
    }

    private fun deserializerForType(type: Type): JsonDeserializer<*>? {
        if (type is ParameterizedType) {
            if (type.rawType === List::class.java) {
                val typeArguments = type.actualTypeArguments
                val firstType = typeArguments[0]
                val deserializer = deserializerForType(firstType)
                if (deserializer != null) {
                    return ListDeserializer(deserializer)
                }
            }
        }
        if (type is Class<*>) {
            val deserializer = deserializerFactory.get(type)
            if (deserializer != null) {
                return deserializer
            }
        }
        return null
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody>? {
        val jsonSerializer = serializerForType(type)
        return if (jsonSerializer != null) {
            RequestBodyConverter(jsonFactory, jsonSerializer, serializerFactory)
        } else {
            super.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit)
        }
    }

    private fun serializerForType(type: Type): JsonSerializer<*>? {
        if (type is ParameterizedType) {
            if (type.rawType === List::class.java) {
                val typeArguments = type.actualTypeArguments
                val firstType = typeArguments[0]
                val serializer = serializerForType(firstType)
                if (serializer != null) {
                    return ListSerializer(serializer)
                }
            }
        }
        if (type is Class<*>) {
            val serializer = serializerFactory.get(type)
            if (serializer != null) {
                return serializer
            }
        }
        return null
    }
}
