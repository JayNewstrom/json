package com.jaynewstrom.jsonDelight.compiler

import com.squareup.kotlinpoet.TypeName

internal data class FieldDefinition(
    val isPublic: Boolean,
    val type: TypeName,
    val primitiveType: PrimitiveType?,
    val fieldName: String,
    val jsonName: String,
    val customSerializer: TypeName?,
    val customDeserializer: TypeName?
)
