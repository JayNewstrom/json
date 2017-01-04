package com.jaynewstrom.json.sample.nested;

import com.jaynewstrom.json.sample.JsonTestHelper;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public final class NestedTest {
    @Test public void testAllTheThings() {
        Outer outer = new JsonTestHelper().deserializeFile(Outer.class, "NestedTest.json", this);
        assertThat(outer.one).isEqualTo("a");
        assertThat(outer.two.foo).isEqualTo("b");
        String json = new JsonTestHelper().serialize(outer);
        assertThat(json).isEqualTo("{\"one\":\"a\",\"two\":{\"foo\":\"b\"}}");
    }
}
