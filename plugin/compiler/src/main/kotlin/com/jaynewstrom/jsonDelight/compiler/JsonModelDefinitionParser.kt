package com.jaynewstrom.jsonDelight.compiler

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import java.io.File
import java.util.ArrayList

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
        onlyContains(modelJson, supportedTypeNames(), "type")
        return ModelDefinition(packageName, isPublic, modelName, fieldDefinitions, createSerializer, createDeserializer)
    }

    private fun parseField(fieldJson: JsonNode, modelIsPublic: Boolean): FieldDefinition {
        val isPublic = fieldJson.getBooleanOrDefault("public", modelIsPublic)
        val fieldName = fieldJson.get("name").asText()
        val jsonName = if (fieldJson.has("jsonName")) fieldJson.get("jsonName").asText() else fieldName
        val typeName = fieldJson.get("type").asText()
        val isList = fieldJson.getBooleanOrDefault("list", false)
        val primitiveType = PrimitiveType.fromIdentifier(typeName)
        var kotlinType: TypeName = primitiveType?.kotlinTypeName ?: ClassName.bestGuess(typeName)
        if (isList) {
            kotlinType = List::class.asClassName().parameterizedBy(kotlinType)
        }
        val nullable = fieldJson.getBooleanOrDefault("nullable", false)
        if (nullable) {
            kotlinType = kotlinType.copy(nullable = nullable)
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
        return FieldDefinition(isPublic, kotlinType, primitiveType, fieldName, jsonName, customSerializer, customDeserializer)
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
        return setOf("public", "fields", "createSerializer", "createDeserializer")
    }

    private fun supportedFieldNames(): Set<String> {
        return setOf("public", "name", "jsonName", "type", "list", "nullable", "customSerializer", "customDeserializer")
    }
}
