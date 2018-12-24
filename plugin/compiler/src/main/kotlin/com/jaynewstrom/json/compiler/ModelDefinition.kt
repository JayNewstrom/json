package com.jaynewstrom.json.compiler

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeName
import java.io.File

data class ModelDefinition internal constructor(
    internal val packageName: String,
    internal val isPublic: Boolean,
    internal val name: String,
    internal val fields: List<FieldDefinition>,
    val createSerializer: Boolean,
    val createDeserializer: Boolean
) {
    val serializerTypeName: TypeName by lazy { ClassName(packageName, JsonCompiler.serializerName(name)) }
    val deserializerTypeName: TypeName by lazy { ClassName(packageName, JsonCompiler.deserializerName(name)) }

    fun createModels(outputDirectory: File) {
        val typeBuilder = FileSpec.builder(packageName, name)
        typeBuilder.addType(ModelBuilder(this).build())
        if (createSerializer) {
            typeBuilder.addType(ModelSerializerBuilder(isPublic, name, fields).build())
        }
        if (createDeserializer) {
            typeBuilder.addType(ModelDeserializerBuilder(isPublic, name, fields).build())
        }
        typeBuilder.build().writeTo(outputDirectory)
    }
}
