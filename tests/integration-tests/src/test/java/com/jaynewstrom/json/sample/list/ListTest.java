package com.jaynewstrom.json.sample.list;

import com.jaynewstrom.json.sample.JsonTestHelper;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public final class ListTest {
    @Test public void testAllTheThings() {
        Outer outer = new JsonTestHelper().deserializeFile(Outer.class, "ListTest.json", this);
        assertThat(outer.nested).hasSize(2);
        assertThat(outer.nested.get(0).foo).isEqualTo("a");
        assertThat(outer.nested.get(1).foo).isEqualTo("b");
        String json = new JsonTestHelper().serialize(outer);
        assertThat(json).isEqualTo("{\"nested\":[{\"foo\":\"a\"},{\"foo\":\"b\"}]}");
    }

    @Test public void testListOfPrimitives() {
        Basic basic = new JsonTestHelper().deserializeFile(Basic.class, "BasicTest.json", this);
        assertThat(basic.basic).hasSize(3);
        assertThat(basic.basic.get(0)).isEqualTo("a");
        assertThat(basic.basic.get(1)).isEqualTo("b");
        assertThat(basic.basic.get(2)).isEqualTo("c");
        String json = new JsonTestHelper().serialize(basic);
        assertThat(json).isEqualTo("{\"basic\":[\"a\",\"b\",\"c\"]}");
    }

    @Test public void testListIsImmutable() {
        Basic basic = new JsonTestHelper().deserializeFile(Basic.class, "BasicTest.json", this);
        try {
            basic.basic.add("This should fail.");
            fail();
        } catch (UnsupportedOperationException expected) {
            assertThat(expected).isNotNull();
        }
    }
}
