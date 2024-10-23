plugins {
    alias(libs.plugins.healthrecoveryassistant.android.application.compose)
    alias(libs.plugins.healthrecoveryassistant.android.tests)
}

android {
    namespace = "by.bashlikovvv.healthrecoveryassistant"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.ui)
    implementation(projects.data.common)
    implementation(projects.data.database)
    implementation(projects.data.bluetooth)
    implementation(projects.features.root)
}