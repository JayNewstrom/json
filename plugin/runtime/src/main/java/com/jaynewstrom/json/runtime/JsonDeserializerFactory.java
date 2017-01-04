package com.jaynewstrom.json.runtime;

public interface JsonDeserializerFactory {
    <T> JsonDeserializer<T> get(Class<T> modelClass);
}
