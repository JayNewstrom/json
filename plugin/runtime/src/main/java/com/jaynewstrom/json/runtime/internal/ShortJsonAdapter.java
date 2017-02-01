package com.jaynewstrom.json.runtime.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.jaynewstrom.json.runtime.JsonDeserializer;
import com.jaynewstrom.json.runtime.JsonDeserializerFactory;
import com.jaynewstrom.json.runtime.JsonRegistrable;
import com.jaynewstrom.json.runtime.JsonSerializer;
import com.jaynewstrom.json.runtime.JsonSerializerFactory;

import java.io.IOException;

public final class ShortJsonAdapter implements JsonSerializer<Short>, JsonDeserializer<Short>, JsonRegistrable {
    public static final ShortJsonAdapter INSTANCE = new ShortJsonAdapter();

    private ShortJsonAdapter() {
    }

    @Override public void serialize(Short s, JsonGenerator jg, JsonSerializerFactory serializerFactory) throws IOException {
        jg.writeNumber(s);
    }

    @Override public Short deserialize(JsonParser jp, JsonDeserializerFactory deserializerFactory) throws IOException {
        return jp.getShortValue();
    }

    @Override public Class<?> modelClass() {
        return Short.class;
    }
}
