package by.bashlikovvv.healthrecoveryassistant

import by.bashlikovvv.healthrecoveryassistant.plconfig.configureKotlinJvm
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.kotlin.dsl.apply

class JVMLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            apply<JavaLibraryPlugin>()
            pluginManager.apply("org.jetbrains.kotlin.jvm")
            configureKotlinJvm()
        }
    }
}