package com.jaynewstrom.json.runtime.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.jaynewstrom.json.runtime.JsonSerializer;
import com.jaynewstrom.json.runtime.JsonSerializerFactory;

import java.io.IOException;
import java.util.List;

public final class ListSerializer<T> implements JsonSerializer<List<T>> {
    private final JsonSerializer<T> elementSerializer;

    public ListSerializer(JsonSerializer<T> elementSerializer) {
        this.elementSerializer = elementSerializer;
    }

    @Override public void serialize(List<T> elements, JsonGenerator jg, JsonSerializerFactory serializerFactory) throws IOException {
        jg.writeStartArray();
        for (int i = 0, size = elements.size(); i < size; i++) {
            elementSerializer.serialize(elements.get(i), jg, serializerFactory);
        }
        jg.writeEndArray();
    }
}
