package com.jaynewstrom.json.compiler

import com.jaynewstrom.json.runtime.internal.BooleanJsonAdapter
import com.jaynewstrom.json.runtime.internal.ByteJsonAdapter
import com.jaynewstrom.json.runtime.internal.DoubleJsonAdapter
import com.jaynewstrom.json.runtime.internal.FloatJsonAdapter
import com.jaynewstrom.json.runtime.internal.IntegerJsonAdapter
import com.jaynewstrom.json.runtime.internal.LongJsonAdapter
import com.jaynewstrom.json.runtime.internal.ShortJsonAdapter
import com.jaynewstrom.json.runtime.internal.StringJsonAdapter
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

        fun registerDefaultJsonAdapters(methodBuilder: MethodSpec.Builder) {
            val codeFormat = "register(\$T.INSTANCE)"
            methodBuilder.addStatement(codeFormat, ClassName.get(BooleanJsonAdapter::class.java))
            methodBuilder.addStatement(codeFormat, ClassName.get(ByteJsonAdapter::class.java))
            methodBuilder.addStatement(codeFormat, ClassName.get(DoubleJsonAdapter::class.java))
            methodBuilder.addStatement(codeFormat, ClassName.get(FloatJsonAdapter::class.java))
            methodBuilder.addStatement(codeFormat, ClassName.get(IntegerJsonAdapter::class.java))
            methodBuilder.addStatement(codeFormat, ClassName.get(LongJsonAdapter::class.java))
            methodBuilder.addStatement(codeFormat, ClassName.get(ShortJsonAdapter::class.java))
            methodBuilder.addStatement(codeFormat, ClassName.get(StringJsonAdapter::class.java))
        }

        fun mapSize(list: Collection<Any>): Int {
            // Try and initialize the map to a size where it won't need to be expanded.
            return (Math.round((list.size + 10) * 1.4)).toInt()
        }
    }
}
