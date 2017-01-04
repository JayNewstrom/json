package com.jaynewstrom.json.runtime.internal;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.jaynewstrom.json.runtime.JsonDeserializer;
import com.jaynewstrom.json.runtime.JsonDeserializerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class ListDeserializer<T> implements JsonDeserializer<List<T>> {
    private final JsonDeserializer<T> elementFactory;

    public ListDeserializer(JsonDeserializer<T> elementFactory) {
        this.elementFactory = elementFactory;
    }

    @Override public List<T> deserialize(JsonParser jp, JsonDeserializerFactory deserializerFactory) throws IOException {
        if (jp.currentToken() != JsonToken.START_ARRAY) {
            throw new IOException("Expected data to start with an Array");
        }

        List<T> list = new ArrayList<>();

        while (jp.nextToken() != JsonToken.END_ARRAY) {
            list.add(elementFactory.deserialize(jp, deserializerFactory));
        }

        return list;
    }

    @Override public Class<?> modelClass() {
        throw new UnsupportedOperationException("The modelClass method shouldn't be used on ListDeserializer.");
    }
}
