package com.jaynewstrom.json.gradle

import com.jaynewstrom.json.compiler.DeserializerFactoryBuilder
import com.jaynewstrom.json.compiler.JsonModelDefinitionParser
import com.jaynewstrom.json.compiler.ModelDefinition
import com.jaynewstrom.json.compiler.SerializerFactoryBuilder
import com.jaynewstrom.json.compiler.VERSION
import com.jaynewstrom.json.compiler.relativePath
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeName
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs
import java.io.File

@CacheableTask
open class JsonTask : SourceTask() {
    @Suppress("unused") // Required to invalidate the task on version updates.
    @Input fun pluginVersion() = VERSION

    @Input var defaultPackage = ""
    @Input var createSerializerByDefault = false
    @Input var createDeserializerByDefault = false

    @get:OutputDirectory var outputDirectory: File? = null

    var buildDirectory: File? = null
        set(value) {
            field = value
            outputDirectory = listOf("generated", "source", "json").fold(buildDirectory, ::File)
        }

    @TaskAction fun execute(inputs: IncrementalTaskInputs) {
        val fileModelDefinitionMap = linkedMapOf<File, ModelDefinition>()
        val deserializers = arrayListOf<TypeName>()
        val serializers = arrayListOf<TypeName>()
        getInputs().files.forEach { file ->
            val modelDefinition = modelDefinition(file)
            fileModelDefinitionMap[file] = modelDefinition
            if (modelDefinition.createSerializer) {
                serializers.add(modelDefinition.serializerTypeName)
            }
            if (modelDefinition.createDeserializer) {
                deserializers.add(modelDefinition.deserializerTypeName)
            }
        }
        inputs.outOfDate { inputFileDetails ->
            removeFilesAssociatedWithDefinition(inputFileDetails.file)
            val modelDefinition = fileModelDefinitionMap[inputFileDetails.file] ?: return@outOfDate
            createModelSpecificClasses(modelDefinition)
        }
        if (deserializers.isNotEmpty()) {
            FileSpec.get(defaultPackage, DeserializerFactoryBuilder(deserializers).build()).writeTo(outputDirectory!!)
        } else {
            removeGeneratedInDefaultPackage("RealJsonDeserializerFactory.kt")
        }
        if (serializers.isNotEmpty()) {
            FileSpec.get(defaultPackage, SerializerFactoryBuilder(serializers).build()).writeTo(outputDirectory!!)
        } else {
            removeGeneratedInDefaultPackage("RealJsonSerializerFactory.kt")
        }
        inputs.removed { inputFileDetails ->
            removeFilesAssociatedWithDefinition(inputFileDetails.file)
        }
    }

    private fun File.relativePackage() = absolutePath.relativePath(File.separatorChar).dropLast(1).joinToString(".")

    private fun modelDefinition(file: File): ModelDefinition {
        return JsonModelDefinitionParser(file, createSerializerByDefault, createDeserializerByDefault, file.relativePackage()).parse()
    }

    private fun createModelSpecificClasses(modelDefinition: ModelDefinition) {
        modelDefinition.createModels(outputDirectory!!)
    }

    private fun removeFilesAssociatedWithDefinition(definitionFile: File) {
        val directory = File(outputDirectory, definitionFile.relativePackage().replace('.', File.separatorChar))
        val fileName = definitionFile.nameWithoutExtension
        File(directory, "$fileName.kt").delete()
    }

    private fun removeGeneratedInDefaultPackage(fileName: String) {
        val directory = File(outputDirectory, defaultPackage.replace('.', File.separatorChar))
        File(directory, fileName).delete()
    }
}
