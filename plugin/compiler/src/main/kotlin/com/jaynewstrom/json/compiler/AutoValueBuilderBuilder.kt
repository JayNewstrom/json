package com.jaynewstrom.json.compiler

import com.jaynewstrom.json.runtime.internal.Nullable
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier

internal data class AutoValueBuilderBuilder(
        private val packageName: String,
        private val isPublic: Boolean,
        private val name: String,
        private val fields: List<FieldDefinition>
) {
    fun build(): TypeSpec {
        val classBuilder = TypeSpec.interfaceBuilder("$name${JsonCompiler.INTERFACE_BUILDER_SUFFIX}")
        if (isPublic) {
            classBuilder.addModifiers(Modifier.PUBLIC)
        }
        fields.forEach { field ->
            classBuilder.addMethod(field.methodSpec())
        }
        classBuilder.addMethod(buildMethod())
        return classBuilder.build()
    }

    private fun FieldDefinition.methodSpec(): MethodSpec {
        val methodBuilder = MethodSpec.methodBuilder("set${fieldName.capitalize()}")
        methodBuilder.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        methodBuilder.returns(ClassName.get(packageName, "$name.Builder"))
        methodBuilder.addParameter(parameterSpec())
        return methodBuilder.build()
    }

    private fun FieldDefinition.parameterSpec(): ParameterSpec {
        val parameterBuilder = ParameterSpec.builder(type, fieldName)
        if (addNullableAnnotation()) {
            parameterBuilder.addAnnotation(Nullable::class.java)
        }
        return parameterBuilder.build()
    }

    private fun buildMethod(): MethodSpec {
        val methodBuilder = MethodSpec.methodBuilder("build")
        methodBuilder.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        methodBuilder.returns(ClassName.get(packageName, name))
        return methodBuilder.build()
    }
}
