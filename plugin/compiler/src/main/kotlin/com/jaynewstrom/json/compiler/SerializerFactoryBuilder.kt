package com.jaynewstrom.json.compiler

import com.jaynewstrom.json.runtime.JsonSerializerFactory
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier

data class SerializerFactoryBuilder(val serializers: Collection<TypeName>) {
    fun build(): TypeSpec {
        return TypeSpec.classBuilder("RealJsonSerializerFactory")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .superclass(ClassName.get(JsonSerializerFactory::class.java))
                .addMethod(createConstructor())
                .build()
    }

    private fun createConstructor(): MethodSpec {
        val constructorBuilder = MethodSpec.constructorBuilder()
        constructorBuilder.addModifiers(Modifier.PUBLIC)
        serializers.forEach {
            val codeFormat = "register(new \$T())"
            constructorBuilder.addStatement(codeFormat, it)
        }
        return constructorBuilder.build()
    }
}