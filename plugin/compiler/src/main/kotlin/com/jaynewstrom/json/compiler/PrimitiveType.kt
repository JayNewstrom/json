package com.jaynewstrom.json.compiler

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import com.squareup.kotlinpoet.ClassName as KotlinClassName
import com.squareup.kotlinpoet.TypeName as KotlinTypeName

internal enum class PrimitiveType(
        private val typeName: TypeName,
        private val kotlinTypeName: KotlinTypeName,
        private val identifier: String,
        val defaultValue: String,
        val parseMethod: String,
        val serializeMethod: String
) {
    BOOLEAN(TypeName.BOOLEAN, com.squareup.kotlinpoet.BOOLEAN, "boolean", "false", "getBooleanValue", "writeBoolean"),
    BYTE(TypeName.BYTE, com.squareup.kotlinpoet.BYTE, "byte", "0", "getByteValue", "writeNumber"),
    SHORT(TypeName.SHORT, com.squareup.kotlinpoet.SHORT, "short", "0", "getShortValue", "writeNumber"),
    INT(TypeName.INT, com.squareup.kotlinpoet.INT, "int", "0", "getIntValue", "writeNumber"),
    LONG(TypeName.LONG, com.squareup.kotlinpoet.LONG, "long", "0", "getLongValue", "writeNumber"),
    FLOAT(TypeName.FLOAT, com.squareup.kotlinpoet.FLOAT, "float", "0.0f", "getFloatValue", "writeNumber"),
    DOUBLE(TypeName.DOUBLE, com.squareup.kotlinpoet.DOUBLE, "double", "0.0d", "getDoubleValue", "writeNumber"),
    STRING(ClassName.get("java.lang", "String"), KotlinClassName("kotlin", "String"), "String", "null", "getText", "writeString"),
    ;

    companion object {
        fun fromTypeName(typeName: TypeName): PrimitiveType {
            values().forEach {
                if (typeName == it.typeName) {
                    return it
                }
            }
            throw UnsupportedOperationException("$typeName is not a supported primitive.")
        }

        fun fromTypeNameOrBoxedTypeName(typeName: TypeName): PrimitiveType? {
            values().forEach {
                if (typeName == it.typeName || typeName == it.typeName.box()) {
                    return it
                }
            }
            return null
        }

        fun typeNameFromIdentifier(identifier: String): TypeName? {
            values().forEach {
                if (identifier == it.identifier) {
                    return it.typeName
                }
                val boxedTypeName = it.typeName.box()
                if (boxedTypeName is ClassName) {
                    if (boxedTypeName.simpleName() == identifier) {
                        return boxedTypeName
                    }
                }
            }
            return null
        }

        fun kotlinTypeNameFromIdentifier(identifier: String): KotlinTypeName? {
            values().forEach {
                if (identifier == it.identifier) {
                    return it.kotlinTypeName
                }
                val boxedTypeName = it.typeName.box()
                if (boxedTypeName is ClassName) {
                    if (boxedTypeName.simpleName() == identifier) {
                        return it.kotlinTypeName
                    }
                }
            }
            return null
        }
    }
}
