package com.jaynewstrom.json.sample.malformed;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.io.JsonEOFException;
import com.jaynewstrom.json.sample.JsonTestHelper;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public final class MalformedJsonTest {
    @Test public void testMissingEndObject() {
        try {
            new JsonTestHelper().deserializeFile(Malformed.class, "MissingEndObject.json", this);
        } catch (AssertionError e) {
            assertThat(e.getCause()).isExactlyInstanceOf(JsonEOFException.class)
                    .hasMessageContaining("Unexpected end-of-input: expected close marker for Object");
            return; // Catching AssertionError, can't fail the try.
        }
        fail();
    }

    @Test public void testMissingNestedEndArray() {
        try {
            new JsonTestHelper().deserializeFile(Malformed.class, "MissingNestedEndArray.json", this);
        } catch (AssertionError e) {
            assertThat(e.getCause()).isExactlyInstanceOf(JsonParseException.class)
                    .hasMessageContaining("Unexpected close marker '}': expected ']'");
            return; // Catching AssertionError, can't fail the try.
        }
        fail();
    }

    @Test public void testMissingNestedEndObject() {
        try {
            new JsonTestHelper().deserializeFile(Malformed.class, "MissingNestedEndObject.json", this);
        } catch (AssertionError e) {
            assertThat(e.getCause()).isExactlyInstanceOf(JsonParseException.class)
                    .hasMessageContaining("Unexpected character ('}' (code 125)): was expecting double-quote to start field name");
            return; // Catching AssertionError, can't fail the try.
        }
        fail();
    }
}
