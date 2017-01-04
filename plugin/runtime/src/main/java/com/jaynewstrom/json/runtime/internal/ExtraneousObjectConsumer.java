package com.jaynewstrom.json.runtime.internal;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;

public final class ExtraneousObjectConsumer {
    public static final ExtraneousObjectConsumer INSTANCE = new ExtraneousObjectConsumer();

    private ExtraneousObjectConsumer() {
    }

    public void consume(JsonParser jp) throws IOException {
        // Ensure we are in the correct state.
        if (jp.currentToken() != JsonToken.START_OBJECT) {
            throw new IOException("Expected data to start with an Object");
        }
        int extraneousObjects = 1;
        while (extraneousObjects != 0) {
            JsonToken nextToken = jp.nextToken();
            if (nextToken == JsonToken.START_OBJECT) {
                extraneousObjects++;
            } else if (nextToken == JsonToken.END_OBJECT) {
                extraneousObjects--;
            }
        }
    }
}
