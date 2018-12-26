package com.jaynewstrom.jsonDelight.runtime

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
annotation class HavingJsonSerializer(val value: KClass<out JsonSerializer<*>>)
