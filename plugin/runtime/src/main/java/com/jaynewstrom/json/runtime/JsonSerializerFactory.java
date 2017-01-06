package com.jaynewstrom.json.runtime;

import com.jaynewstrom.json.runtime.internal.BooleanJsonAdapter;
import com.jaynewstrom.json.runtime.internal.ByteJsonAdapter;
import com.jaynewstrom.json.runtime.internal.DoubleJsonAdapter;
import com.jaynewstrom.json.runtime.internal.FloatJsonAdapter;
import com.jaynewstrom.json.runtime.internal.IntegerJsonAdapter;
import com.jaynewstrom.json.runtime.internal.LongJsonAdapter;
import com.jaynewstrom.json.runtime.internal.ShortJsonAdapter;
import com.jaynewstrom.json.runtime.internal.StringJsonAdapter;

import java.util.LinkedHashMap;
import java.util.Map;

public class JsonSerializerFactory {
    final Map<Class<?>, JsonSerializer<?>> serializerMap;

    public JsonSerializerFactory() {
        this(0);
    }

    public JsonSerializerFactory(int initialMapSize) {
        serializerMap = new LinkedHashMap<>(initialMapSize + 20);
        register(BooleanJsonAdapter.INSTANCE);
        register(ByteJsonAdapter.INSTANCE);
        register(DoubleJsonAdapter.INSTANCE);
        register(FloatJsonAdapter.INSTANCE);
        register(IntegerJsonAdapter.INSTANCE);
        register(LongJsonAdapter.INSTANCE);
        register(ShortJsonAdapter.INSTANCE);
        register(StringJsonAdapter.INSTANCE);
    }

    public final void register(JsonSerializer<?> jsonSerializer) {
        serializerMap.put(jsonSerializer.modelClass(), jsonSerializer);
    }

    public final <T> JsonSerializer<T> get(Class<T> modelClass) {
        // noinspection unchecked
        return (JsonSerializer<T>) serializerMap.get(modelClass);
    }
}
