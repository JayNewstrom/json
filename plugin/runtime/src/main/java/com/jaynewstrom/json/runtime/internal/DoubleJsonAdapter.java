package com.jaynewstrom.json.runtime.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.jaynewstrom.json.runtime.JsonDeserializer;
import com.jaynewstrom.json.runtime.JsonDeserializerFactory;
import com.jaynewstrom.json.runtime.JsonSerializer;
import com.jaynewstrom.json.runtime.JsonSerializerFactory;

import java.io.IOException;

public final class DoubleJsonAdapter implements JsonSerializer<Double>, JsonDeserializer<Double> {
    public static final DoubleJsonAdapter INSTANCE = new DoubleJsonAdapter();

    private DoubleJsonAdapter() {
    }

    @Override public void serialize(Double d, JsonGenerator jg, JsonSerializerFactory serializerFactory) throws IOException {
        jg.writeNumber(d);
    }

    @Override public Double deserialize(JsonParser jp, JsonDeserializerFactory deserializerFactory) throws IOException {
        return jp.getDoubleValue();
    }

    @Override public Class<?> modelClass() {
        return Double.class;
    }
}

