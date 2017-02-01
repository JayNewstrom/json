package com.jaynewstrom.json.runtime.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.jaynewstrom.json.runtime.JsonDeserializer;
import com.jaynewstrom.json.runtime.JsonDeserializerFactory;
import com.jaynewstrom.json.runtime.JsonRegistrable;
import com.jaynewstrom.json.runtime.JsonSerializer;
import com.jaynewstrom.json.runtime.JsonSerializerFactory;

import java.io.IOException;

public final class ByteJsonAdapter implements JsonSerializer<Byte>, JsonDeserializer<Byte>, JsonRegistrable {
    public static final ByteJsonAdapter INSTANCE = new ByteJsonAdapter();

    private ByteJsonAdapter() {
    }

    @Override public void serialize(Byte b, JsonGenerator jg, JsonSerializerFactory serializerFactory) throws IOException {
        jg.writeNumber(b);
    }

    @Override public Byte deserialize(JsonParser jp, JsonDeserializerFactory deserializerFactory) throws IOException {
        return jp.getByteValue();
    }

    @Override public Class<?> modelClass() {
        return Byte.class;
    }
}
