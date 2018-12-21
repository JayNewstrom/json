package com.jaynewstrom.json.compiler

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.jaynewstrom.json.runtime.JsonDeserializer
import com.jaynewstrom.json.runtime.JsonDeserializerFactory
import com.jaynewstrom.json.runtime.JsonRegistrable
import com.jaynewstrom.json.runtime.internal.ListDeserializer
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import java.io.IOException

internal data class ModelDeserializerBuilder(
    private val isPublic: Boolean,
    private val name: String,
    private val fields: List<FieldDefinition>
) {
    fun build(): TypeSpec {
        val jsonDeserializerType = JsonDeserializer::class.asTypeName()
        val typeBuilder = TypeSpec.objectBuilder(JsonCompiler.deserializerName(name))
            .addSuperinterface(jsonDeserializerType.parameterizedBy(JsonCompiler.jsonModelType(name)))
            .addSuperinterface(JsonRegistrable::class)
            .addFunction(JsonCompiler.modelClassFunSpec(name))
            .addFunction(deserializeMethodSpec())
        if (!isPublic) {
            typeBuilder.addModifiers(KModifier.INTERNAL)
        }
        return typeBuilder.build()
    }

    private fun deserializeMethodSpec(): FunSpec {
        val methodBuilder = FunSpec.builder("deserialize")
            .addAnnotation(JsonCompiler.throwsIoExceptionAnnotation())
            .addModifiers(KModifier.OVERRIDE)
            .returns(JsonCompiler.jsonModelType(name))
            .addParameter(ParameterSpec.builder(JSON_PARSER_VARIABLE_NAME, JsonParser::class).build())
            .addParameter(ParameterSpec.builder(DESERIALIZER_FACTORY_VARIABLE_NAME, JsonDeserializerFactory::class.java).build())
        deserializeMethodBody(methodBuilder)
        return methodBuilder.build()
    }

    private fun deserializeMethodBody(methodBuilder: FunSpec.Builder) {
        methodBuilder.addComment("Ensure we are in the correct state.")
        methodBuilder.beginControlFlow("if ($JSON_PARSER_VARIABLE_NAME.currentToken() != %T.%L)", JsonToken::class.java,
            JsonToken.START_OBJECT)
        methodBuilder.addStatement("throw %T(%S)", IOException::class.java, "Expected data to start with an Object")
        methodBuilder.endControlFlow()

        methodBuilder.addComment("Initialize variables.")
        fields.forEach { field ->
            if (field.primitiveType != null && !field.type.isNullable && field.primitiveType != PrimitiveType.STRING) {
                methodBuilder.addStatement("var ${field.fieldName}: %T = ${field.primitiveType.defaultValue}", field.type)
            } else if (field.type.isNullable) {
                methodBuilder.addStatement("var ${field.fieldName}: %T = null", field.type)
            } else {
                methodBuilder.addStatement("lateinit var ${field.fieldName}: %T", field.type)
            }
        }

        methodBuilder.addComment("Parse fields as they come.")
        methodBuilder.beginControlFlow("while ($JSON_PARSER_VARIABLE_NAME.nextToken() != %T.%L)", JsonToken::class.java,
            JsonToken.END_OBJECT)
        methodBuilder.addStatement("val fieldName = $JSON_PARSER_VARIABLE_NAME.getCurrentName()")
        methodBuilder.addStatement("val nextToken = $JSON_PARSER_VARIABLE_NAME.nextToken()")
        methodBuilder.beginControlFlow("if (nextToken == %T.%L)", JsonToken::class.java, JsonToken.VALUE_NULL)
        methodBuilder.addStatement("continue")
        methodBuilder.endControlFlow()
        if (fields.isNotEmpty()) {
            var addElse = false
            fields.forEach { field ->
                val fieldEqualsIfStatement = "if (fieldName == \"${field.jsonName}\")"
                if (addElse) {
                    methodBuilder.nextControlFlow("else $fieldEqualsIfStatement")
                } else {
                    addElse = true
                    methodBuilder.beginControlFlow(fieldEqualsIfStatement)
                }
                field.assignVariable(methodBuilder)
            }
            methodBuilder.nextControlFlow("else")
            methodBuilder.addStatement("$JSON_PARSER_VARIABLE_NAME.skipChildren()")
            methodBuilder.endControlFlow() // End if / else if.
        }
        methodBuilder.endControlFlow() // End while loop.

        methodBuilder.addComment("Create the model given the parsed fields.")
        createModel(methodBuilder)
    }

    private fun FieldDefinition.assignVariable(methodBuilder: FunSpec.Builder) {
        if (type is ParameterizedTypeName && type.rawType == List::class.asTypeName()) {
            val modelType = type.typeArguments[0]
            val listDeserializerType = ListDeserializer::class.java.asTypeName()
            val deserializer = getDeserializer(modelType)
            val codeFormat = "$fieldName = %T(${deserializer.code}).${callDeserialize()}"
            methodBuilder.addStatement(codeFormat, listDeserializerType, deserializer.codeArgument)
        } else if (customDeserializer == null && primitiveType != null) {
            methodBuilder.addStatement("$fieldName = $JSON_PARSER_VARIABLE_NAME.${primitiveType.parseMethod}()")
        } else {
            val deserializer = getDeserializer(type)
            methodBuilder.addStatement("$fieldName = ${deserializer.code}.${callDeserialize()}", deserializer.codeArgument)
        }
    }

    private fun createModel(methodBuilder: FunSpec.Builder) {
        val constructorCallArguments = StringBuilder()
        fields.forEach { field ->
            if (!constructorCallArguments.isEmpty()) {
                constructorCallArguments.append(", ")
            }
            constructorCallArguments.append(field.fieldName)
        }
        methodBuilder.addStatement("return $name(%L)", constructorCallArguments.toString())
    }

    private data class FieldDeserializerResult(val code: String, val codeArgument: TypeName)

    private fun FieldDefinition.getDeserializer(typeName: TypeName): FieldDeserializerResult {
        return if (customDeserializer == null) {
            FieldDeserializerResult("$DESERIALIZER_FACTORY_VARIABLE_NAME[%T::class.java]", typeName.copy(nullable = false))
        } else {
            FieldDeserializerResult("%T", customDeserializer)
        }
    }

    private fun callDeserialize(): String {
        return "deserialize($JSON_PARSER_VARIABLE_NAME, $DESERIALIZER_FACTORY_VARIABLE_NAME)"
    }

    companion object {
        private const val JSON_PARSER_VARIABLE_NAME = "jp"
        private const val DESERIALIZER_FACTORY_VARIABLE_NAME = "deserializerFactory"
    }
}
