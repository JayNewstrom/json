package com.jaynewstrom.json.sample.extraneous;

import com.jaynewstrom.json.sample.JsonTestHelper;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public final class ExtraneousTest {
    @Test public void testDeserializer_whenThereIsAnExtraneousObjectInJson() {
        JsonTestHelper testHelper = new JsonTestHelper();
        String json = "{\"extra\":{\"extra\":{\"readAllAboutIt\":{}}},\"foo\":\"bar\",\"extra2\":{}}";
        Extraneous extraneous = testHelper.deserializeString(Extraneous.class, json);
        assertThat(extraneous.foo).isEqualTo("bar");
    }

    @Test public void testDeserializer_whenThereIsAnExtraneousArrayInJson() {
        JsonTestHelper testHelper = new JsonTestHelper();
        String json = "{\"extra\":[{\"extra\":[\"readAllAboutIt\"]}],\"foo\":\"bar\",\"extra2\":[]}";
        Extraneous extraneous = testHelper.deserializeString(Extraneous.class, json);
        assertThat(extraneous.foo).isEqualTo("bar");
    }

    @Test public void testDeserializer_whenThereIsAnExtraneousField() {
        JsonTestHelper testHelper = new JsonTestHelper();
        String json = "{\"extra\":\"blah\",\"foo\":\"bar\",\"extra2\":false}";
        Extraneous extraneous = testHelper.deserializeString(Extraneous.class, json);
        assertThat(extraneous.foo).isEqualTo("bar");
    }
}
