package com.jaynewstrom.json.sample.simple

import com.jaynewstrom.json.sample.JsonTestHelper
import org.fest.assertions.api.Assertions.assertThat
import org.junit.Test

class SkiResortTest {
    @Test fun ensureSkiResortIsDeserializedFromJson() {
        val (name, population, averageSnowfallInInches) = JsonTestHelper().deserializeFile(SkiResort::class.java, "JacksonHole.json", this)
        assertThat(name).isEqualTo("Jackson Hole")
        assertThat(population).isEqualTo(10135)
        assertThat(averageSnowfallInInches).isEqualTo(459)
    }

    @Test fun ensureSkiResortIsCreatedFromJson() {
        val telluride = SkiResort("Telluride", 2319, 309)
        val json = JsonTestHelper().serialize(telluride, SkiResort::class.java)
        assertThat(json).isEqualTo("{\"name\":\"Telluride\",\"population\":2319,\"averageSnowfall\":309}")
    }
}
