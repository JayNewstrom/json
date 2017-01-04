package com.jaynewstrom.json.sample.simple;

import com.jaynewstrom.json.sample.JsonTestHelper;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public final class SkiResortTest {
    @Test public void ensureSkiResortIsDeserializedFromJson() {
        SkiResort skiResort = new JsonTestHelper().deserializeFile(SkiResort.class, "JacksonHole.json", this);
        assertThat(skiResort.name).isEqualTo("Jackson Hole");
        assertThat(skiResort.population).isEqualTo(10_135);
        assertThat(skiResort.averageSnowfallInInches).isEqualTo(459);
    }

    @Test public void ensureSkiResortIsCreatedFromJson() {
        SkiResort telluride = new SkiResort("Telluride", 2_319, 309);
        String json = new JsonTestHelper().serialize(telluride);
        assertThat(json).isEqualTo("{\"name\":\"Telluride\",\"population\":2319,\"averageSnowfall\":309}");
    }
}
