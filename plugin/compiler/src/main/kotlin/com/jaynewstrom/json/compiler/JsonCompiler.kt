package com.jaynewstrom.json.compiler

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.WildcardTypeName
import java.io.File
import javax.lang.model.element.Modifier

class JsonCompiler {
    companion object {
        private const val DESERIALIZER_SUFFIX = "Deserializer"
        private const val SERIALIZER_SUFFIX = "Serializer"
        const val FILE_EXTENSION = "json"

        val OUTPUT_DIRECTORY = listOf("generated", "source", "json")

        val QUESTION_MARK_WILDCARD_TYPE_NAME: TypeName = WildcardTypeName.subtypeOf(Any::class.java)

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
