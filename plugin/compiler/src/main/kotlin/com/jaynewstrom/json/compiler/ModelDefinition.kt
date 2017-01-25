package com.jaynewstrom.json.compiler

import com.squareup.javapoet.TypeSpec

data class ModelDefinition(val packageName: String, val isPublic: Boolean, val name: String, val fields: List<FieldDefinition>, val createSerializer: Boolean,
        val createDeserializer: Boolean, val useAutoValue: Boolean, val generateAutoValueBuilder: Boolean) {
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
