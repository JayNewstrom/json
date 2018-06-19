package com.jaynewstrom.json.sample.optional;

import com.jaynewstrom.json.sample.JsonTestHelper;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public final class OptionalTest {
    @Test public void whenOneIsNull_EnsureTwoIsParsed() {
        Optional optional = new JsonTestHelper().deserializeFile(Optional.class, "OneNull.json", this);
        assertThat(optional.one).isNull();
        assertThat(optional.two).isEqualTo("foo");
    }

    @Test public void whenTwoIsNull_EnsureOneIsParsed() {
        Optional optional = new JsonTestHelper().deserializeFile(Optional.class, "TwoNull.json", this);
        assertThat(optional.one).isEqualTo("foo");
        assertThat(optional.two).isNull();
    }

    @Test public void ensureBothCanBeNull() {
        Optional optional = new JsonTestHelper().deserializeFile(Optional.class, "BothNull.json", this);
        assertThat(optional.one).isNull();
        assertThat(optional.two).isNull();
    }

    @Test public void ensureNullIsSerialized() {
        String result = new JsonTestHelper().serialize(new Optional(null, "Two"));
        assertThat(result).isEqualTo("{\"one\":null,\"two\":\"Two\"}");
    }
}
