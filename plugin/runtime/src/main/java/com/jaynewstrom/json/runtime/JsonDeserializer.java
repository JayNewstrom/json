package com.jaynewstrom.json.runtime;

import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;

public interface JsonDeserializer<T> {
    T deserialize(JsonParser jp, JsonDeserializerFactory deserializerFactory) throws IOException;
}
