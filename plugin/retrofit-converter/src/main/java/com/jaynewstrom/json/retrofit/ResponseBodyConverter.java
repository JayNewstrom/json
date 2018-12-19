package com.jaynewstrom.json.retrofit;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.jaynewstrom.json.runtime.JsonDeserializer;
import com.jaynewstrom.json.runtime.JsonDeserializerFactory;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

final class ResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final JsonFactory jsonFactory;
    private final JsonDeserializer<T> deserializer;
    private final JsonDeserializerFactory deserializerFactory;

    ResponseBodyConverter(JsonFactory jsonFactory, JsonDeserializer<T> deserializer, JsonDeserializerFactory deserializerFactory) {
        this.jsonFactory = jsonFactory;
        this.deserializer = deserializer;
        this.deserializerFactory = deserializerFactory;
    }

    @Override public T convert(ResponseBody value) throws IOException {
        try {
            JsonParser jsonParser = jsonFactory.createParser(value.charStream());
            jsonParser.nextToken();
            T returnValue = deserializer.deserialize(jsonParser, deserializerFactory);
            jsonParser.close();
            return returnValue;
        } finally {
            value.close();
        }
    }
}
