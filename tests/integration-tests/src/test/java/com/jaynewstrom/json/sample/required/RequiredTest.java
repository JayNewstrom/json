package com.jaynewstrom.json.sample.required;

import com.jaynewstrom.json.sample.JsonTestHelper;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public final class RequiredTest {
    @Test public void whenRequiredFieldIsNull_ensureNullPointerIsThrown() {
        try {
            new JsonTestHelper().deserializeFile(Required.class, "RequiredTest.json", this);
            fail();
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("foo == null");
        }
    }

    @Test public void whenRequiredFieldIsMissing_ensureNullPointerIsThrown() {
        try {
            new JsonTestHelper().deserializeFile(Required.class, "MissingTest.json", this);
            fail();
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("foo == null");
        }
    }
}
