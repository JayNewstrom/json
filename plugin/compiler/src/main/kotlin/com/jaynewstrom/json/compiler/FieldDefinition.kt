package com.jaynewstrom.json.compiler

import com.squareup.javapoet.TypeName

data class FieldDefinition(var isPublic: Boolean, val isRequired: Boolean, val type: TypeName, val fieldName: String,
        val jsonName: String, val customSerializer: TypeName?, val customDeserializer: TypeName?)
