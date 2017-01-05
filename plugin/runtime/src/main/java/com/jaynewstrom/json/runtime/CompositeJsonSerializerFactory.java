package com.jaynewstrom.json.runtime;

public final class CompositeJsonSerializerFactory extends JsonSerializerFactory {
    public CompositeJsonSerializerFactory() {
    }

    public final void registerAll(JsonSerializerFactory serializerFactory) {
        serializerMap.putAll(serializerFactory.serializerMap);
    }
}
