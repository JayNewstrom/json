package com.jaynewstrom.json.sample;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.jaynewstrom.json.runtime.JsonDeserializer;
import com.jaynewstrom.json.runtime.JsonSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public final class JsonTestHelper {
    private final JsonFactory jsonFactory = new JsonFactory();
    private final RealJsonDeserializerFactory deserializerFactory = new RealJsonDeserializerFactory();
    private final RealJsonSerializerFactory serializerFactory = new RealJsonSerializerFactory();

    public JsonTestHelper() {
    }

    public <T> T deserializeFile(Class<T> modelClass, String fileName, Object testObject) {
        try {
            InputStream inputStream = testObject.getClass().getResource(fileName).openStream();
            return deserialize(modelClass, jsonFactory.createParser(inputStream));
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    public <T> T deserializeString(Class<T> modelClass, String json) {
        try {
            return deserialize(modelClass, jsonFactory.createParser(json));
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    private <T> T deserialize(Class<T> modelClass, JsonParser jsonParser) {
        try {
            jsonParser.nextToken();
            JsonDeserializer<T> deserializer = deserializerFactory.get(modelClass);
            return deserializer.deserialize(jsonParser, deserializerFactory);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    public <T> String serialize(T model) {
        //noinspection unchecked
        return serialize(model, (Class<T>) model.getClass());
    }

    public <T> String serialize(T model, Class<T> modelClass) {
        try {
            StringWriter sw = new StringWriter();
            JsonGenerator generator = jsonFactory.createGenerator(sw);
            JsonSerializer<T> serializer = serializerFactory.get(modelClass);
            serializer.serialize(model, generator, serializerFactory);
            generator.close();
            return sw.toString();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}
