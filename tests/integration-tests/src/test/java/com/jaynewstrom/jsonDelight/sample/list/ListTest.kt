package com.jaynewstrom.jsonDelight.sample.list

import com.jaynewstrom.jsonDelight.sample.JsonTestHelper
import org.fest.assertions.api.Assertions.assertThat
import org.junit.Test

class ListTest {
    @Test fun testAllTheThings() {
        val outer = JsonTestHelper().deserializeFile(Outer::class.java, "ListTest.json", this)
        assertThat(outer.nested).hasSize(2)
        assertThat(outer.nested[0].foo).isEqualTo("a")
        assertThat(outer.nested[1].foo).isEqualTo("b")
        val json = JsonTestHelper().serialize(outer, Outer::class.java)
        assertThat(json).isEqualTo("{\"nested\":[{\"foo\":\"a\"},{\"foo\":\"b\"}]}")
    }

    @Test fun testListOfPrimitives() {
        val basic = JsonTestHelper().deserializeFile(Basic::class.java, "BasicTest.json", this)
        assertThat(basic.basic).hasSize(3)
        assertThat(basic.basic[0]).isEqualTo("a")
        assertThat(basic.basic[1]).isEqualTo("b")
        assertThat(basic.basic[2]).isEqualTo("c")
        val json = JsonTestHelper().serialize(basic, Basic::class.java)
        assertThat(json).isEqualTo("{\"basic\":[\"a\",\"b\",\"c\"]}")
    }
}
