package com.jaynewstrom.json.sample.custom;

import com.fasterxml.jackson.core.JsonParser;
import com.jaynewstrom.json.runtime.JsonDeserializer;
import com.jaynewstrom.json.runtime.JsonDeserializerFactory;

import java.io.IOException;
import java.util.Date;

final class DateDeserializer implements JsonDeserializer<Date> {
    @Override public Date deserialize(JsonParser jp, JsonDeserializerFactory deserializerFactory) throws IOException {
        return new Date(jp.getLongValue());
    }
}
