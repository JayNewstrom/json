package com.jaynewstrom.json.runtime;

public interface JsonSerializerFactory {
    <T> JsonSerializer<T> get(Class<T> modelClass);
}
