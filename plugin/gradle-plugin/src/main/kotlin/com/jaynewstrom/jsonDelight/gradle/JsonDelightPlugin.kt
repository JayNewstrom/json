package com.jaynewstrom.jsonDelight.gradle

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.AndroidSourceSet
import com.android.build.gradle.api.BaseVariant
import com.jaynewstrom.jsonDelight.compiler.VERSION
import org.gradle.api.DomainObjectSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyResolutionListener
import org.gradle.api.artifacts.DependencySet
import org.gradle.api.artifacts.ResolvableDependencies
import org.gradle.api.artifacts.UnknownConfigurationException
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.HasConvention
import org.jetbrains.kotlin.gradle.plugin.KOTLIN_DSL_NAME
import org.jetbrains.kotlin.gradle.plugin.KOTLIN_JS_DSL_NAME
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import java.io.File

class JsonDelightPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.all {
            when (it) {
                is AppPlugin -> {
                    val extension = project.extensions.getByType(AppExtension::class.java)
                    configureAndroid(project, extension, extension.applicationVariants)
                }
                is LibraryPlugin -> {
                    val extension = project.extensions.getByType(LibraryExtension::class.java)
                    configureAndroid(project, extension, extension.libraryVariants)
                }
            }
        }
    }

    private fun <T : BaseVariant> configureAndroid(project: Project, androidExtension: BaseExtension, variants: DomainObjectSet<T>) {
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

        val sources: Map<String, SourceDirectorySet> = androidExtension.sources()

        variants.all { variant ->
            val buildDirectory = listOf("generated", "source", "jsonDelight", variant.name).fold(project.buildDir, ::File)
            val taskName = "generate${variant.name.capitalize()}JsonDelightModel"
            val taskProvider = project.tasks.register(taskName, JsonDelightTask::class.java) { task ->
                val extension = project.extensions.getByType(JsonDelightExtension::class.java)
                task.createSerializerByDefault = extension.createSerializerByDefault
                task.createDeserializerByDefault = extension.createDeserializerByDefault
                task.group = "jsondelightmodel"
                task.outputDirectory = buildDirectory
                task.description = "Generate Json Delight Models and Factories for ${variant.name}"
                task.source(variant.sourceSets.map { sourceSet -> "src/${sourceSet.name}/jsonDelight" })
                task.include("**/*.json")
            }

            sources[variant.name]?.srcDir(buildDirectory.toRelativeString(project.projectDir))

            // TODO: https://issuetracker.google.com/issues/117343589
            variant.registerJavaGeneratingTask(taskProvider.get(), buildDirectory)
        }
    }

    private fun Project.dependencySetForName(name: String): DependencySet? {
        return try {
            configurations.getByName(name).dependencies
        } catch (e: UnknownConfigurationException) {
            null
        }
    }

    private fun BaseExtension.sources(): Map<String, SourceDirectorySet> {
        return sourceSets
            .associate { sourceSet ->
                sourceSet.name to (sourceSet as AndroidSourceSet).kotlinSourceSet!!
            }
    }

    // Copied from kotlin plugin
    private val Any.kotlinSourceSet: SourceDirectorySet?
        get() {
            val convention = (getConvention(KOTLIN_DSL_NAME) ?: getConvention(KOTLIN_JS_DSL_NAME)) ?: return null
            val kotlinSourceSetIface =
                convention.javaClass.interfaces.find { it.name == KotlinSourceSet::class.qualifiedName }
            val getKotlin = kotlinSourceSetIface?.methods?.find { it.name == "getKotlin" } ?: return null
            return getKotlin(convention) as? SourceDirectorySet
        }

    private fun Any.getConvention(name: String): Any? = (this as HasConvention).convention.plugins[name]
}
