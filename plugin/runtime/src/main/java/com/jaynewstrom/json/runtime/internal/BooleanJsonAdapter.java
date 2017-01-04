package com.jaynewstrom.json.runtime.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.jaynewstrom.json.runtime.JsonDeserializer;
import com.jaynewstrom.json.runtime.JsonDeserializerFactory;
import com.jaynewstrom.json.runtime.JsonSerializer;
import com.jaynewstrom.json.runtime.JsonSerializerFactory;

import java.io.IOException;

public final class BooleanJsonAdapter implements JsonSerializer<Boolean>, JsonDeserializer<Boolean> {
    public static final BooleanJsonAdapter INSTANCE = new BooleanJsonAdapter();

    private BooleanJsonAdapter() {
    }

    @Override public void serialize(Boolean b, JsonGenerator jg, JsonSerializerFactory serializerFactory) throws IOException {
        jg.writeBoolean(b);
    }

    @Override public Boolean deserialize(JsonParser jp, JsonDeserializerFactory deserializerFactory) throws IOException {
        return jp.getBooleanValue();
    }

    @Override public Class<?> modelClass() {
        return Boolean.class;
    }
}
