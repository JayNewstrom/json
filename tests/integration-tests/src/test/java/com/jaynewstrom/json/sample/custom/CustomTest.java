package com.jaynewstrom.json.sample.custom;

import com.jaynewstrom.json.sample.JsonTestHelper;

import org.junit.Test;

import java.util.Date;

import static org.fest.assertions.api.Assertions.assertThat;

public final class CustomTest {
    @Test public void testCustomSerializer() {
        JsonTestHelper testHelper = new JsonTestHelper();
        String serialized = testHelper.serialize(new Custom(new Date(1480978912499L)));
        assertThat(serialized).isEqualTo("{\"foo\":1480978912499}");
    }

    @Test public void testCustomDeserializer() {
        JsonTestHelper testHelper = new JsonTestHelper();
        Custom custom = testHelper.deserializeString(Custom.class, "{\"foo\":1480978912499}");
        assertThat(custom.foo.getTime()).isEqualTo(1480978912499L);
    }
}
