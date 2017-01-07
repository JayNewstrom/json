package com.jaynewstrom.json.retrofit;

import com.fasterxml.jackson.core.JsonFactory;
import com.jaynewstrom.json.runtime.JsonDeserializer;
import com.jaynewstrom.json.runtime.JsonDeserializerFactory;
import com.jaynewstrom.json.runtime.JsonSerializer;
import com.jaynewstrom.json.runtime.JsonSerializerFactory;
import com.jaynewstrom.json.runtime.internal.ListDeserializer;
import com.jaynewstrom.json.runtime.internal.ListSerializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public final class JsonConverterFactory extends Converter.Factory {
    private final JsonFactory jsonFactory;
    private final JsonSerializerFactory serializerFactory;
    private final JsonDeserializerFactory deserializerFactory;

    private JsonConverterFactory(JsonFactory jsonFactory, JsonSerializerFactory serializerFactory, JsonDeserializerFactory
            deserializerFactory) {
        this.jsonFactory = jsonFactory;
        this.serializerFactory = serializerFactory;
        this.deserializerFactory = deserializerFactory;
    }

    public static Converter.Factory create(JsonFactory jsonFactory, JsonSerializerFactory serializerFactory, JsonDeserializerFactory
            deserializerFactory) {
        if (jsonFactory == null) {
            throw new NullPointerException("jsonFactory == null");
        }
        if (serializerFactory == null) {
            throw new NullPointerException("serializerFactory == null");
        }
        if (deserializerFactory == null) {
            throw new NullPointerException("deserializerFactory == null");
        }
        return new JsonConverterFactory(jsonFactory, serializerFactory, deserializerFactory);
    }

    @Override public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if (type instanceof Class) {
            JsonDeserializer<?> deserializer = deserializerFactory.get((Class<?>) type);
            if (deserializer != null) {
                return new ResponseBodyConverter<>(jsonFactory, deserializer, deserializerFactory);
            }
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            Type firstType = typeArguments[0];
            if (firstType instanceof Class) {
                JsonDeserializer<?> deserializer = deserializerFactory.get((Class<?>) firstType);
                Type rawType = parameterizedType.getRawType();
                if (deserializer != null && rawType == List.class) {
                    return new ResponseBodyConverter<>(jsonFactory, new ListDeserializer<>(deserializer), deserializerFactory);
                }
            }
        }
        return super.responseBodyConverter(type, annotations, retrofit);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations,
            Retrofit retrofit) {
        if (type instanceof Class) {
            JsonSerializer<?> serializer = serializerFactory.get((Class<?>) type);
            if (serializer != null) {
                return new RequestBodyConverter<>(jsonFactory, serializer, serializerFactory);
            }
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            Type firstType = typeArguments[0];
            if (firstType instanceof Class) {
                JsonSerializer<?> serializer = serializerFactory.get((Class<?>) firstType);
                Type rawType = parameterizedType.getRawType();
                if (serializer != null && rawType == List.class) {
                    return new RequestBodyConverter<>(jsonFactory, new ListSerializer<>(serializer), serializerFactory);
                }
            }
        }
        return super.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
    }
}
