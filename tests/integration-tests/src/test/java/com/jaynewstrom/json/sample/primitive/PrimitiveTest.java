package com.jaynewstrom.json.sample.primitive;

import com.jaynewstrom.json.sample.JsonTestHelper;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public final class PrimitiveTest {
    @Test public void testAllTheThings() {
        Primitive primitive = new JsonTestHelper().deserializeFile(Primitive.class, "PrimitiveTest.json", this);
        assertThat(primitive.aString).isEqualTo("a");
        assertThat(primitive.aBoolean).isEqualTo(true);
        assertThat(primitive.aBoxedBoolean).isEqualTo(false);
        assertThat(primitive.aByte).isEqualTo((byte) 0);
        assertThat(primitive.aBoxedByte).isEqualTo((byte) 1);
        assertThat(primitive.aShort).isEqualTo((short) 2);
        assertThat(primitive.aBoxedShort).isEqualTo((short) 3);
        assertThat(primitive.aInt).isEqualTo(4);
        assertThat(primitive.aBoxedInt).isEqualTo(5);
        assertThat(primitive.aLong).isEqualTo(6);
        assertThat(primitive.aBoxedLong).isEqualTo(7);
        assertThat(primitive.aFloat).isEqualTo(0.2f);
        assertThat(primitive.aBoxedFloat).isEqualTo(0.4f);
        assertThat(primitive.aDouble).isEqualTo(0.6);
        assertThat(primitive.aBoxedDouble).isEqualTo(0.8);
        String json = new JsonTestHelper().serialize(primitive);
        assertThat(json).isEqualTo("{\"aString\":\"a\",\"aBoolean\":true,\"aBoxedBoolean\":false,\"aByte\":0,\"aBoxedByte\":1," +
                "\"aShort\":2,\"aBoxedShort\":3,\"aInt\":4,\"aBoxedInt\":5,\"aLong\":6,\"aBoxedLong\":7,\"aFloat\":0.2," +
                "\"aBoxedFloat\":0.4,\"aDouble\":0.6,\"aBoxedDouble\":0.8}");
    }
}
