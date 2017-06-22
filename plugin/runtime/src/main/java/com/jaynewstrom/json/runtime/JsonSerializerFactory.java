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
    private final int initialMapSize;

    public JsonSerializerFactory(int initialMapSize) {
        this.initialMapSize = initialMapSize;
        serializerMap = new LinkedHashMap<>(initialMapCapacity(initialMapSize));
        register(BooleanJsonAdapter.INSTANCE);
        register(ByteJsonAdapter.INSTANCE);
        register(DoubleJsonAdapter.INSTANCE);
        register(FloatJsonAdapter.INSTANCE);
        register(IntegerJsonAdapter.INSTANCE);
        register(LongJsonAdapter.INSTANCE);
        register(ShortJsonAdapter.INSTANCE);
        register(StringJsonAdapter.INSTANCE);
    }

    // Size the map so that it won't grow when initializing, with a little room for user registered serializers.
    private static int initialMapCapacity(int initialMapSize) {
        return (int) Math.ceil(initialMapSize / 0.75) + 15;
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

    public final int getInitialMapSize() {
        return initialMapSize;
    }
}
