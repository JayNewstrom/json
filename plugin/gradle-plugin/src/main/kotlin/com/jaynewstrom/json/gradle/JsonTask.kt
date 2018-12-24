package com.jaynewstrom.json.gradle

import com.jaynewstrom.json.compiler.JsonModelDefinitionParser
import com.jaynewstrom.json.compiler.ModelDefinition
import com.jaynewstrom.json.compiler.VERSION
import com.jaynewstrom.json.compiler.relativePath
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

    @Input var createSerializerByDefault = false
    @Input var createDeserializerByDefault = false

    @get:OutputDirectory lateinit var outputDirectory: File

    var buildDirectory: File? = null
        set(value) {
            field = value
            outputDirectory = listOf("generated", "source", "json").fold(buildDirectory!!, ::File)
        }

    @TaskAction fun execute(inputs: IncrementalTaskInputs) {
        if (!inputs.isIncremental) {
            outputDirectory.delete()
        }
        inputs.outOfDate { inputFileDetails ->
            removeFilesAssociatedWithDefinition(inputFileDetails.file)
            val modelDefinition = modelDefinition(inputFileDetails.file)
            modelDefinition.createModels(outputDirectory)
        }
        inputs.removed { inputFileDetails ->
            removeFilesAssociatedWithDefinition(inputFileDetails.file)
        }
    }

    private fun File.relativePackage() = absolutePath.relativePath(File.separatorChar).dropLast(1).joinToString(".")

    private fun modelDefinition(file: File): ModelDefinition {
        try {
            return JsonModelDefinitionParser(
                file,
                createSerializerByDefault,
                createDeserializerByDefault,
                file.relativePackage()
            ).parse()
        } catch (e: Exception) {
            logger.error("Error parsing $file")
            throw e
        }
    }

    private fun removeFilesAssociatedWithDefinition(definitionFile: File) {
        val directory = File(outputDirectory, definitionFile.relativePackage().replace('.', File.separatorChar))
        val fileName = definitionFile.nameWithoutExtension
        File(directory, "$fileName.kt").delete()
    }
}
