package com.jaynewstrom.json.compiler

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.WildcardTypeName
import java.io.File
import javax.lang.model.element.Modifier

class JsonCompiler {
    companion object {
        const val DESERIALIZER_SUFFIX = "Deserializer"
        const val SERIALIZER_SUFFIX = "Serializer"
        const val INTERFACE_SUFFIX = "Interface"
        const val INTERFACE_BUILDER_SUFFIX = "BuilderInterface"

        const val FILE_EXTENSION = "json"

        val OUTPUT_DIRECTORY = listOf("generated", "source", "json")

        fun deserializerName(jsonFileName: String) = jsonFileName + DESERIALIZER_SUFFIX

        fun serializerName(jsonFileName: String) = jsonFileName + SERIALIZER_SUFFIX

        fun nameFromFile(file: File): String {
            return file.name.substring(0, file.name.indexOf(file.extension) - 1)
        }

        fun jsonModelType(modelName: String): ClassName {
            return ClassName.bestGuess(modelName)
        }

        fun modelClassMethodSpec(modelName: String): MethodSpec {
            return MethodSpec.methodBuilder("modelClass")
                    .addAnnotation(Override::class.java)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(ParameterizedTypeName.get(ClassName.get(Class::class.java), WildcardTypeName.subtypeOf(Any::class.java)))
                    .addStatement("return \$T.class", JsonCompiler.jsonModelType(modelName))
                    .build()
        }
    }
}
