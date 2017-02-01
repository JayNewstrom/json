package com.jaynewstrom.json.sample.custom;

import com.fasterxml.jackson.core.JsonParser;
import com.jaynewstrom.json.runtime.JsonDeserializer;
import com.jaynewstrom.json.runtime.JsonDeserializerFactory;

import java.io.IOException;

final class LenientBooleanDeserializer implements JsonDeserializer<Boolean> {
    @Override public Boolean deserialize(JsonParser jp, JsonDeserializerFactory deserializerFactory) throws IOException {
        return jp.getValueAsBoolean();
    }
}
