package com.jaynewstrom.jsonDelight.gradle

import com.jaynewstrom.jsonDelight.compiler.JsonModelDefinitionParser
import com.jaynewstrom.jsonDelight.compiler.ModelDefinition
import com.jaynewstrom.jsonDelight.compiler.VERSION
import com.jaynewstrom.jsonDelight.compiler.relativePath
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs
import java.io.File

@CacheableTask
open class JsonDelightTask : SourceTask() {
    @Suppress("unused") // Required to invalidate the task on version updates.
    @Input fun pluginVersion() = VERSION

    @Input
    var createSerializerByDefault = false
    @Input
    var createDeserializerByDefault = false

    @get:OutputDirectory
    lateinit var outputDirectory: File

    @PathSensitive(PathSensitivity.RELATIVE)
    override fun getSource(): FileTree {
        return super.getSource()
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
