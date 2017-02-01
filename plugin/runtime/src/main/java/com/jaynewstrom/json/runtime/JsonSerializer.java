package com.jaynewstrom.json.runtime;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;

public interface JsonSerializer<T> {
    void serialize(T t, JsonGenerator jg, JsonSerializerFactory serializerFactory) throws IOException;
}
