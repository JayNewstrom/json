package com.jaynewstrom.jsonDelight.runtime

import com.jaynewstrom.jsonDelight.runtime.internal.BooleanJsonAdapter
import com.jaynewstrom.jsonDelight.runtime.internal.ByteJsonAdapter
import com.jaynewstrom.jsonDelight.runtime.internal.DoubleJsonAdapter
import com.jaynewstrom.jsonDelight.runtime.internal.FloatJsonAdapter
import com.jaynewstrom.jsonDelight.runtime.internal.IntegerJsonAdapter
import com.jaynewstrom.jsonDelight.runtime.internal.LongJsonAdapter
import com.jaynewstrom.jsonDelight.runtime.internal.ShortJsonAdapter
import com.jaynewstrom.jsonDelight.runtime.internal.StringJsonAdapter

import java.util.LinkedHashMap

class JsonSerializerFactory {
    private val serializerMap: MutableMap<Class<*>, JsonSerializer<*>>

    init {
        serializerMap = LinkedHashMap(100)
        register(BooleanJsonAdapter)
        register(ByteJsonAdapter)
        register(DoubleJsonAdapter)
        register(FloatJsonAdapter)
        register(IntegerJsonAdapter)
        register(LongJsonAdapter)
        register(ShortJsonAdapter)
        register(StringJsonAdapter)
    }

    fun hasSerializerFor(modelClass: Class<*>): Boolean {
        if (serializerMap.contains(modelClass)) {
            return true
        }
        val serializer = putSerializerIntoMapForTypeIfExists(modelClass)
        return serializer != null
    }

    operator fun <T> get(modelClass: Class<T>): JsonSerializer<T> {
        var serializer = serializerMap[modelClass]
        if (serializer == null) {
            serializer = putSerializerIntoMapForTypeIfExists(modelClass)
        }
        @Suppress("UNCHECKED_CAST") // We protect this by how we register.
        return serializer!! as JsonSerializer<T>
    }

    fun <T> register(jsonSerializer: T) where T : JsonSerializer<*>, T : JsonRegistrable {
        serializerMap[jsonSerializer.modelClass()] = jsonSerializer
    }

    private fun putSerializerIntoMapForTypeIfExists(modelClass: Class<*>): JsonSerializer<*>? {
        val annotation = modelClass.declaredAnnotations.firstOrNull { it is HavingJsonSerializer }
        val serializer = (annotation as? HavingJsonSerializer)?.value?.java?.newInstance()
        if (serializer != null) {
            serializerMap[modelClass] = serializer
        }
        return serializer
    }
}
