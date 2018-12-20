package com.jaynewstrom.json.compiler

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.File

data class ModelDefinition internal constructor(
    private val packageName: String,
    private val isPublic: Boolean,
    private val name: String,
    private val fields: List<FieldDefinition>,
    val createSerializer: Boolean,
    val createDeserializer: Boolean
) {
    val serializerTypeSpec: TypeSpec by lazy(LazyThreadSafetyMode.NONE) { ModelSerializerBuilder(name, fields).build() }
    val deserializerTypeSpec: TypeSpec by lazy(LazyThreadSafetyMode.NONE) { ModelDeserializerBuilder(name, fields).build() }

    fun createModels(outputDirectory: File) {
        val typeBuilder = FileSpec.builder(packageName, name)
        typeBuilder.addType(KotlinModelBuilder(isPublic, name, fields).build())
        if (createSerializer) {
            typeBuilder.addType(serializerTypeSpec)
        }
        if (createDeserializer) {
            typeBuilder.addType(deserializerTypeSpec)
        }
        typeBuilder.build().writeTo(outputDirectory)
    }
}
