package com.jaynewstrom.json.compiler

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.jaynewstrom.json.runtime.JsonDeserializer
import com.jaynewstrom.json.runtime.JsonDeserializerFactory
import com.jaynewstrom.json.runtime.JsonRegistrable
import com.jaynewstrom.json.runtime.internal.ListDeserializer
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import java.io.IOException
import javax.lang.model.element.Modifier

internal data class ModelDeserializerBuilder(val name: String, val fields: List<FieldDefinition>, val useAutoValue: Boolean,
        val generateAutoValueBuilder: Boolean) {
    fun build(): TypeSpec {
        val jsonDeserializerType = ClassName.get(JsonDeserializer::class.java)
        return TypeSpec.classBuilder(JsonCompiler.deserializerName(name))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(ParameterizedTypeName.get(jsonDeserializerType, JsonCompiler.jsonModelType(name)))
                .addSuperinterface(ClassName.get(JsonRegistrable::class.java))
                .addMethod(JsonCompiler.modelClassMethodSpec(name))
                .addMethod(createMethodSpec())
                .build()
    }

    private fun createMethodSpec(): MethodSpec {
        val methodBuilder = MethodSpec.methodBuilder("deserialize")
                .addException(IOException::class.java)
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PUBLIC)
                .returns(JsonCompiler.jsonModelType(name))
                .addParameter(JsonParser::class.java, JSON_PARSER_VARIABLE_NAME)
                .addParameter(JsonDeserializerFactory::class.java, DESERIALIZER_FACTORY_VARIABLE_NAME)
        createMethodBody(methodBuilder)
        return methodBuilder.build()
    }

    private fun createMethodBody(methodBuilder: MethodSpec.Builder) {
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

        if (!useAutoValue) {
            methodBuilder.addComment("Ensure required fields were parsed from json.")
            fields.forEach { field ->
                if (field.isRequired) {
                    methodBuilder.beginControlFlow("if (${field.fieldName} == null)")
                    methodBuilder.addStatement("throw new \$T(\$S)", NullPointerException::class.java, "${field.fieldName} == null")
                    methodBuilder.endControlFlow()
                }
            }
        }

        methodBuilder.addComment("Create the model given the parsed fields.")
        if (generateAutoValueBuilder) {
            val builderMethodChain = StringBuilder()
            val callArguments = mutableListOf<String>()
            fields.forEach { field ->
                builderMethodChain.append("\n.set${field.fieldName.capitalize()}(\$L)")
                callArguments.add(field.fieldName)
            }
            methodBuilder.addStatement("return new AutoValue_$name.Builder()$builderMethodChain\n.build()",
                    *callArguments.toTypedArray())
        } else {
            val constructorCallArguments = StringBuilder()
            fields.forEach { field ->
                if (!constructorCallArguments.isEmpty()) {
                    constructorCallArguments.append(", ")
                }
                constructorCallArguments.append(field.fieldName)
            }
            val type: String
            if (useAutoValue) {
                type = "AutoValue_$name"
            } else {
                type = name
            }
            methodBuilder.addStatement("return new \$N(\$L)", type, constructorCallArguments.toString())
        }
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
            val codeFormat = "$fieldName = new \$T<>(${deserializer.code}).${callCreate()}"
            methodBuilder.addStatement(codeFormat, listDeserializerType, deserializer.codeArgument)
        } else {
            val deserializer = getDeserializer(type)
            methodBuilder.addStatement("$fieldName = ${deserializer.code}.${callCreate()}", deserializer.codeArgument)
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

    private fun callCreate(): String {
        return "deserialize($JSON_PARSER_VARIABLE_NAME, $DESERIALIZER_FACTORY_VARIABLE_NAME)"
    }

    companion object {
        private const val JSON_PARSER_VARIABLE_NAME = "jp"
        private const val DESERIALIZER_FACTORY_VARIABLE_NAME = "deserializerFactory"
    }
}
