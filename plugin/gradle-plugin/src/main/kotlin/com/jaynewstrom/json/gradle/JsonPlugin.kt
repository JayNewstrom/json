package com.jaynewstrom.json.gradle

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.BaseVariant
import com.jaynewstrom.json.compiler.COMPOSITE_VERSION
import com.jaynewstrom.json.compiler.VERSION
import org.gradle.api.DomainObjectSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyResolutionListener
import org.gradle.api.artifacts.DependencySet
import org.gradle.api.artifacts.ResolvableDependencies
import org.gradle.api.artifacts.UnknownConfigurationException

class JsonPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.all {
            when (it) {
                is AppPlugin -> configureAndroid(project,
                    project.extensions.getByType(AppExtension::class.java).applicationVariants)
                is LibraryPlugin -> configureAndroid(project,
                    project.extensions.getByType(LibraryExtension::class.java).libraryVariants)
            }
        }
    }

    private fun <T : BaseVariant> configureAndroid(project: Project, variants: DomainObjectSet<T>) {
        project.extensions.create("json", JsonExtension::class.java)

        val generateJsonModel = project.task("generateJsonModel")

        val compileDeps = project.dependencySetForName("implementation") ?: project.dependencySetForName("compile")
        val annotationProcessorDeps = project.dependencySetForName("kapt") ?: project.dependencySetForName("annotationProcessor")
        project.gradle.addListener(object : DependencyResolutionListener {
            override fun beforeResolve(dependencies: ResolvableDependencies?) {
                compileDeps?.add(project.dependencies.create("com.jaynewstrom.json:runtime:$VERSION"))
                compileDeps?.add(project.dependencies.create("com.jaynewstrom.composite:runtime:$COMPOSITE_VERSION"))
                annotationProcessorDeps?.add(project.dependencies.create("com.jaynewstrom.composite:compiler:$COMPOSITE_VERSION"))
                project.gradle.removeListener(this)
            }

            override fun afterResolve(dependencies: ResolvableDependencies?) {
            }
        })

        variants.all { variant ->
            val taskName = "generate${variant.name.capitalize()}JsonModel"
            val task = project.tasks.create(taskName, JsonTask::class.java) { jsonTask ->
                val extension = project.extensions.getByType(JsonExtension::class.java)
                jsonTask.defaultPackage = variant.generateBuildConfig.buildConfigPackageName
                jsonTask.createSerializerByDefault = extension.createSerializerByDefault
                jsonTask.createDeserializerByDefault = extension.createDeserializerByDefault
            }
            task.group = "jsonmodel"
            task.buildDirectory = project.buildDir
            task.description = "Generate Json Models and Factories for ${variant.name}"
            task.source(variant.sourceSets.map { sourceSet -> "src/${sourceSet.name}/json" })
            task.include("**/*.json")

            generateJsonModel.dependsOn(task)

            variant.registerJavaGeneratingTask(task, task.outputDirectory)
        }
    }

    private fun Project.dependencySetForName(name: String): DependencySet? {
        try {
            return configurations.getByName(name).dependencies
        } catch (e: UnknownConfigurationException) {
            return null
        }
    }
}
