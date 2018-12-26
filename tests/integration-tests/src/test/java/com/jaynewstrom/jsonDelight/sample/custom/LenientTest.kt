package com.jaynewstrom.jsonDelight.sample.custom

import com.jaynewstrom.jsonDelight.sample.JsonTestHelper
import org.fest.assertions.api.Assertions.assertThat
import org.junit.Test

class LenientTest {
    @Test fun testLenientSerializerTrue() {
        val testHelper = JsonTestHelper()
        val serialized = testHelper.serialize(Lenient(true), Lenient::class.java)
        assertThat(serialized).isEqualTo("{\"foo\":\"true\"}")
    }

    @Test fun testLenientSerializerFalse() {
        val testHelper = JsonTestHelper()
        val serialized = testHelper.serialize(Lenient(false), Lenient::class.java)
        assertThat(serialized).isEqualTo("{\"foo\":\"false\"}")
    }

    @Test fun testLenientDeserializerTrue() {
        val testHelper = JsonTestHelper()
        val (foo) = testHelper.deserializeString(Lenient::class.java, "{\"foo\":\"true\"}")
        assertThat(foo).isEqualTo(true)
    }

    @Test fun testLenientDeserializerFalse() {
        val testHelper = JsonTestHelper()
        val (foo) = testHelper.deserializeString(Lenient::class.java, "{\"foo\":\"false\"}")
        assertThat(foo).isEqualTo(false)
    }
}
