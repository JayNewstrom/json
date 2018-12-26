package com.jaynewstrom.jsonDelight.sample.primitive

import com.jaynewstrom.jsonDelight.sample.JsonTestHelper
import org.fest.assertions.api.Assertions.assertThat
import org.junit.Test

class PrimitiveTest {
    @Test fun testAllTheThings() {
        val primitive = JsonTestHelper().deserializeFile(Primitive::class.java, "PrimitiveTest.json", this)
        assertThat(primitive.aString).isEqualTo("a")
        assertThat(primitive.aBoolean).isEqualTo(true)
        assertThat(primitive.aBoxedBoolean).isEqualTo(false)
        assertThat(primitive.aByte).isEqualTo(0.toByte())
        assertThat(primitive.aBoxedByte).isEqualTo(1.toByte())
        assertThat(primitive.aShort).isEqualTo(2.toShort())
        assertThat(primitive.aBoxedShort).isEqualTo(3.toShort())
        assertThat(primitive.aInt).isEqualTo(4)
        assertThat(primitive.aBoxedInt).isEqualTo(5)
        assertThat(primitive.aLong).isEqualTo(6)
        assertThat(primitive.aBoxedLong).isEqualTo(7)
        assertThat(primitive.aFloat).isEqualTo(0.2f)
        assertThat(primitive.aBoxedFloat).isEqualTo(0.4f)
        assertThat(primitive.aDouble).isEqualTo(0.6)
        assertThat(primitive.aBoxedDouble).isEqualTo(0.8)
        val json = JsonTestHelper().serialize(primitive, Primitive::class.java)
        assertThat(json).isEqualTo("{\"aString\":\"a\",\"aBoolean\":true,\"aBoxedBoolean\":false,\"aByte\":0,\"aBoxedByte\":1," +
            "\"aShort\":2,\"aBoxedShort\":3,\"aInt\":4,\"aBoxedInt\":5,\"aLong\":6,\"aBoxedLong\":7,\"aFloat\":0.2," +
            "\"aBoxedFloat\":0.4,\"aDouble\":0.6,\"aBoxedDouble\":0.8}")
    }
}
