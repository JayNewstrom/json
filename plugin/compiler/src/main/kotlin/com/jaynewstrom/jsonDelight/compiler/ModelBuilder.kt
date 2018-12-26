package com.jaynewstrom.jsonDelight.compiler

import com.jaynewstrom.jsonDelight.runtime.HavingJsonDeserializer
import com.jaynewstrom.jsonDelight.runtime.HavingJsonSerializer
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

internal data class ModelBuilder(
    private val modelDefinition: ModelDefinition
) {
    fun build(): TypeSpec {
        val classBuilder = TypeSpec.classBuilder(modelDefinition.name)
        classBuilder.addModifiers(KModifier.DATA)
        if (!modelDefinition.isPublic) {
            classBuilder.addModifiers(KModifier.INTERNAL)
        }
        if (modelDefinition.createSerializer) {
            classBuilder.addAnnotation(AnnotationSpec.builder(HavingJsonSerializer::class)
                .addMember("%T::class", modelDefinition.serializerTypeName)
                .build()
            )
        }
        if (modelDefinition.createDeserializer) {
            classBuilder.addAnnotation(AnnotationSpec.builder(HavingJsonDeserializer::class)
                .addMember("%T::class", modelDefinition.deserializerTypeName)
                .build()
            )
        }
        val constructor = FunSpec.constructorBuilder().addModifiers(KModifier.INTERNAL)
        modelDefinition.fields.forEach { field ->
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
