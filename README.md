Overview
--------
A predictable and fast json parser that doesn't use reflection for Android.
Define your models in json - The library will generate models, serializers, and deserializers automatically at build time.

How fast is it?
---------------
Super fast! It's faster than [jackson-databind](https://github.com/FasterXML/jackson-databind), [Gson](https://github.com/google/gson), and [Moshi](https://github.com/square/moshi).
It's very comparable in speed with [LoganSquare](https://github.com/bluelinelabs/LoganSquare) (both libraries use code generation and [jackson-core](https://github.com/FasterXML/jackson-core) under the hood).

How is it different from other json libraries?
----------------------------------------------
- Strictly enforced immutable types
- 100% Generated code rather than using reflection
- Fully proguard ready

How does it work?
-----------------
The library uses a custom gradle task to generate models based on the definitions you provide.
It can also generate and automatically register serializers and deserializers.  

Setup
-----
```groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.jaynewstrom.json:gradle-plugin:0.8.0'
    }
}

apply plugin: 'com.android.application'
apply plugin: 'com.jaynewstrom.json'

json {
    defaultPackage {your base package here}
}
```

Usage
-----
Define your models in /src/main/json/{the package you want the model to live in}. The gradle task will create the models for you at build time.
An example json file is below.
```json
{
  "public": false, // Not required, defaults to false.
  "createSerializer": true, // Not required, defaults to false.
  "createDeserializer": false, // Not required, defaults to false.
  "useAutoValue": true, // Not required, defaults to false.
  "fields": [
    {
      "name": "foo", // Required!
      "type": "String", // Required!
      "public": false, // Not required, defaults to the types public flag.
      "jsonName": "foo", // Not required, defaults to the "name" property.
      "isList": false, // Not required, defaults to false.
      "required": true, // Not required, defaults to true if object, defaults to false if primitive.
      "customSerializer": null, // Not required.
      "customDeserializer": null // Not required.
    }
  ]
}
```

Custom Serializers/Deserializers
--------------------------------
Have a type that you can't generate? Platform types such as java.Util.Date can still be (de)serialized!
Just call `realJsonDeserializerFactory.register(...)` or `realJsonSerializerFactory.register(...)` to register your custom (de)serializers.

Field Specific Serializers/Deserializers
----------------------------------------
If your server is sending data back in a goofy way `{"foo": "true"}` you can use a custom deserializer to deserialize it into a boolean, rather than a string.
Just add `"customDeserializer": "LenientBooleanDeserializer"` to the field definition when defining it in json.
You can also do tricks like this to deserialize polymorphic types and nested types.

Working with other json parsers
-------------------------------
Want to work with data that is a little more dynamic? Want to use jackson-databind? Have models that aren't performance critical?
You can use a custom `JsonSerializer` or `JsonDeserializer` to bridge the gap between the libraries!
Just call `realJsonDeserializerFactory.register(...)` or `realJsonSerializerFactory.register(...)` to register your custom (de)serializers.

Use with AutoValue
------------------
```groovy
dependencies {
    ...
    annotationProcessor 'com.google.auto.value:auto-value:1.3'
    provided 'com.jakewharton.auto.value:auto-value-annotations:1.3'
}
```

Specify the "useAutoValue" property on your model definition.
This will generate an interface with the suffix of `Interface`.
A model defined as `SkiResort.json` will generate an interface of `SkiResortInterface.java`.
It's expected that you will extend this interface and add anything extra here (no extra properties!).
The generated (de)serializers expect your class that extends `SkiResortInterface` to be name `SkiResort`.

Use with Retrofit
-----------------
Add the retrofit dependency to your `build.gradle`.

```groovy
dependencies {
    ...
    compile 'com.jaynewstrom.json:retrofit-converter:0.8.0'
}
```

Add the converter to your retrofit instance.

```java
new Retrofit.Builder()
    ...
    .addConverterFactory(JsonConverterFactory.create(new JsonFactory(), new RealJsonSerializerFactory(), new RealJsonDeserializerFactory()))
    .build();
```

Any models used in a retrofit call will be run through the converter.

Upcoming Features
-----------------
- Better error messages - Please report difficult to understand errors!
- Easier model definition using code DSL
- Better documentation - Please report areas that need more/better documentation!

Local Development
-----------------
This project uses gradle as a build system, and uses the new composite builds. See [LOCAL_DEVELOPMENT.md](LOCAL_DEVELOPMENT.md) for working on this project.

Thanks!
-------
I got the idea for this library by looking into [SqlDelight](https://github.com/square/sqldelight/), and a lot of the basic needs of this library come from examples within SqlDelight.

License
-------
    Copyright 2016 Jay Newstrom

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
