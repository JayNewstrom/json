package com.jaynewstrom.json.sample.custom;

import com.fasterxml.jackson.core.JsonGenerator;
import com.jaynewstrom.json.runtime.JsonSerializer;
import com.jaynewstrom.json.runtime.JsonSerializerFactory;

import java.io.IOException;

final class LenientBooleanSerializer implements JsonSerializer<Boolean> {
    @Override public void serialize(Boolean aBoolean, JsonGenerator jg, JsonSerializerFactory serializerFactory) throws IOException {
        jg.writeString(aBoolean.toString());
    }

    @Override public Class<?> modelClass() {
        return Boolean.class;
    }
}
