-keep public class * extends com.jaynewstrom.json.runtime.JsonSerializerFactory { public <init>(); }
-keep public class * extends com.jaynewstrom.json.runtime.JsonDeserializerFactory { public <init>(); }

# Provided JSR305 dependency.
-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault
