package com.jaynewstrom.json.runtime.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.jaynewstrom.json.runtime.JsonDeserializer;
import com.jaynewstrom.json.runtime.JsonDeserializerFactory;
import com.jaynewstrom.json.runtime.JsonRegistrable;
import com.jaynewstrom.json.runtime.JsonSerializer;
import com.jaynewstrom.json.runtime.JsonSerializerFactory;

import java.io.IOException;

public final class LongJsonAdapter implements JsonSerializer<Long>, JsonDeserializer<Long>, JsonRegistrable {
    public static final LongJsonAdapter INSTANCE = new LongJsonAdapter();

    private LongJsonAdapter() {
    }

    @Override public void serialize(Long l, JsonGenerator jg, JsonSerializerFactory serializerFactory) throws IOException {
        jg.writeNumber(l);
    }

    @Override public Long deserialize(JsonParser jp, JsonDeserializerFactory deserializerFactory) throws IOException {
        return jp.getLongValue();
    }

    @Override public Class<?> modelClass() {
        return Long.class;
    }
}
