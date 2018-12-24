-if @com.jaynewstrom.json.runtime.HavingJsonSerializer class *
-keep,allowobfuscation class <1>Serializer {
    <init>();
}

-if @com.jaynewstrom.json.runtime.HavingJsonDeserializer class *
-keep,allowobfuscation class <1>Deserializer {
    <init>();
}
