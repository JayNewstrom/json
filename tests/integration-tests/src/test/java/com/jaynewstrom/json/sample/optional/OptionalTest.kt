package com.jaynewstrom.json.sample.optional

import com.jaynewstrom.json.sample.JsonTestHelper
import org.fest.assertions.api.Assertions.assertThat
import org.junit.Test

class OptionalTest {
    @Test fun whenOneIsNull_EnsureTwoIsParsed() {
        val (one, two) = JsonTestHelper().deserializeFile(Optional::class.java, "OneNull.json", this)
        assertThat(one).isNull()
        assertThat(two).isEqualTo("foo")
    }

    @Test fun whenTwoIsNull_EnsureOneIsParsed() {
        val (one, two) = JsonTestHelper().deserializeFile(Optional::class.java, "TwoNull.json", this)
        assertThat(one).isEqualTo("foo")
        assertThat(two).isNull()
    }

    @Test fun ensureBothCanBeNull() {
        val (one, two) = JsonTestHelper().deserializeFile(Optional::class.java, "BothNull.json", this)
        assertThat(one).isNull()
        assertThat(two).isNull()
    }

    @Test fun ensureNullIsSerialized() {
        val result = JsonTestHelper().serialize(Optional(null, "Two"), Optional::class.java)
        assertThat(result).isEqualTo("{\"one\":null,\"two\":\"Two\"}")
    }
}
