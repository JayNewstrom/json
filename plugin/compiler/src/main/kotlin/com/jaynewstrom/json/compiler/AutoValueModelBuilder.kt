package com.jaynewstrom.json.compiler

import com.jaynewstrom.json.runtime.internal.Nullable
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier

internal data class AutoValueModelBuilder(
        private val isPublic: Boolean,
        private val name: String,
        private val fields: List<FieldDefinition>
) {
    fun build(): TypeSpec {
        val classBuilder = TypeSpec.interfaceBuilder("$name${JsonCompiler.INTERFACE_SUFFIX}")
        if (isPublic) {
            classBuilder.addModifiers(Modifier.PUBLIC)
        }
        fields.forEach { field ->
            classBuilder.addMethod(field.methodSpec())
        }
        return classBuilder.build()
    }

    private fun FieldDefinition.methodSpec(): MethodSpec {
        val methodBuilder = MethodSpec.methodBuilder("get${fieldName.capitalize()}")
        methodBuilder.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        methodBuilder.returns(type)
        if (nullable) {
            methodBuilder.addAnnotation(Nullable::class.java)
        }
        return methodBuilder.build()
    }
}
