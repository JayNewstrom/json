package com.jaynewstrom.json.sample.autoValue;

import com.jaynewstrom.json.sample.JsonTestHelper;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public final class AutoValueTest {
    @Test public void testRequired() {
        Value value = new JsonTestHelper().deserializeFile(Value.class, "AutoValueTest.json", this);
        assertThat(value.getName()).isEqualTo("foo");
        String json = new JsonTestHelper().serialize(value, Value.class);
        assertThat(json).isEqualTo("{\"name\":\"foo\"}");
    }

    @Test public void testMissingNotRequired() {
        NullValue value = new JsonTestHelper().deserializeString(NullValue.class, "{}");
        assertThat(value.getName()).isEqualTo(null);
        String json = new JsonTestHelper().serialize(value, NullValue.class);
        assertThat(json).isEqualTo("{\"name\":null}");
    }

    @Test public void testPresentNotRequired() {
        NullValue value = new JsonTestHelper().deserializeFile(NullValue.class, "AutoValueTest.json", this);
        assertThat(value.getName()).isEqualTo("foo");
        String json = new JsonTestHelper().serialize(value, NullValue.class);
        assertThat(json).isEqualTo("{\"name\":\"foo\"}");
    }
}
