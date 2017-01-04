package com.jaynewstrom.json.runtime.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.jaynewstrom.json.runtime.JsonDeserializer;
import com.jaynewstrom.json.runtime.JsonDeserializerFactory;
import com.jaynewstrom.json.runtime.JsonSerializer;
import com.jaynewstrom.json.runtime.JsonSerializerFactory;

import java.io.IOException;

public final class IntegerJsonAdapter implements JsonSerializer<Integer>, JsonDeserializer<Integer> {
    public static final IntegerJsonAdapter INSTANCE = new IntegerJsonAdapter();

    private IntegerJsonAdapter() {
    }

    @Override public void serialize(Integer i, JsonGenerator jg, JsonSerializerFactory serializerFactory) throws IOException {
        jg.writeNumber(i);
    }

    @Override public Integer deserialize(JsonParser jp, JsonDeserializerFactory deserializerFactory) throws IOException {
        return jp.getIntValue();
    }

    @Override public Class<?> modelClass() {
        return Integer.class;
    }
}
