package com.jaynewstrom.json.compiler

import com.squareup.javapoet.TypeName
import com.squareup.kotlinpoet.TypeName as KotlinTypeName

data class FieldDefinition(
        var isPublic: Boolean,
        val isRequired: Boolean,
        val type: TypeName,
        private val kotlinType: KotlinTypeName,
        val fieldName: String,
        val jsonName: String,
        val customSerializer: TypeName?,
        val customDeserializer: TypeName?
) {
    internal fun isNullable() = !isRequired && !type.isPrimitive

    internal fun kotlinType() = if (isNullable()) {
        kotlinType.asNullable()
    } else {
        kotlinType.asNonNullable()
    }
}
