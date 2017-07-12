package com.jaynewstrom.json.compiler

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

internal data class KotlinModelBuilder(
        private val isPublic: Boolean,
        private val name: String,
        private val fields: List<FieldDefinition>
) {
    fun build(): TypeSpec {
        val classBuilder = TypeSpec.classBuilder(name)
        classBuilder.addModifiers(KModifier.DATA)
        if (!isPublic) {
            classBuilder.addModifiers(KModifier.INTERNAL)
        }
        val constructor = FunSpec.constructorBuilder().addModifiers(KModifier.INTERNAL)
        fields.forEach { field ->
            classBuilder.addProperty(PropertySpec.builder(field.fieldName, field.kotlinType()).initializer(field.fieldName).build())
            constructor.addParameter(field.fieldName, field.kotlinType())
        }
        classBuilder.primaryConstructor(constructor.build())
        return classBuilder.build()
    }
}
