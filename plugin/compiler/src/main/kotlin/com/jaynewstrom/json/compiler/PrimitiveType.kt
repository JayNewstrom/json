package com.jaynewstrom.json.compiler

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName

internal enum class PrimitiveType(val typeName: TypeName, val identifier: String, val defaultValue: String, val parseMethod: String,
        val serializeMethod: String) {
    BOOLEAN(TypeName.BOOLEAN, "boolean", "false", "getBooleanValue", "writeBoolean"),
    BYTE(TypeName.BYTE, "byte", "0", "getByteValue", "writeNumber"),
    SHORT(TypeName.SHORT, "short", "0", "getShortValue", "writeNumber"),
    INT(TypeName.INT, "int", "0", "getIntValue", "writeNumber"),
    LONG(TypeName.LONG, "long", "0", "getLongValue", "writeNumber"),
    FLOAT(TypeName.FLOAT, "float", "0.0f", "getFloatValue", "writeNumber"),
    DOUBLE(TypeName.DOUBLE, "double", "0.0d", "getDoubleValue", "writeNumber"),
    STRING(ClassName.get("java.lang", "String"), "String", "null", "getText", "writeString"),
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
    }
}
