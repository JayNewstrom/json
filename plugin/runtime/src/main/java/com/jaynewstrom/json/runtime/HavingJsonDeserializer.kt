package com.jaynewstrom.json.runtime

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
annotation class HavingJsonDeserializer(val value: KClass<out JsonDeserializer<*>>)
