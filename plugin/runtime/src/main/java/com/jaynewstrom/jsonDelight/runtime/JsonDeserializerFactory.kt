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

class JsonDeserializerFactory {
    private val deserializerMap: MutableMap<Class<*>, JsonDeserializer<*>>

    init {
        deserializerMap = LinkedHashMap(100)
        register(BooleanJsonAdapter)
        register(ByteJsonAdapter)
        register(DoubleJsonAdapter)
        register(FloatJsonAdapter)
        register(IntegerJsonAdapter)
        register(LongJsonAdapter)
        register(ShortJsonAdapter)
        register(StringJsonAdapter)
    }

    fun hasDeserializerFor(modelClass: Class<*>): Boolean {
        if (deserializerMap.contains(modelClass)) {
            return true
        }
        val deserializer = putDeserializerIntoMapForTypeIfExists(modelClass)
        return deserializer != null
    }

    operator fun <T> get(modelClass: Class<T>): JsonDeserializer<T> {
        var deserializer = deserializerMap[modelClass]
        if (deserializer == null) {
            deserializer = putDeserializerIntoMapForTypeIfExists(modelClass)
        }
        @Suppress("UNCHECKED_CAST") // We protect this by how we register.
        return deserializer!! as JsonDeserializer<T>
    }

    fun <T> register(jsonDeserializer: T) where T : JsonDeserializer<*>, T : JsonRegistrable {
        deserializerMap[jsonDeserializer.modelClass()] = jsonDeserializer
    }

    private fun putDeserializerIntoMapForTypeIfExists(modelClass: Class<*>): JsonDeserializer<*>? {
        val annotation = modelClass.declaredAnnotations.firstOrNull { it is HavingJsonDeserializer }
        val deserializer = (annotation as? HavingJsonDeserializer)?.value?.java?.newInstance()
        if (deserializer != null) {
            deserializerMap[modelClass] = deserializer
        }
        return deserializer
    }
}
