package com.jaynewstrom.json.retrofit;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.jaynewstrom.json.runtime.JsonSerializer;
import com.jaynewstrom.json.runtime.JsonSerializerFactory;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import retrofit2.Converter;

final class RequestBodyConverter<T> implements Converter<T, RequestBody> {
    private final JsonFactory jsonFactory;
    private final JsonSerializer<T> serializer;
    private final JsonSerializerFactory serializerFactory;

    RequestBodyConverter(JsonFactory jsonFactory, JsonSerializer<T> serializer, JsonSerializerFactory serializerFactory) {
        this.jsonFactory = jsonFactory;
        this.serializer = serializer;
        this.serializerFactory = serializerFactory;
    }

    @Override public RequestBody convert(T value) throws IOException {
        return new JsonRequestBody<>(value, jsonFactory, serializer, serializerFactory);
    }

    private static final class JsonRequestBody<T> extends RequestBody {
        private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");

        private final T value;
        private final JsonFactory jsonFactory;
        private final JsonSerializer<T> serializer;
        private final JsonSerializerFactory serializerFactory;

        JsonRequestBody(T value, JsonFactory jsonFactory, JsonSerializer<T> serializer, JsonSerializerFactory serializerFactory) {
            this.value = value;
            this.jsonFactory = jsonFactory;
            this.serializer = serializer;
            this.serializerFactory = serializerFactory;
        }

        @Override public MediaType contentType() {
            return MEDIA_TYPE;
        }

        @Override public void writeTo(BufferedSink sink) throws IOException {
            JsonGenerator generator = jsonFactory.createGenerator(sink.outputStream());
            serializer.serialize(value, generator, serializerFactory);
            generator.close();
        }
    }
}
