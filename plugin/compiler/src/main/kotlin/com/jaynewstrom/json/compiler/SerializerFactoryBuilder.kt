package com.jaynewstrom.json.compiler

import com.jaynewstrom.composite.runtime.LibraryModule
import com.jaynewstrom.json.runtime.JsonSerializerFactory
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier

data class SerializerFactoryBuilder(
        private val serializers: Collection<TypeName>
) {
    fun build(): TypeSpec {
        return TypeSpec.classBuilder("RealJsonSerializerFactory")
                .addAnnotation(libraryModuleAnnotation())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .superclass(ClassName.get(JsonSerializerFactory::class.java))
                .addMethod(createConstructor())
                .build()
    }

    private fun libraryModuleAnnotation(): AnnotationSpec {
        return AnnotationSpec.builder(LibraryModule::class.java)
                .addMember("value", "\$T.class", JsonSerializerFactory::class.java)
                .build()
    }

    private fun createConstructor(): MethodSpec {
        val constructorBuilder = MethodSpec.constructorBuilder()
        constructorBuilder.addModifiers(Modifier.PUBLIC)
        constructorBuilder.addStatement("super(\$L)", serializers.size)
        serializers.forEach {
            val codeFormat = "register(\$T.INSTANCE)"
            constructorBuilder.addStatement(codeFormat, it)
        }
        return constructorBuilder.build()
    }
}
