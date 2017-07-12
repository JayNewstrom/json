package com.jaynewstrom.json.compiler

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import java.io.File
import java.util.ArrayList
import com.squareup.kotlinpoet.ClassName as KotlinClassName
import com.squareup.kotlinpoet.ParameterizedTypeName as KotlinParameterizedTypeName

data class JsonModelDefinitionParser(
        private val file: File,
        private val createSerializerByDefault: Boolean,
        private val createDeserializerByDefault: Boolean,
        private val packageName: String
) {
    fun parse(): ModelDefinition {
        val objectMapper = ObjectMapper()
        val modelJson = objectMapper.readTree(file)
        val modelName = JsonCompiler.nameFromFile(file)
        val isPublic = modelJson.getBooleanOrDefault("public", false)
        val fieldDefinitions = ArrayList<FieldDefinition>()
        modelJson.get("fields").forEach {
            fieldDefinitions.add(parseField(it, isPublic))
        }
        val createSerializer = modelJson.getBooleanOrDefault("createSerializer", createSerializerByDefault)
        val createDeserializer = modelJson.getBooleanOrDefault("createDeserializer", createDeserializerByDefault)
        val modelType = ModelType.fromDescriptor(modelJson.get("modelType")?.asText() ?: "basic")
        onlyContains(modelJson, supportedTypeNames(), "type")
        return ModelDefinition(packageName, isPublic, modelName, fieldDefinitions, createSerializer, createDeserializer, modelType)
    }

    private fun parseField(fieldJson: JsonNode, modelIsPublic: Boolean): FieldDefinition {
        val isPublic = fieldJson.getBooleanOrDefault("public", modelIsPublic)
        val fieldName = fieldJson.get("name").asText()
        val jsonName = if (fieldJson.has("jsonName")) fieldJson.get("jsonName").asText() else fieldName
        val typeName = fieldJson.get("type").asText()
        var type = PrimitiveType.typeNameFromIdentifier(typeName) ?: ClassName.bestGuess(typeName)
        val isList = fieldJson.getBooleanOrDefault("list", false)
        if (isList) {
            type = ParameterizedTypeName.get(ClassName.get("java.util", "List"), type)
        }
        var kotlinType = PrimitiveType.kotlinTypeNameFromIdentifier(typeName) ?: KotlinClassName.bestGuess(typeName)
        if (isList) {
            kotlinType = KotlinParameterizedTypeName.get(KotlinClassName("kotlin.collections", "List"), kotlinType)
        }
        val isRequired = fieldJson.getBooleanOrDefault("required", !type.isPrimitive)
        if (isRequired && type.isPrimitive) {
            throw IllegalStateException("Primitives can't be required.")
        }
        var customSerializer: TypeName? = null
        if (fieldJson.hasNonNull("customSerializer")) {
            customSerializer = ClassName.bestGuess(fieldJson.get("customSerializer").asText())
        }
        var customDeserializer: TypeName? = null
        if (fieldJson.hasNonNull("customDeserializer")) {
            customDeserializer = ClassName.bestGuess(fieldJson.get("customDeserializer").asText())
        }
        onlyContains(fieldJson, supportedFieldNames(), "field")
        return FieldDefinition(isPublic, isRequired, type, kotlinType, fieldName, jsonName, customSerializer, customDeserializer)
    }

    private fun JsonNode.getBooleanOrDefault(key: String, defaultValue: Boolean): Boolean {
        return if (has(key)) get(key).asBoolean() else defaultValue
    }

    private fun onlyContains(json: JsonNode, keys: Set<String>, type: String) {
        (json as ObjectNode).fieldNames().forEach { fieldName ->
            if (!keys.contains(fieldName)) {
                throw IllegalArgumentException("$fieldName is not a supported $type property in ${file.path}.")
            }
        }
    }

    private fun supportedTypeNames(): Set<String> {
        return setOf("public", "fields", "createSerializer", "createDeserializer", "modelType")
    }

    private fun supportedFieldNames(): Set<String> {
        return setOf("public", "name", "jsonName", "type", "list", "required", "customSerializer", "customDeserializer")
    }
}
