package com.jaynewstrom.composite;

import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import java.util.Arrays;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;

// A single unit test to make debugging in the IDE easier, full testing provided by integration tests.
public final class CompositeFactoryProcessorTest {
    @Test public void testProcessorWithoutAddsTo() {
        JavaFileObject serializer = JavaFileObjects.forSourceLines("com.example.ValueSerializer",
                "package com.example;",
                "import com.fasterxml.jackson.core.JsonGenerator;",
                "import com.jaynewstrom.json.runtime.AddToCompositeFactory;",
                "import com.jaynewstrom.json.runtime.JsonSerializer;",
                "import com.jaynewstrom.json.runtime.JsonSerializerFactory;",
                "import java.io.IOException;",
                "@AddToCompositeFactory",
                "public final class ValueSerializer implements JsonSerializer<Object> {",
                "    @Override public Class<?> modelClass() {",
                "        return Object.class;",
                "    }",
                "    @Override",
                "    public void serialize(Object model, JsonGenerator jg, JsonSerializerFactory sf) throws IOException {",
                "        jg.writeStartObject();",
                "        jg.writeEndObject();",
                "    }",
                "}"
        );

        JavaFileObject deserializer = JavaFileObjects.forSourceLines("com.example.ValueDeserializer",
                "package com.example;",
                "import com.fasterxml.jackson.core.JsonParser;",
                "import com.jaynewstrom.json.runtime.AddToCompositeFactory;",
                "import com.jaynewstrom.json.runtime.JsonDeserializer;",
                "import com.jaynewstrom.json.runtime.JsonDeserializerFactory;",
                "import java.io.IOException;",
                "@AddToCompositeFactory",
                "public final class ValueDeserializer implements JsonDeserializer<Object> {",
                "    @Override public Class<?> modelClass() {",
                "        return Object.class;",
                "    }",
                "    @Override",
                "    public Object deserialize(JsonParser jp, JsonDeserializerFactory deserializerFactory) throws IOException {",
                "        return new Object();",
                "    }",
                "}"
        );

        JavaFileObject expectedSerializerFactory = JavaFileObjects.forSourceLines("CompositeJsonSerializerFactory",
                "package com.jaynewstrom.json.runtime;",
                "",
                "import com.example.ValueSerializer;",
                "",
                "public final class CompositeJsonSerializerFactory extends JsonSerializerFactory {",
                "  public CompositeJsonSerializerFactory() {",
                "    super(1);",
                "    register(new ValueSerializer());",
                "  }",
                "}"
        );

        JavaFileObject expectedDeserializerFactory = JavaFileObjects.forSourceLines("CompositeJsonDeserializerFactory",
                "package com.jaynewstrom.json.runtime;",
                "",
                "import com.example.ValueDeserializer;",
                "",
                "public final class CompositeJsonDeserializerFactory extends JsonDeserializerFactory {",
                "  public CompositeJsonDeserializerFactory() {",
                "    super(1);",
                "    register(new ValueDeserializer());",
                "  }",
                "}"
        );

        assertAbout(javaSources())
                .that(Arrays.asList(serializer, deserializer))
                .processedWith(new CompositeFactoryProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedSerializerFactory, expectedDeserializerFactory);
    }
}