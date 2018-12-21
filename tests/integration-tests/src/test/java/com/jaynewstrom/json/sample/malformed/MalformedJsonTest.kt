package com.jaynewstrom.json.sample.malformed

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.core.io.JsonEOFException
import com.jaynewstrom.json.sample.JsonTestHelper
import org.fest.assertions.api.Assertions.assertThat
import org.junit.Assert.fail
import org.junit.Test

class MalformedJsonTest {
    @Test fun testMissingEndObject() {
        try {
            JsonTestHelper().deserializeFile(Malformed::class.java, "MissingEndObject.json", this)
        } catch (e: AssertionError) {
            assertThat(e.cause).isExactlyInstanceOf(JsonEOFException::class.java)
                .hasMessageContaining("Unexpected end-of-input: expected close marker for Object")
            return  // Catching AssertionError, can't fail the try.
        }

        fail()
    }

    @Test fun testMissingNestedEndArray() {
        try {
            JsonTestHelper().deserializeFile(Malformed::class.java, "MissingNestedEndArray.json", this)
        } catch (e: AssertionError) {
            assertThat(e.cause).isExactlyInstanceOf(JsonParseException::class.java)
                .hasMessageContaining("Unexpected close marker '}': expected ']'")
            return  // Catching AssertionError, can't fail the try.
        }

        fail()
    }

    @Test fun testMissingNestedEndObject() {
        try {
            JsonTestHelper().deserializeFile(Malformed::class.java, "MissingNestedEndObject.json", this)
        } catch (e: AssertionError) {
            assertThat(e.cause).isExactlyInstanceOf(JsonParseException::class.java)
                .hasMessageContaining("Unexpected character ('}' (code 125)): was expecting double-quote to start field name")
            return  // Catching AssertionError, can't fail the try.
        }

        fail()
    }
}
