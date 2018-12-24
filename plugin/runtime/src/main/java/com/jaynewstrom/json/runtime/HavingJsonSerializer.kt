package com.jaynewstrom.json.runtime

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
annotation class HavingJsonSerializer(val value: KClass<out JsonSerializer<*>>)
