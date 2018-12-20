package com.jaynewstrom.json.compiler

import com.fasterxml.jackson.core.JsonGenerator
import com.jaynewstrom.json.runtime.JsonRegistrable
import com.jaynewstrom.json.runtime.JsonSerializer
import com.jaynewstrom.json.runtime.JsonSerializerFactory
import com.jaynewstrom.json.runtime.internal.ListSerializer
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName

internal data class ModelSerializerBuilder(
    private val name: String,
    private val fields: List<FieldDefinition>
) {
    fun build(): TypeSpec {
        val jsonFactoryType = JsonSerializer::class.asTypeName()
        return TypeSpec.objectBuilder(JsonCompiler.serializerName(name))
            .addSuperinterface(jsonFactoryType.parameterizedBy(JsonCompiler.jsonModelType(name)))
            .addSuperinterface(JsonRegistrable::class.java)
            .addFunction(JsonCompiler.modelClassFunSpec(name))
            .addFunction(serializeFunSpec())
            .build()
    }

    private fun serializeFunSpec(): FunSpec {
        val methodBuilder = FunSpec.builder("serialize")
            .addAnnotation(JsonCompiler.throwsIoExceptionAnnotation())
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(ParameterSpec.builder(MODEL_VARIABLE_NAME, JsonCompiler.jsonModelType(name)).build())
            .addParameter(ParameterSpec.builder(JSON_GENERATOR_VARIABLE_NAME, JsonGenerator::class).build())
            .addParameter(ParameterSpec.builder(SERIALIZER_FACTORY_VARIABLE_NAME, JsonSerializerFactory::class).build())
        serializeMethodBody(methodBuilder)
        return methodBuilder.build()
    }

    private fun serializeMethodBody(methodBuilder: FunSpec.Builder) {
        methodBuilder.addStatement("$JSON_GENERATOR_VARIABLE_NAME.writeStartObject()")
        fields.forEach { field ->
            val nullable = field.type.isNullable
            if (nullable) {
                methodBuilder.beginControlFlow("if (%L != null)", "$MODEL_VARIABLE_NAME.${field.fieldName}")
            }
            methodBuilder.addStatement("$JSON_GENERATOR_VARIABLE_NAME.writeFieldName(%S)", field.jsonName)
            field.serialize(methodBuilder)
            if (nullable) {
                methodBuilder.nextControlFlow("else")
                methodBuilder.addStatement("$JSON_GENERATOR_VARIABLE_NAME.writeNullField(%S)", field.jsonName)
                methodBuilder.endControlFlow()
            }
        }
        methodBuilder.addStatement("$JSON_GENERATOR_VARIABLE_NAME.writeEndObject()")
    }

    private fun FieldDefinition.serialize(methodBuilder: FunSpec.Builder) {
        if (customSerializer == null && primitiveType != null) {
            methodBuilder.addStatement("$JSON_GENERATOR_VARIABLE_NAME.${primitiveType.serializeMethod}($MODEL_VARIABLE_NAME.$fieldName${primitiveType.conversionForSerializeMethod})")
        } else if (type is ParameterizedTypeName && type.rawType == List::class.asTypeName()) {
            val modelType = type.typeArguments[0]
            val listSerializerType = ListSerializer::class.asTypeName()
            val serializer = getSerializer(modelType)
            val codeFormat = "new %T<>(${serializer.code}).${callSerialize()}"
            methodBuilder.addStatement(codeFormat, listSerializerType, serializer.codeArgument)
        } else {
            val serializer = getSerializer(type)
            methodBuilder.addStatement("${serializer.code}.${callSerialize()}", serializer.codeArgument)
        }
    }

    private data class FieldSerializerResult(val code: String, val codeArgument: TypeName)

    private fun FieldDefinition.getSerializer(typeName: TypeName): FieldSerializerResult {
        return if (customSerializer == null) {
            FieldSerializerResult("$SERIALIZER_FACTORY_VARIABLE_NAME.get(%T::class.java)", typeName)
        } else {
            FieldSerializerResult("%T", customSerializer)
        }
    }

    private fun FieldDefinition.callSerialize(): String {
        return "serialize($MODEL_VARIABLE_NAME.$fieldName, $JSON_GENERATOR_VARIABLE_NAME, $SERIALIZER_FACTORY_VARIABLE_NAME)"
    }

    companion object {
        private const val MODEL_VARIABLE_NAME = "value"
        private const val JSON_GENERATOR_VARIABLE_NAME = "jg"
        private const val SERIALIZER_FACTORY_VARIABLE_NAME = "serializerFactory"
    }
}
