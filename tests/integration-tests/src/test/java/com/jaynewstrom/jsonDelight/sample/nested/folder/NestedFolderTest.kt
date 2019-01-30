package com.jaynewstrom.jsonDelight.sample.nested.folder

import com.jaynewstrom.jsonDelight.sample.JsonTestHelper
import org.fest.assertions.api.Assertions.assertThat
import org.junit.Test

class NestedFolderTest {
    @Test fun testAllTheThings() {
        val simple = JsonTestHelper().deserializeFile(Simple::class.java, "NestedFolderTest.json", this)
        assertThat(simple.name).isEqualTo("Jay")
        val json = JsonTestHelper().serialize(simple, Simple::class.java)
        assertThat(json).isEqualTo("{\"name\":\"Jay\"}")
    }
}
