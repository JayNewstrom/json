package com.jaynewstrom.json.compiler

import com.fasterxml.jackson.core.JsonGenerator
import com.jaynewstrom.json.runtime.JsonRegistrable
import com.jaynewstrom.json.runtime.JsonSerializer
import com.jaynewstrom.json.runtime.JsonSerializerFactory
import com.jaynewstrom.json.runtime.internal.ListSerializer
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import java.io.IOException
import javax.lang.model.element.Modifier

internal data class ModelSerializerBuilder(
        private val name: String,
        private val fields: List<FieldDefinition>,
        private val modelType: ModelType
) {
    fun build(): TypeSpec {
        val jsonFactoryType = ClassName.get(JsonSerializer::class.java)
        return TypeSpec.classBuilder(JsonCompiler.serializerName(name))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(ParameterizedTypeName.get(jsonFactoryType, JsonCompiler.jsonModelType(name)))
                .addSuperinterface(ClassName.get(JsonRegistrable::class.java))
                .addMethod(JsonCompiler.modelClassMethodSpec(name))
                .addMethod(serializeMethodSpec())
                .build()
    }

    private fun serializeMethodSpec(): MethodSpec {
        val methodBuilder = MethodSpec.methodBuilder("serialize")
                .addException(IOException::class.java)
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(JsonCompiler.jsonModelType(name), MODEL_VARIABLE_NAME)
                .addParameter(JsonGenerator::class.java, JSON_GENERATOR_VARIABLE_NAME)
                .addParameter(JsonSerializerFactory::class.java, SERIALIZER_FACTORY_VARIABLE_NAME)
        serializeMethodBody(methodBuilder)
        return methodBuilder.build()
    }

    private fun serializeMethodBody(methodBuilder: MethodSpec.Builder) {
        methodBuilder.addStatement("$JSON_GENERATOR_VARIABLE_NAME.writeStartObject()")
        fields.forEach { field ->
            methodBuilder.addStatement("$JSON_GENERATOR_VARIABLE_NAME.writeFieldName(\$S)", field.jsonName)
            field.serialize(methodBuilder)
        }
        methodBuilder.addStatement("$JSON_GENERATOR_VARIABLE_NAME.writeEndObject()")
    }

    private fun FieldDefinition.serialize(methodBuilder: MethodSpec.Builder) {
        val primitiveType = PrimitiveType.fromTypeNameOrBoxedTypeName(type)
        if (customSerializer == null && primitiveType != null) {
            methodBuilder.addStatement("$JSON_GENERATOR_VARIABLE_NAME.${primitiveType.serializeMethod}(model.${modelValue()})")
        } else if (type is ParameterizedTypeName && type.rawType == ClassName.get("java.util", "List")) {
            val modelType = type.typeArguments[0]
            val listSerializerType = ClassName.get(ListSerializer::class.java)
            val serializer = getSerializer(modelType)
            val codeFormat = "new \$T<>(${serializer.code}).${callSerialize()}"
            methodBuilder.addStatement(codeFormat, listSerializerType, serializer.codeArgument)
        } else {
            val serializer = getSerializer(type)
            methodBuilder.addStatement("${serializer.code}.${callSerialize()}", serializer.codeArgument)
        }
    }

    private data class FieldSerializerResult(val code: String, val codeArgument: TypeName)

    private fun FieldDefinition.getSerializer(typeName: TypeName): FieldSerializerResult {
        if (customSerializer == null) {
            return FieldSerializerResult("$SERIALIZER_FACTORY_VARIABLE_NAME.get(\$T.class)", typeName)
        } else {
            return FieldSerializerResult("new \$T()", customSerializer)
        }
    }

    private fun FieldDefinition.callSerialize(): String {
        return "serialize(model.${modelValue()}, $JSON_GENERATOR_VARIABLE_NAME, $SERIALIZER_FACTORY_VARIABLE_NAME)"
    }

    private fun FieldDefinition.modelValue() = modelType.valueCode(fieldName)

    companion object {
        private const val MODEL_VARIABLE_NAME = "model"
        private const val JSON_GENERATOR_VARIABLE_NAME = "jg"
        private const val SERIALIZER_FACTORY_VARIABLE_NAME = "serializerFactory"
    }
}
