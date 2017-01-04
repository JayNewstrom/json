package com.jaynewstrom.json.sample.custom;

import com.jaynewstrom.json.sample.JsonTestHelper;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public final class LenientTest {
    @Test public void testLenientSerializerTrue() {
        JsonTestHelper testHelper = new JsonTestHelper();
        String serialized = testHelper.serialize(new Lenient(true));
        assertThat(serialized).isEqualTo("{\"foo\":\"true\"}");
    }

    @Test public void testLenientSerializerFalse() {
        JsonTestHelper testHelper = new JsonTestHelper();
        String serialized = testHelper.serialize(new Lenient(false));
        assertThat(serialized).isEqualTo("{\"foo\":\"false\"}");
    }

    @Test public void testLenientDeserializerTrue() {
        JsonTestHelper testHelper = new JsonTestHelper();
        Lenient lenient = testHelper.deserializeString(Lenient.class, "{\"foo\":\"true\"}");
        assertThat(lenient.foo).isEqualTo(true);
    }

    @Test public void testLenientDeserializerFalse() {
        JsonTestHelper testHelper = new JsonTestHelper();
        Lenient lenient = testHelper.deserializeString(Lenient.class, "{\"foo\":\"false\"}");
        assertThat(lenient.foo).isEqualTo(false);
    }
}
