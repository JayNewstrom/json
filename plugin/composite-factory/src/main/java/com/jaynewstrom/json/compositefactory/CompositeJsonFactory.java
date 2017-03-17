package com.jaynewstrom.json.compositefactory;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.jaynewstrom.json.runtime.JsonDeserializerFactory;
import com.jaynewstrom.json.runtime.JsonSerializerFactory;

public final class CompositeJsonFactory {
    private static final String JSON_SERIALIZER_FACTORY_VALUE = "JsonSerializerFactory";
    private static final String JSON_DESERIALIZER_FACTORY_VALUE = "JsonDeserializerFactory";

    private final Context context;
    private final JsonSerializerFactory compositeJsonSerializerFactory;
    private final JsonDeserializerFactory compositeJsonDeserializerFactory;

    public CompositeJsonFactory(Context context) {
        this.context = context;
        this.compositeJsonSerializerFactory = new JsonSerializerFactory();
        this.compositeJsonDeserializerFactory = new JsonDeserializerFactory();
        initialize();
    }

    private void initialize() {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo appInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo.metaData == null) {
                return;
            }
            for (String key : appInfo.metaData.keySet()) {
                Object value = appInfo.metaData.get(key);
                if (JSON_SERIALIZER_FACTORY_VALUE.equals(value)) {
                    compositeJsonSerializerFactory.registerAll(parseJsonFactory(key, JsonSerializerFactory.class));
                } else if (JSON_DESERIALIZER_FACTORY_VALUE.equals(value)) {
                    compositeJsonDeserializerFactory.registerAll(parseJsonFactory(key, JsonDeserializerFactory.class));
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Unable to find metadata to parse JsonSerializerFactories", e);
        }
    }

    private static <T> T parseJsonFactory(String className, Class<T> expectedClass) {
        String expectedClassName = expectedClass.getSimpleName();
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Unable to find " + expectedClassName + " implementation", e);
        }

        Object jsonFactory;
        try {
            jsonFactory = clazz.newInstance();
        } catch (Exception e) {
            String message = "Unable to instantiate " + expectedClassName + " implementation for " + clazz;
            throw new RuntimeException(message, e);
        }

        if (!expectedClass.isAssignableFrom(jsonFactory.getClass())) {
            throw new RuntimeException("Expected instanceof " + expectedClassName + ", but found: " + jsonFactory);
        }

        //noinspection unchecked
        return (T) jsonFactory;
    }

    public JsonSerializerFactory serializerFactory() {
        return compositeJsonSerializerFactory;
    }

    public JsonDeserializerFactory deserializerFactory() {
        return compositeJsonDeserializerFactory;
    }
}
