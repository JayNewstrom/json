package com.jaynewstrom.jsonDelight.runtime

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
annotation class HavingJsonDeserializer(val value: KClass<out JsonDeserializer<*>>)
