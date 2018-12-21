package com.jaynewstrom.json.sample.required

import com.jaynewstrom.json.sample.JsonTestHelper
import org.fest.assertions.api.Assertions.assertThat
import org.junit.Assert.fail
import org.junit.Test

class RequiredTest {
    @Test fun whenRequiredFieldIsNull_ensureNullPointerIsThrown() {
        try {
            JsonTestHelper().deserializeFile(Required::class.java, "RequiredTest.json", this)
            fail()
        } catch (e: UninitializedPropertyAccessException) {
            assertThat(e).hasMessage("lateinit property foo has not been initialized")
        }
    }

    @Test fun whenRequiredFieldIsMissing_ensureNullPointerIsThrown() {
        try {
            JsonTestHelper().deserializeFile(Required::class.java, "MissingTest.json", this)
            fail()
        } catch (e: UninitializedPropertyAccessException) {
            assertThat(e).hasMessage("lateinit property foo has not been initialized")
        }
    }
}
