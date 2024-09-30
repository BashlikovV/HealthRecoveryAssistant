package by.bashlikovvv.healthrecoveryassistant.plconfig

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>
) {
    commonExtension.run {
        buildFeatures {
            compose = true
        }
        composeOptions {
            kotlinCompilerExtensionVersion = "1.5.1"
        }


        dependencies {
            val bom = libs.findLibrary("androidx.compose.bom").get()
            "implementation"(platform(bom))
            "implementation"(libs.findLibrary("androidx.activity.compose").get())
            "implementation"(libs.findLibrary("androidx.ui").get())
            "implementation"(libs.findLibrary("androidx.ui.graphics").get())
            "implementation"(libs.findLibrary("androidx.material3").get())
            "androidTestImplementation"(platform(bom))
            "androidTestImplementation"(libs.findLibrary("androidx.ui.test.junit4").get())
            "implementation"(libs.findLibrary("androidx.ui.tooling").get())
            "debugImplementation"(libs.findLibrary("androidx.ui.test.manifest").get())
        }
    }
}