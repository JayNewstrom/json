package com.jaynewstrom.json.compiler

import com.squareup.javapoet.TypeSpec

data class ModelDefinition(
        private val packageName: String,
        private val isPublic: Boolean,
        private val name: String,
        private val fields: List<FieldDefinition>,
        val createSerializer: Boolean,
        val createDeserializer: Boolean,
        private val useAutoValue: Boolean,
        private val generateAutoValueBuilder: Boolean
) {
    fun modelTypeSpecs(): Collection<TypeSpec> {
        if (useAutoValue) {
            val types = mutableListOf(AutoValueModelBuilder(isPublic, name, fields).build())
            if (generateAutoValueBuilder) {
                types.add(AutoValueBuilderBuilder(packageName, isPublic, name, fields).build())
            }
            return types
        } else {
            return listOf(ImmutableModelBuilder(isPublic, name, fields).build())
        }
    }

    fun deserializerTypeSpec(): TypeSpec {
        return ModelDeserializerBuilder(name, fields, useAutoValue, generateAutoValueBuilder).build()
    }

    fun serializerTypeSpec(): TypeSpec {
        return ModelSerializerBuilder(name, fields, useAutoValue).build()
    }
}
