package com.jaynewstrom.json.runtime;

public final class CompositeJsonDeserializerFactory extends JsonDeserializerFactory {
    public CompositeJsonDeserializerFactory() {
    }

    public final void registerAll(JsonDeserializerFactory deserializerFactory) {
        deserializerMap.putAll(deserializerFactory.deserializerMap);
    }
}
