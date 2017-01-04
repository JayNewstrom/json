package com.jaynewstrom.json.runtime.internal;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;

public final class ExtraneousArrayConsumer {
    public static final ExtraneousArrayConsumer INSTANCE = new ExtraneousArrayConsumer();

    private ExtraneousArrayConsumer() {
    }

    public void consume(JsonParser jp) throws IOException {
        // Ensure we are in the correct state.
        if (jp.currentToken() != JsonToken.START_ARRAY) {
            throw new IOException("Expected data to start with an Array");
        }
        int extraneousArrays = 1;
        while (extraneousArrays != 0) {
            JsonToken nextToken = jp.nextToken();
            if (nextToken == JsonToken.START_ARRAY) {
                extraneousArrays++;
            } else if (nextToken == JsonToken.END_ARRAY) {
                extraneousArrays--;
            }
        }
    }
}
