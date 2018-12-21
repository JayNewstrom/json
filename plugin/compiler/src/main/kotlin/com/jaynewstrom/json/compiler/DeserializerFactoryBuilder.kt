package com.jaynewstrom.json.compiler

import com.jaynewstrom.composite.runtime.LibraryModule
import com.jaynewstrom.json.runtime.JsonDeserializerFactory
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec

data class DeserializerFactoryBuilder(private val deserializers: Collection<TypeName>) {
    fun build(): TypeSpec {
        return TypeSpec.classBuilder("RealJsonDeserializerFactory")
            .addAnnotation(libraryModuleAnnotation())
            .superclass(JsonDeserializerFactory::class)
            .primaryConstructor(createConstructor())
            .addSuperclassConstructorParameter("%L", deserializers.size)
            .build()
    }

    private fun libraryModuleAnnotation(): AnnotationSpec {
        return AnnotationSpec.builder(LibraryModule::class.java)
            .addMember("%T::class", JsonDeserializerFactory::class.java)
            .build()
    }

    private fun createConstructor(): FunSpec {
        val constructorBuilder = FunSpec.constructorBuilder()
        deserializers.forEach {
            val codeFormat = "register(%T)"
            constructorBuilder.addStatement(codeFormat, it)
        }
        return constructorBuilder.build()
    }
}
