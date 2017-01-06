package com.jaynewstrom.json.compiler

import com.squareup.javapoet.TypeSpec

data class ModelDefinition(val isPublic: Boolean, val name: String, val fields: List<FieldDefinition>, val createSerializer: Boolean,
        val createDeserializer: Boolean, val useAutoValue: Boolean, val addToCompositeFactory: Boolean) {
    fun modelTypeSpec(): TypeSpec {
        if (useAutoValue) {
            return AutoValueModelBuilder(isPublic, name, fields).build()
        } else {
            return ImmutableModelBuilder(isPublic, name, fields).build()
        }
    }

    fun deserializerTypeSpec(): TypeSpec {
        return ModelDeserializerBuilder(name, fields, useAutoValue, addToCompositeFactory).build()
    }

    fun serializerTypeSpec(): TypeSpec {
        return ModelSerializerBuilder(name, fields, useAutoValue, addToCompositeFactory).build()
    }
}
