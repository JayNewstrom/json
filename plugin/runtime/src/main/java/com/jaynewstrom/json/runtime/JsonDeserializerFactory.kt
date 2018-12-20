package com.jaynewstrom.json.runtime

import com.jaynewstrom.json.runtime.internal.BooleanJsonAdapter
import com.jaynewstrom.json.runtime.internal.ByteJsonAdapter
import com.jaynewstrom.json.runtime.internal.DoubleJsonAdapter
import com.jaynewstrom.json.runtime.internal.FloatJsonAdapter
import com.jaynewstrom.json.runtime.internal.IntegerJsonAdapter
import com.jaynewstrom.json.runtime.internal.LongJsonAdapter
import com.jaynewstrom.json.runtime.internal.ShortJsonAdapter
import com.jaynewstrom.json.runtime.internal.StringJsonAdapter

import java.util.LinkedHashMap

open class JsonDeserializerFactory(val initialMapSize: Int) {
    private val deserializerMap: MutableMap<Class<*>, JsonDeserializer<*>>

    init {
        deserializerMap = LinkedHashMap(initialMapCapacity(initialMapSize))
        register(BooleanJsonAdapter)
        register(ByteJsonAdapter)
        register(DoubleJsonAdapter)
        register(FloatJsonAdapter)
        register(IntegerJsonAdapter)
        register(LongJsonAdapter)
        register(ShortJsonAdapter)
        register(StringJsonAdapter)
    }

    // Size the map so that it won't grow when initializing, with a little room for user registered deserializers.
    private fun initialMapCapacity(initialMapSize: Int): Int {
        return Math.ceil(initialMapSize / 0.75).toInt() + 15
    }

    operator fun <T> get(modelClass: Class<T>): JsonDeserializer<T>? {
        @Suppress("UNCHECKED_CAST") // We protect this by how we register.
        return deserializerMap[modelClass] as JsonDeserializer<T>?
    }

    fun <T> register(jsonDeserializer: T) where T : JsonDeserializer<*>, T : JsonRegistrable {
        deserializerMap[jsonDeserializer.modelClass()] = jsonDeserializer
    }

    fun registerAll(deserializerFactory: JsonDeserializerFactory) {
        deserializerMap.putAll(deserializerFactory.deserializerMap)
    }
}
