package com.jaynewstrom.json.compiler

import com.squareup.javapoet.MethodSpec

internal sealed class ModelType {
    object BasicJava : ModelType() {
        override fun valueCode(fieldName: String): String {
            return fieldName
        }

        override fun ensureFieldsWereDeserialized(methodBuilder: MethodSpec.Builder, fields: List<FieldDefinition>) {
            methodBuilder.addComment("Ensure required fields were parsed from json.")
            fields.forEach { field ->
                if (!field.nullable && !field.type.isPrimitive) {
                    methodBuilder.beginControlFlow("if (${field.fieldName} == null)")
                    methodBuilder.addStatement("throw new \$T(\$S)", NullPointerException::class.java, "${field.fieldName} == null")
                    methodBuilder.endControlFlow()
                }
            }
        }

        override fun createModel(modelName: String, methodBuilder: MethodSpec.Builder, fields: List<FieldDefinition>) {
            defaultModelCreator(modelName, methodBuilder, fields)
        }
    }

    object KotlinData : ModelType() {
        override fun createModel(modelName: String, methodBuilder: MethodSpec.Builder, fields: List<FieldDefinition>) {
            defaultModelCreator(modelName, methodBuilder, fields)
        }
    }

    open fun valueCode(fieldName: String) = "get${fieldName.capitalize()}()"

    open fun ensureFieldsWereDeserialized(methodBuilder: MethodSpec.Builder, fields: List<FieldDefinition>) {
        // ModelTypes that do not override this are assumed to have null validation built into their constructors.
    }

    abstract fun createModel(modelName: String, methodBuilder: MethodSpec.Builder, fields: List<FieldDefinition>)

    fun defaultModelCreator(modelName: String, methodBuilder: MethodSpec.Builder, fields: List<FieldDefinition>) {
        val constructorCallArguments = StringBuilder()
        fields.forEach { field ->
            if (!constructorCallArguments.isEmpty()) {
                constructorCallArguments.append(", ")
            }
            constructorCallArguments.append(field.fieldName)
        }
        methodBuilder.addStatement("return new $modelName(\$L)", constructorCallArguments.toString())
    }

    companion object {
        fun fromDescriptor(modelTypeDescriptor: String) = when (modelTypeDescriptor) {
            "basic" -> ModelType.BasicJava
            "kotlinData" -> ModelType.KotlinData
            else -> throw IllegalArgumentException("modelTypeDescriptor not supported: $modelTypeDescriptor")
        }
    }
}
