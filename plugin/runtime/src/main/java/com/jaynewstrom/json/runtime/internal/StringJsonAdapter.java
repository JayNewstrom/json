package com.jaynewstrom.json.runtime.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.jaynewstrom.json.runtime.JsonDeserializer;
import com.jaynewstrom.json.runtime.JsonDeserializerFactory;
import com.jaynewstrom.json.runtime.JsonSerializer;
import com.jaynewstrom.json.runtime.JsonSerializerFactory;

import java.io.IOException;

public final class StringJsonAdapter implements JsonSerializer<String>, JsonDeserializer<String> {
    public static final StringJsonAdapter INSTANCE = new StringJsonAdapter();

    private StringJsonAdapter() {
    }

    @Override public void serialize(String s, JsonGenerator jg, JsonSerializerFactory serializerFactory) throws IOException {
        jg.writeString(s);
    }

    @Override public String deserialize(JsonParser jp, JsonDeserializerFactory deserializerFactory) throws IOException {
        return jp.getText();
    }

    @Override public Class<?> modelClass() {
        return String.class;
    }
}
