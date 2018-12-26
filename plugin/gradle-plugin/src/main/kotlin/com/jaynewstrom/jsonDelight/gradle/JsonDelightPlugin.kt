package com.jaynewstrom.jsonDelight.gradle

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.BaseVariant
import com.jaynewstrom.jsonDelight.compiler.VERSION
import org.gradle.api.DomainObjectSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyResolutionListener
import org.gradle.api.artifacts.DependencySet
import org.gradle.api.artifacts.ResolvableDependencies
import org.gradle.api.artifacts.UnknownConfigurationException

class JsonDelightPlugin : Plugin<Project> {
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
        project.extensions.create("jsonDelight", JsonDelightExtension::class.java)

        val generateJsonDelightModel = project.task("generateJsonDelightModel")

        val compileDeps = project.dependencySetForName("implementation") ?: project.dependencySetForName("compile")
        project.gradle.addListener(object : DependencyResolutionListener {
            override fun beforeResolve(dependencies: ResolvableDependencies?) {
                compileDeps?.add(project.dependencies.create("com.jaynewstrom.jsonDelight:runtime:$VERSION"))
                project.gradle.removeListener(this)
            }

            override fun afterResolve(dependencies: ResolvableDependencies?) {
            }
        })

        variants.all { variant ->
            val taskName = "generate${variant.name.capitalize()}JsonDelightModel"
            val task = project.tasks.create(taskName, JsonDelightTask::class.java) { jsonTask ->
                val extension = project.extensions.getByType(JsonDelightExtension::class.java)
                jsonTask.createSerializerByDefault = extension.createSerializerByDefault
                jsonTask.createDeserializerByDefault = extension.createDeserializerByDefault
            }
            task.group = "jsondelightmodel"
            task.buildDirectory = project.buildDir
            task.description = "Generate Json Delight Models and Factories for ${variant.name}"
            task.source(variant.sourceSets.map { sourceSet -> "src/${sourceSet.name}/jsonDelight" })
            task.include("**/*.json")

            generateJsonDelightModel.dependsOn(task)

            variant.registerJavaGeneratingTask(task, task.outputDirectory)
        }
    }

    private fun Project.dependencySetForName(name: String): DependencySet? {
        return try {
            configurations.getByName(name).dependencies
        } catch (e: UnknownConfigurationException) {
            null
        }
    }
}
