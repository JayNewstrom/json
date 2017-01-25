package com.jaynewstrom.json.sample.autoValue;

import com.google.auto.value.AutoValue;

@AutoValue
abstract class Person implements PersonInterface {
    public static Builder builder() {
        return new AutoValue_Person.Builder();
    }

    @AutoValue.Builder
    abstract static class Builder implements PersonBuilderInterface {
    }
}
