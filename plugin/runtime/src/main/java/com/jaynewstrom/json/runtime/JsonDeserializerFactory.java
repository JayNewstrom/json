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

    public JsonDeserializerFactory() {
        deserializerMap = new LinkedHashMap<>();
        register(BooleanJsonAdapter.INSTANCE);
        register(ByteJsonAdapter.INSTANCE);
        register(DoubleJsonAdapter.INSTANCE);
        register(FloatJsonAdapter.INSTANCE);
        register(IntegerJsonAdapter.INSTANCE);
        register(LongJsonAdapter.INSTANCE);
        register(ShortJsonAdapter.INSTANCE);
        register(StringJsonAdapter.INSTANCE);
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
}
