package com.jaynewstrom.json.compiler

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File
import java.io.IOException

internal object JsonCompiler {
    fun deserializerName(jsonFileName: String) = jsonFileName + "Deserializer"

    fun serializerName(jsonFileName: String) = jsonFileName + "Serializer"

    fun nameFromFile(file: File): String {
        return file.name.substring(0, file.name.indexOf(file.extension) - 1)
    }

    fun jsonModelType(modelName: String): ClassName {
        return ClassName.bestGuess(modelName)
    }

    fun modelClassFunSpec(modelName: String): FunSpec {
        return FunSpec.builder("modelClass")
            .addModifiers(KModifier.OVERRIDE)
            .returns(Class::class.parameterizedBy(Any::class))
            .addStatement("return \$T.class", JsonCompiler.jsonModelType(modelName))
            .build()
    }

    fun throwsIoExceptionAnnotation(): AnnotationSpec {
        return AnnotationSpec.builder(Throws::class)
            .addMember("value", "%T::class", IOException::class)
            .build()
    }
}
