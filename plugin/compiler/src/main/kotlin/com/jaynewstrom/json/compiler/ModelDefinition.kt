package com.jaynewstrom.json.compiler

import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import com.squareup.kotlinpoet.KotlinFile
import java.io.File
import com.squareup.kotlinpoet.TypeSpec as KotlinTypeSpec

data class ModelDefinition internal constructor(
        private val packageName: String,
        private val isPublic: Boolean,
        private val name: String,
        private val fields: List<FieldDefinition>,
        val createSerializer: Boolean,
        val createDeserializer: Boolean,
        private val modelType: ModelType
) {
    fun createModels(outputDirectory: File) {
        when (modelType) {
            ModelType.AutoValueWithBuilder -> {
                outputDirectory.writeJava(AutoValueModelBuilder(isPublic, name, fields).build())
                outputDirectory.writeJava(AutoValueBuilderBuilder(packageName, isPublic, name, fields).build())
            }
            ModelType.AutoValue -> outputDirectory.writeJava(AutoValueModelBuilder(isPublic, name, fields).build())
            ModelType.BasicJava -> outputDirectory.writeJava(ImmutableModelBuilder(isPublic, name, fields).build())
            ModelType.KotlinData -> outputDirectory.writeKotlin(KotlinModelBuilder(isPublic, name, fields).build())
        }
    }

    fun deserializerTypeSpec(): TypeSpec {
        return ModelDeserializerBuilder(name, fields, modelType).build()
    }

    fun serializerTypeSpec(): TypeSpec {
        return ModelSerializerBuilder(name, fields, modelType).build()
    }

    private fun File.writeJava(typeSpec: TypeSpec) {
        JavaFile.builder(packageName, typeSpec).build().writeTo(this)
    }

    private fun File.writeKotlin(typeSpec: KotlinTypeSpec) {
        KotlinFile.get(packageName, typeSpec).writeTo(this)
    }
}
