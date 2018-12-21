package com.jaynewstrom.json.compiler

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

internal data class ModelBuilder(
    private val isPublic: Boolean,
    private val name: String,
    private val fields: List<FieldDefinition>
) {
    fun build(): TypeSpec {
        val classBuilder = TypeSpec.classBuilder(name)
        classBuilder.addModifiers(KModifier.DATA)
        if (!isPublic) {
            // TODO: revert once we switch to using reflection.
//            classBuilder.addModifiers(KModifier.INTERNAL)
        }
        val constructor = FunSpec.constructorBuilder().addModifiers(KModifier.INTERNAL)
        fields.forEach { field ->
            classBuilder.addProperty(
                PropertySpec.builder(field.fieldName, field.type)
                    .initializer(field.fieldName)
                    .addAnnotation(AnnotationSpec.builder(JvmField::class).build())
                    .build()
            )
            constructor.addParameter(field.fieldName, field.type)
        }
        classBuilder.primaryConstructor(constructor.build())
        return classBuilder.build()
    }
}
