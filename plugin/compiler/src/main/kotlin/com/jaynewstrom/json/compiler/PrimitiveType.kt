package com.jaynewstrom.json.compiler

import com.squareup.kotlinpoet.ClassName

internal enum class PrimitiveType(
    val kotlinTypeName: ClassName,
    val defaultValue: String,
    val parseMethod: String,
    val serializeMethod: String
) {
    BOOLEAN(com.squareup.kotlinpoet.BOOLEAN, "false", "getBooleanValue", "writeBoolean"),
    BYTE(com.squareup.kotlinpoet.BYTE, "0", "getByteValue", "writeNumber"),
    SHORT(com.squareup.kotlinpoet.SHORT, "0", "getShortValue", "writeNumber"),
    INT(com.squareup.kotlinpoet.INT, "0", "getIntValue", "writeNumber"),
    LONG(com.squareup.kotlinpoet.LONG, "0", "getLongValue", "writeNumber"),
    FLOAT(com.squareup.kotlinpoet.FLOAT, "0.0f", "getFloatValue", "writeNumber"),
    DOUBLE(com.squareup.kotlinpoet.DOUBLE, "0.0d", "getDoubleValue", "writeNumber"),
    STRING(ClassName("kotlin", "String"), "null", "getText", "writeString"),
    ;

    companion object {
        fun fromIdentifier(identifier: String): PrimitiveType? = values().firstOrNull { identifier == it.kotlinTypeName.simpleName }
    }
}
