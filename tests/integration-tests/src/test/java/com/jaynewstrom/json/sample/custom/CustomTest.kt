package com.jaynewstrom.json.sample.custom

import com.jaynewstrom.json.sample.JsonTestHelper
import org.fest.assertions.api.Assertions.assertThat
import org.junit.Test
import java.util.Date

class CustomTest {
    @Test fun testCustomSerializer() {
        val testHelper = JsonTestHelper()
        val serialized = testHelper.serialize(Custom(Date(1480978912499L)), Custom::class.java)
        assertThat(serialized).isEqualTo("{\"foo\":1480978912499}")
    }

    @Test fun testCustomDeserializer() {
        val testHelper = JsonTestHelper()
        val (foo) = testHelper.deserializeString(Custom::class.java, "{\"foo\":1480978912499}")
        assertThat(foo.time).isEqualTo(1480978912499L)
    }
}
