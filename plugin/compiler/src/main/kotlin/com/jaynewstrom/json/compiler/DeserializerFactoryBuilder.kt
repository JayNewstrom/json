package com.jaynewstrom.json.compiler

import com.jaynewstrom.composite.runtime.LibraryModule
import com.jaynewstrom.json.runtime.JsonDeserializerFactory
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec

data class DeserializerFactoryBuilder(private val deserializers: Collection<TypeSpec>) {
    fun build(): TypeSpec {
        return TypeSpec.classBuilder("RealJsonDeserializerFactory")
            .addAnnotation(libraryModuleAnnotation())
            .superclass(JsonDeserializerFactory::class)
            .addFunction(createConstructor())
            .build()
    }

    private fun libraryModuleAnnotation(): AnnotationSpec {
        return AnnotationSpec.builder(LibraryModule::class.java)
            .addMember("value", "%T.class", JsonDeserializerFactory::class.java)
            .build()
    }

    private fun createConstructor(): FunSpec {
        val constructorBuilder = FunSpec.constructorBuilder()
        constructorBuilder.addStatement("super(%L)", deserializers.size)
        deserializers.forEach {
            val codeFormat = "register(%T)"
            constructorBuilder.addStatement(codeFormat, it)
        }
        return constructorBuilder.build()
    }
}
