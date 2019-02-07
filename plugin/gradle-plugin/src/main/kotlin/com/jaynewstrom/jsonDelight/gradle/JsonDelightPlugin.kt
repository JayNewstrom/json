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
import java.io.File

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
            val taskProvider = project.tasks.register(taskName, JsonDelightTask::class.java) { task ->
                val extension = project.extensions.getByType(JsonDelightExtension::class.java)
                task.createSerializerByDefault = extension.createSerializerByDefault
                task.createDeserializerByDefault = extension.createDeserializerByDefault
                task.group = "jsondelightmodel"
                task.outputDirectory = listOf("generated", "source", "jsonDelight", variant.name).fold(project.buildDir, ::File)
                task.description = "Generate Json Delight Models and Factories for ${variant.name}"
                task.source(variant.sourceSets.map { sourceSet -> "src/${sourceSet.name}/jsonDelight" })
                task.include("**/*.json")
            }

            // TODO: https://issuetracker.google.com/issues/117343589
            variant.registerJavaGeneratingTask(taskProvider.get(), taskProvider.get().outputDirectory)
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
