package com.jaynewstrom.json.compiler

import com.jaynewstrom.composite.runtime.LibraryModule
import com.jaynewstrom.json.runtime.JsonSerializerFactory
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec

data class SerializerFactoryBuilder(
    private val serializers: Collection<TypeSpec>
) {
    fun build(): TypeSpec {
        return TypeSpec.classBuilder("RealJsonSerializerFactory")
            .addAnnotation(libraryModuleAnnotation())
            .superclass(JsonSerializerFactory::class.java)
            .addFunction(createConstructor())
            .build()
    }

    private fun libraryModuleAnnotation(): AnnotationSpec {
        return AnnotationSpec.builder(LibraryModule::class.java)
            .addMember("value", "%T.class", JsonSerializerFactory::class.java)
            .build()
    }

    private fun createConstructor(): FunSpec {
        val constructorBuilder = FunSpec.constructorBuilder()
        constructorBuilder.addStatement("super(%L)", serializers.size)
        serializers.forEach {
            val codeFormat = "register(%T)"
            constructorBuilder.addStatement(codeFormat, it)
        }
        return constructorBuilder.build()
    }
}
