-if @com.jaynewstrom.jsonDelight.runtime.HavingJsonSerializer class *
-keep,allowobfuscation class <1>Serializer {
    <init>();
}

-if @com.jaynewstrom.jsonDelight.runtime.HavingJsonDeserializer class *
-keep,allowobfuscation class <1>Deserializer {
    <init>();
}
