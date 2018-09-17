package com.jaynewstrom.json.compiler

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.jaynewstrom.json.runtime.JsonDeserializer
import com.jaynewstrom.json.runtime.JsonDeserializerFactory
import com.jaynewstrom.json.runtime.JsonRegistrable
import com.jaynewstrom.json.runtime.internal.ListDeserializer
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import java.io.IOException
import javax.lang.model.element.Modifier

internal data class ModelDeserializerBuilder(
    private val name: String,
    private val fields: List<FieldDefinition>,
    private val modelType: ModelType
) {
    fun build(): TypeSpec {
        val jsonDeserializerType = ClassName.get(JsonDeserializer::class.java)
        return TypeSpec.classBuilder(JsonCompiler.deserializerName(name))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(ParameterizedTypeName.get(jsonDeserializerType, JsonCompiler.jsonModelType(name)))
                .addSuperinterface(ClassName.get(JsonRegistrable::class.java))
                .addField(singletonInstanceField())
                .addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build())
                .addMethod(JsonCompiler.modelClassMethodSpec(name))
                .addMethod(deserializeMethodSpec())
                .build()
    }

    private fun singletonInstanceField(): FieldSpec {
        return FieldSpec.builder(ClassName.bestGuess(JsonCompiler.deserializerName(name)), "INSTANCE")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("new \$L()", JsonCompiler.deserializerName(name))
                .build()
    }

    private fun deserializeMethodSpec(): MethodSpec {
        val methodBuilder = MethodSpec.methodBuilder("deserialize")
                .addException(IOException::class.java)
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PUBLIC)
                .returns(JsonCompiler.jsonModelType(name))
                .addParameter(JsonParser::class.java, JSON_PARSER_VARIABLE_NAME)
                .addParameter(JsonDeserializerFactory::class.java, DESERIALIZER_FACTORY_VARIABLE_NAME)
        deserializeMethodBody(methodBuilder)
        return methodBuilder.build()
    }

    private fun deserializeMethodBody(methodBuilder: MethodSpec.Builder) {
        methodBuilder.addComment("Ensure we are in the correct state.")
        methodBuilder.beginControlFlow("if ($JSON_PARSER_VARIABLE_NAME.currentToken() != \$T.\$L)", JsonToken::class.java,
                JsonToken.START_OBJECT)
        methodBuilder.addStatement("throw new \$T(\$S)", IOException::class.java, "Expected data to start with an Object")
        methodBuilder.endControlFlow()

        methodBuilder.addComment("Initialize variables.")
        fields.forEach { field ->
            methodBuilder.addStatement("\$T ${field.fieldName} = ${field.defaultValue()}", field.type)
        }

        methodBuilder.addComment("Parse fields as they come.")
        methodBuilder.beginControlFlow("while ($JSON_PARSER_VARIABLE_NAME.nextToken() != \$T.\$L)", JsonToken::class.java,
                JsonToken.END_OBJECT)
        methodBuilder.addStatement("String fieldName = $JSON_PARSER_VARIABLE_NAME.getCurrentName()")
        methodBuilder.addStatement("\$T nextToken = $JSON_PARSER_VARIABLE_NAME.nextToken()", JsonToken::class.java)
        methodBuilder.beginControlFlow("if (nextToken == \$T.\$L)", JsonToken::class.java,
                JsonToken.VALUE_NULL)
        methodBuilder.addStatement("continue")
        methodBuilder.endControlFlow()
        if (fields.isNotEmpty()) {
            var addElse = false
            fields.forEach { field ->
                val fieldEqualsIfStatement = "if (fieldName.equals(\"${field.jsonName}\"))"
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

        modelType.ensureFieldsWereDeserialized(methodBuilder, fields)
        methodBuilder.addComment("Create the model given the parsed fields.")
        modelType.createModel(name, methodBuilder, fields)
    }

    private fun FieldDefinition.defaultValue(): String {
        if (type.isPrimitive) {
            return PrimitiveType.fromTypeName(type).defaultValue
        } else {
            return "null"
        }
    }

    private fun FieldDefinition.assignVariable(methodBuilder: MethodSpec.Builder) {
        val primitiveType = PrimitiveType.fromTypeNameOrBoxedTypeName(type)
        if (customDeserializer == null && primitiveType != null) {
            methodBuilder.addStatement("$fieldName = $JSON_PARSER_VARIABLE_NAME.${primitiveType.parseMethod}()")
        } else if (type is ParameterizedTypeName && type.rawType == ClassName.get("java.util", "List")) {
            val modelType = type.typeArguments[0]
            val listDeserializerType = ClassName.get(ListDeserializer::class.java)
            val deserializer = getDeserializer(modelType)
            val codeFormat = "$fieldName = new \$T<>(${deserializer.code}).${callDeserialize()}"
            methodBuilder.addStatement(codeFormat, listDeserializerType, deserializer.codeArgument)
        } else {
            val deserializer = getDeserializer(type)
            methodBuilder.addStatement("$fieldName = ${deserializer.code}.${callDeserialize()}", deserializer.codeArgument)
        }
    }

    private data class FieldDeserializerResult(val code: String, val codeArgument: TypeName)

    private fun FieldDefinition.getDeserializer(typeName: TypeName): FieldDeserializerResult {
        if (customDeserializer == null) {
            return FieldDeserializerResult("$DESERIALIZER_FACTORY_VARIABLE_NAME.get(\$T.class)", typeName)
        } else {
            return FieldDeserializerResult("new \$T()", customDeserializer)
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
