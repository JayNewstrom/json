package com.jaynewstrom.json.sample.custom;

import com.fasterxml.jackson.core.JsonGenerator;
import com.jaynewstrom.json.runtime.JsonSerializer;
import com.jaynewstrom.json.runtime.JsonSerializerFactory;

import java.io.IOException;
import java.util.Date;

final class DateSerializer implements JsonSerializer<Date> {
    @Override public void serialize(Date date, JsonGenerator jg, JsonSerializerFactory serializerFactory) throws IOException {
        jg.writeNumber(date.getTime());
    }
}
