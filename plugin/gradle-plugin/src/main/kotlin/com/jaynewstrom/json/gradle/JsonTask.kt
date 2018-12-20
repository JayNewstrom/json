package com.jaynewstrom.json.gradle

import com.jaynewstrom.json.compiler.DeserializerFactoryBuilder
import com.jaynewstrom.json.compiler.JsonCompiler
import com.jaynewstrom.json.compiler.JsonModelDefinitionParser
import com.jaynewstrom.json.compiler.ModelDefinition
import com.jaynewstrom.json.compiler.SerializerFactoryBuilder
import com.jaynewstrom.json.compiler.VERSION
import com.jaynewstrom.json.compiler.relativePath
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeName
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
            outputDirectory = JsonCompiler.OUTPUT_DIRECTORY.fold(buildDirectory, ::File)
        }

    @TaskAction fun execute(inputs: IncrementalTaskInputs) {
        val fileModelDefinitionMap = linkedMapOf<File, ModelDefinition>()
        val deserializers = arrayListOf<TypeName>()
        val serializers = arrayListOf<TypeName>()
        getInputs().files.forEach { file ->
            val packageName = file.relativePackage()
            val jsonFileName = JsonCompiler.nameFromFile(file)
            val modelDefinition = modelDefinition(file)
            fileModelDefinitionMap.put(file, modelDefinition)
            if (modelDefinition.createSerializer) {
                serializers.add(ClassName.get(packageName, JsonCompiler.serializerName(jsonFileName)))
            }
            if (modelDefinition.createDeserializer) {
                deserializers.add(ClassName.get(packageName, JsonCompiler.deserializerName(jsonFileName)))
            }
        }
        inputs.outOfDate { inputFileDetails ->
            removeFilesAssociatedWithDefinition(inputFileDetails.file)
            val modelDefinition = fileModelDefinitionMap[inputFileDetails.file] ?: return@outOfDate
            createModelSpecificClasses(inputFileDetails.file, modelDefinition)
        }
        if (deserializers.isNotEmpty()) {
            JavaFile.builder(defaultPackage, DeserializerFactoryBuilder(deserializers).build()).build().writeTo(outputDirectory)
        } else {
            removeGeneratedInDefaultPackage("RealJsonDeserializerFactory.java")
        }
        if (serializers.isNotEmpty()) {
            JavaFile.builder(defaultPackage, SerializerFactoryBuilder(serializers).build()).build().writeTo(outputDirectory)
        } else {
            removeGeneratedInDefaultPackage("RealJsonSerializerFactory.java")
        }
        inputs.removed { inputFileDetails ->
            removeFilesAssociatedWithDefinition(inputFileDetails.file)
        }
    }

    private fun File.relativePackage() = absolutePath.relativePath(File.separatorChar).dropLast(1).joinToString(".")

    private fun modelDefinition(file: File): ModelDefinition {
        return JsonModelDefinitionParser(file, createSerializerByDefault, createDeserializerByDefault, file.relativePackage()).parse()
    }

    private fun createModelSpecificClasses(file: File, modelDefinition: ModelDefinition) {
        val packageName = file.relativePackage()
        modelDefinition.createModels(outputDirectory!!)
        if (modelDefinition.createSerializer) {
            JavaFile.builder(packageName, modelDefinition.serializerTypeSpec()).build().writeTo(outputDirectory)
        }
        if (modelDefinition.createDeserializer) {
            JavaFile.builder(packageName, modelDefinition.deserializerTypeSpec()).build().writeTo(outputDirectory)
        }
    }

    private fun removeFilesAssociatedWithDefinition(definitionFile: File) {
        val directory = File(outputDirectory, definitionFile.relativePackage().replace('.', File.separatorChar))
        val fileName = definitionFile.nameWithoutExtension
        // We don't know the files that were generated, so try to remove all possibilities.
        val filesToRemove = arrayOf(
            "$fileName.java", // The immutable model generated by default (no suffix).
            "$fileName.kt", // The kotlin model.
            "$fileName${JsonCompiler.SERIALIZER_SUFFIX}.java",
            "$fileName${JsonCompiler.DESERIALIZER_SUFFIX}.java"
        )
        filesToRemove.forEach { fileNameToRemove ->
            File(directory, fileNameToRemove).delete()
        }
    }

    private fun removeGeneratedInDefaultPackage(fileName: String) {
        val directory = File(outputDirectory, defaultPackage.replace('.', File.separatorChar))
        File(directory, fileName).delete()
    }
}
