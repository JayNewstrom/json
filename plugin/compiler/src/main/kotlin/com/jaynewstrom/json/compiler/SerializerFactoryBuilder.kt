package com.jaynewstrom.json.compiler

import com.jaynewstrom.json.compiler.JsonCompiler.Companion.QUESTION_MARK_WILDCARD_TYPE_NAME
import com.jaynewstrom.json.runtime.JsonSerializer
import com.jaynewstrom.json.runtime.JsonSerializerFactory
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import com.squareup.javapoet.TypeVariableName
import java.util.LinkedHashMap
import javax.lang.model.element.Modifier

data class SerializerFactoryBuilder(val serializers: Collection<TypeName>) {
    fun build(): TypeSpec {
        return TypeSpec.classBuilder("RealJsonSerializerFactory")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(ClassName.get(JsonSerializerFactory::class.java))
                .addField(createSerializerMapField())
                .addMethod(createConstructor())
                .addMethod(createRegisterMethod())
                .addMethod(createGetMethod())
                .build()
    }

    private fun createSerializerMapField(): FieldSpec {
        val classTypeName = ParameterizedTypeName.get(ClassName.get(Class::class.java), QUESTION_MARK_WILDCARD_TYPE_NAME)
        val rawMapTypeName = ClassName.get(Map::class.java)
        val mapTypeName = ParameterizedTypeName.get(rawMapTypeName, classTypeName, JSON_SERIALIZER_TYPE_NAME)
        return FieldSpec.builder(mapTypeName, SERIALIZER_MAP_NAME, Modifier.PRIVATE, Modifier.FINAL).build()
    }

    private fun createConstructor(): MethodSpec {
        val constructorBuilder = MethodSpec.constructorBuilder()
        constructorBuilder.addModifiers(Modifier.PUBLIC)
        val mapSize = JsonCompiler.mapSize(serializers)
        constructorBuilder.addStatement("this.$SERIALIZER_MAP_NAME = new \$T<>(\$L)", LinkedHashMap::class.java, mapSize)
        JsonCompiler.registerDefaultJsonAdapters(constructorBuilder)
        serializers.forEach {
            val codeFormat = "register(new \$T())"
            constructorBuilder.addStatement(codeFormat, it)
        }
        return constructorBuilder.build()
    }

    private fun createRegisterMethod(): MethodSpec {
        return MethodSpec.methodBuilder("register")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(JSON_SERIALIZER_TYPE_NAME, "jsonSerializer")
                .addStatement("$SERIALIZER_MAP_NAME.put(jsonSerializer.modelClass(), jsonSerializer)")
                .build()
    }

    private fun createGetMethod(): MethodSpec {
        val typeParameter = TypeVariableName.get("T")
        val jsonSerializerTypeName = ParameterizedTypeName.get(ClassName.get(JsonSerializer::class.java), typeParameter)
        return MethodSpec.methodBuilder("get")
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PUBLIC)
                .addTypeVariable(typeParameter)
                .returns(jsonSerializerTypeName)
                .addParameter(ParameterizedTypeName.get(ClassName.get(Class::class.java), typeParameter), "modelClass")
                .addComment("noinspection unchecked")
                .addStatement("return (\$T) $SERIALIZER_MAP_NAME.get(modelClass)", jsonSerializerTypeName)
                .build()
    }

    companion object {
        private const val SERIALIZER_MAP_NAME = "deserializerMap"
        private val JSON_SERIALIZER_TYPE_NAME = ParameterizedTypeName.get(ClassName.get(JsonSerializer::class.java),
                QUESTION_MARK_WILDCARD_TYPE_NAME)
    }
}