package com.jaynewstrom.json.compiler

import com.jaynewstrom.json.compiler.JsonCompiler.Companion.QUESTION_MARK_WILDCARD_TYPE_NAME
import com.jaynewstrom.json.runtime.JsonDeserializer
import com.jaynewstrom.json.runtime.JsonDeserializerFactory
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import com.squareup.javapoet.TypeVariableName
import java.util.LinkedHashMap
import javax.lang.model.element.Modifier

data class DeserializerFactoryBuilder(val deserializers: Collection<TypeName>) {
    companion object {
        private const val DESERIALIZER_MAP_NAME = "deserializerMap"
        private val JSON_DESERIALIZER_TYPE_NAME = ParameterizedTypeName.get(ClassName.get(JsonDeserializer::class.java),
                QUESTION_MARK_WILDCARD_TYPE_NAME)
    }

    fun build(): TypeSpec {
        return TypeSpec.classBuilder("RealJsonDeserializerFactory")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(ClassName.get(JsonDeserializerFactory::class.java))
                .addField(createDeserializerMapField())
                .addMethod(createConstructor())
                .addMethod(createRegisterMethod())
                .addMethod(createGetMethod())
                .build()
    }

    private fun createDeserializerMapField(): FieldSpec {
        val classTypeName = ParameterizedTypeName.get(ClassName.get(Class::class.java), QUESTION_MARK_WILDCARD_TYPE_NAME)
        val rawMapTypeName = ClassName.get(Map::class.java)
        val mapTypeName = ParameterizedTypeName.get(rawMapTypeName, classTypeName, JSON_DESERIALIZER_TYPE_NAME)
        return FieldSpec.builder(mapTypeName, DESERIALIZER_MAP_NAME, Modifier.PRIVATE, Modifier.FINAL).build()
    }

    private fun createConstructor(): MethodSpec {
        val constructorBuilder = MethodSpec.constructorBuilder()
        constructorBuilder.addModifiers(Modifier.PUBLIC)
        val mapSize = JsonCompiler.mapSize(deserializers)
        constructorBuilder.addStatement("this.$DESERIALIZER_MAP_NAME = new \$T<>(\$L)", LinkedHashMap::class.java, mapSize)
        JsonCompiler.registerDefaultJsonAdapters(constructorBuilder)
        deserializers.forEach {
            val codeFormat = "register(new \$T())"
            constructorBuilder.addStatement(codeFormat, it)
        }
        return constructorBuilder.build()
    }

    private fun createRegisterMethod(): MethodSpec {
        return MethodSpec.methodBuilder("register")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(JSON_DESERIALIZER_TYPE_NAME, "jsonDeserializer")
                .addStatement("$DESERIALIZER_MAP_NAME.put(jsonDeserializer.modelClass(), jsonDeserializer)")
                .build()
    }

    private fun createGetMethod(): MethodSpec {
        val typeParameter = TypeVariableName.get("T")
        val jsonDeserializerTypeName = ParameterizedTypeName.get(ClassName.get(JsonDeserializer::class.java), typeParameter)
        return MethodSpec.methodBuilder("get")
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PUBLIC)
                .addTypeVariable(typeParameter)
                .returns(jsonDeserializerTypeName)
                .addParameter(ParameterizedTypeName.get(ClassName.get(Class::class.java), typeParameter), "modelClass")
                .addComment("noinspection unchecked")
                .addStatement("return (\$T) $DESERIALIZER_MAP_NAME.get(modelClass)", jsonDeserializerTypeName)
                .build()
    }
}
