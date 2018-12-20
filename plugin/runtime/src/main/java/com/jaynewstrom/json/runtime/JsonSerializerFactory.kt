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

open class JsonSerializerFactory(val initialMapSize: Int) {
    private val serializerMap: MutableMap<Class<*>, JsonSerializer<*>>

    init {
        serializerMap = LinkedHashMap(initialMapCapacity(initialMapSize))
        register(BooleanJsonAdapter)
        register(ByteJsonAdapter)
        register(DoubleJsonAdapter)
        register(FloatJsonAdapter)
        register(IntegerJsonAdapter)
        register(LongJsonAdapter)
        register(ShortJsonAdapter)
        register(StringJsonAdapter)
    }

    // Size the map so that it won't grow when initializing, with a little room for user registered serializers.
    private fun initialMapCapacity(initialMapSize: Int): Int {
        return Math.ceil(initialMapSize / 0.75).toInt() + 15
    }

    operator fun <T> get(modelClass: Class<T>): JsonSerializer<T>? {
        @Suppress("UNCHECKED_CAST") // We protect this by how we register.
        return serializerMap[modelClass] as JsonSerializer<T>?
    }

    fun <T> register(jsonSerializer: T) where T : JsonSerializer<*>, T : JsonRegistrable {
        serializerMap[jsonSerializer.modelClass()] = jsonSerializer
    }

    fun registerAll(serializerFactory: JsonSerializerFactory) {
        serializerMap.putAll(serializerFactory.serializerMap)
    }
}
