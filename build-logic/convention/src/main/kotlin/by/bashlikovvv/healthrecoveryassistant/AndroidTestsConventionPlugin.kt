package by.bashlikovvv.healthrecoveryassistant

import by.bashlikovvv.healthrecoveryassistant.plconfig.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidTestsConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            dependencies {
                "implementation"(libs.findLibrary("junit").get())
                "implementation"(libs.findLibrary("androidx.junit").get())
                "implementation"(libs.findLibrary("androidx.espresso.core").get())
            }
        }
    }
}