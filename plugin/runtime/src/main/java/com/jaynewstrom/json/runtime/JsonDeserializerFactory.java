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

public class JsonDeserializerFactory {
    private final Map<Class<?>, JsonDeserializer<?>> deserializerMap;
    private final int initialMapSize;

    public JsonDeserializerFactory(int initialMapSize) {
        this.initialMapSize = initialMapSize;
        deserializerMap = new LinkedHashMap<>(initialMapCapacity(initialMapSize));
        register(BooleanJsonAdapter.INSTANCE);
        register(ByteJsonAdapter.INSTANCE);
        register(DoubleJsonAdapter.INSTANCE);
        register(FloatJsonAdapter.INSTANCE);
        register(IntegerJsonAdapter.INSTANCE);
        register(LongJsonAdapter.INSTANCE);
        register(ShortJsonAdapter.INSTANCE);
        register(StringJsonAdapter.INSTANCE);
    }

    // Size the map so that it won't grow when initializing, with a little room for user registered deserializers.
    private static int initialMapCapacity(int initialMapSize) {
        return (int) Math.ceil(initialMapSize / 0.75) + 15;
    }

    public final <T> JsonDeserializer<T> get(Class<T> modelClass) {
        // noinspection unchecked
        return (JsonDeserializer<T>) deserializerMap.get(modelClass);
    }

    public final <T extends JsonDeserializer<?> & JsonRegistrable> void register(T jsonDeserializer) {
        deserializerMap.put(jsonDeserializer.modelClass(), jsonDeserializer);
    }

    public final void registerAll(JsonDeserializerFactory deserializerFactory) {
        deserializerMap.putAll(deserializerFactory.deserializerMap);
    }

    public final int getInitialMapSize() {
        return initialMapSize;
    }
}
