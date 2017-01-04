package com.jaynewstrom.json.runtime.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.jaynewstrom.json.runtime.JsonDeserializer;
import com.jaynewstrom.json.runtime.JsonDeserializerFactory;
import com.jaynewstrom.json.runtime.JsonSerializer;
import com.jaynewstrom.json.runtime.JsonSerializerFactory;

import java.io.IOException;

public final class FloatJsonAdapter implements JsonSerializer<Float>, JsonDeserializer<Float> {
    public static final FloatJsonAdapter INSTANCE = new FloatJsonAdapter();

    private FloatJsonAdapter() {
    }

    @Override public void serialize(Float f, JsonGenerator jg, JsonSerializerFactory serializerFactory) throws IOException {
        jg.writeNumber(f);
    }

    @Override public Float deserialize(JsonParser jp, JsonDeserializerFactory deserializerFactory) throws IOException {
        return jp.getFloatValue();
    }

    @Override public Class<?> modelClass() {
        return Float.class;
    }
}
