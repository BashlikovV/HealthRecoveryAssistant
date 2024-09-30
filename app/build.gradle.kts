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

dependencies { }