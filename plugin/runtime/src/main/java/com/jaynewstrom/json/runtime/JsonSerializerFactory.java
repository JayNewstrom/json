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
    private final Map<Class<?>, JsonSerializer<?>> serializerMap;

    public JsonSerializerFactory() {
        serializerMap = new LinkedHashMap<>();
        register(BooleanJsonAdapter.INSTANCE);
        register(ByteJsonAdapter.INSTANCE);
        register(DoubleJsonAdapter.INSTANCE);
        register(FloatJsonAdapter.INSTANCE);
        register(IntegerJsonAdapter.INSTANCE);
        register(LongJsonAdapter.INSTANCE);
        register(ShortJsonAdapter.INSTANCE);
        register(StringJsonAdapter.INSTANCE);
    }

    public final <T> JsonSerializer<T> get(Class<T> modelClass) {
        // noinspection unchecked
        return (JsonSerializer<T>) serializerMap.get(modelClass);
    }

    public final <T extends JsonSerializer<?> & JsonRegistrable> void register(T jsonSerializer) {
        serializerMap.put(jsonSerializer.modelClass(), jsonSerializer);
    }

    public final void registerAll(JsonSerializerFactory serializerFactory) {
        serializerMap.putAll(serializerFactory.serializerMap);
    }
}
