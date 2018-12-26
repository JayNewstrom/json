package com.jaynewstrom.jsonDelight.sample.extraneous

import com.jaynewstrom.jsonDelight.sample.JsonTestHelper
import org.fest.assertions.api.Assertions.assertThat
import org.junit.Test

class ExtraneousTest {
    @Test fun testDeserializer_whenThereIsAnExtraneousObjectInJson() {
        val testHelper = JsonTestHelper()
        val json = "{\"extra\":{\"extra\":{\"readAllAboutIt\":{}}},\"foo\":\"bar\",\"extra2\":{}}"
        val (foo) = testHelper.deserializeString(Extraneous::class.java, json)
        assertThat(foo).isEqualTo("bar")
    }

    @Test fun testDeserializer_whenThereIsAnExtraneousArrayInJson() {
        val testHelper = JsonTestHelper()
        val json = "{\"extra\":[{\"extra\":[\"readAllAboutIt\"]}],\"foo\":\"bar\",\"extra2\":[]}"
        val (foo) = testHelper.deserializeString(Extraneous::class.java, json)
        assertThat(foo).isEqualTo("bar")
    }

    @Test fun testDeserializer_whenThereIsAnExtraneousField() {
        val testHelper = JsonTestHelper()
        val json = "{\"extra\":\"blah\",\"foo\":\"bar\",\"extra2\":false}"
        val (foo) = testHelper.deserializeString(Extraneous::class.java, json)
        assertThat(foo).isEqualTo("bar")
    }
}
