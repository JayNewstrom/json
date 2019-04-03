package com.jaynewstrom.jsonDelight.compiler

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.asClassName
import java.io.File
import java.io.IOException

internal object JsonCompiler {
    fun deserializerName(jsonFileName: String) = jsonFileName + "Deserializer"

    fun serializerName(jsonFileName: String) = jsonFileName + "Serializer"

    fun nameFromFile(file: File): String {
        return file.name.substring(0, file.name.indexOf(file.extension) - 1)
    }

    fun jsonModelType(packageName: String, modelName: String): ClassName {
        return ClassName(packageName, modelName)
    }

    fun modelClassFunSpec(packageName: String, modelName: String): FunSpec {
        return FunSpec.builder("modelClass")
            .addModifiers(KModifier.OVERRIDE)
            .returns(Class::class.asClassName().parameterizedBy(STAR))
            .addStatement("return %T::class.java", jsonModelType(packageName, modelName))
            .build()
    }

    fun throwsIoExceptionAnnotation(): AnnotationSpec {
        return AnnotationSpec.builder(Throws::class)
            .addMember("%T::class", IOException::class)
            .build()
    }
}
