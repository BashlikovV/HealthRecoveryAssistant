plugins {
    `kotlin-dsl`
}

group = "by.bashlikovvv.healthrecoveryassistant.buildlogic"

dependencies {
    compileOnly(libs.gradleplugin.android)
    compileOnly(libs.gradleplugin.compose)
    compileOnly(libs.gradleplugin.composeCompiler)
    compileOnly(libs.gradleplugin.kotlin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "healthrecoveryassistant.android.application"
            implementationClass = "by.bashlikovvv.healthrecoveryassistant.AndroidApplicationConventionPlugin"
        }
        register("androidApplicationCompose") {
            id = "healthrecoveryassistant.android.application.compose"
            implementationClass = "by.bashlikovvv.healthrecoveryassistant.AndroidApplicationComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = "healthrecoveryassistant.android.library"
            implementationClass = "by.bashlikovvv.healthrecoveryassistant.AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "healthrecoveryassistant.android.library.compose"
            implementationClass = "by.bashlikovvv.healthrecoveryassistant.AndroidLibraryComposeConventionPlugin"
        }
        register("androidTestsLibrary") {
            id = "healthrecoveryassistant.android.library.tests"
            implementationClass ="by.bashlikovvv.healthrecoveryassistant.AndroidTestsConventionPlugin"
        }
        register("jvmLibrary") {
            id = "healthrecoveryassistant.taxifly.jvm.library"
            implementationClass = "by.bashlikovvv.healthrecoveryassistant.JVMLibraryConventionPlugin"
        }
    }
}