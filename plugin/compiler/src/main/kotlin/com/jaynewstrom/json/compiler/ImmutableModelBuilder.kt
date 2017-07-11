package com.jaynewstrom.json.compiler

import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier

internal data class ImmutableModelBuilder(
        private val isPublic: Boolean,
        private val name: String,
        private val fields: List<FieldDefinition>
) {
    fun build(): TypeSpec {
        val classBuilder = TypeSpec.classBuilder(name)
        classBuilder.addModifiers(Modifier.FINAL)
        if (isPublic) {
            classBuilder.addModifiers(Modifier.PUBLIC)
        }
        fields.forEach { field ->
            classBuilder.addField(field.fieldSpec())
        }
        classBuilder.addMethod(modelConstructor())
        return classBuilder.build()
    }

    private fun modelConstructor(): MethodSpec {
        val constructorBuilder = MethodSpec.constructorBuilder()
        fields.forEach { field ->
            constructorBuilder.addParameter(field.parameterSpec())
            constructorBuilder.addStatement(field.constructorCode())
        }
        return constructorBuilder.build()
    }

    private fun FieldDefinition.fieldSpec(): FieldSpec {
        val fieldBuilder = FieldSpec.builder(type, fieldName, Modifier.FINAL)
        if (isPublic) {
            fieldBuilder.addModifiers(Modifier.PUBLIC)
        }
        return fieldBuilder.build()
    }

    private fun FieldDefinition.parameterSpec(): ParameterSpec {
        return ParameterSpec.builder(type, fieldName).build()
    }

    private fun FieldDefinition.constructorCode(): String {
        return "this.$fieldName = $fieldName"
    }
}
