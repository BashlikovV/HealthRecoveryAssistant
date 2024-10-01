plugins {
    alias(libs.plugins.healthrecoveryassistant.android.library.compose)
    id("kotlin-parcelize")
}

android {
    namespace = "by.bashlikovvv.ui"
}

dependencies {
    api(libs.mvikotlin)
    api(libs.mvikotlin.main)
    api(libs.mvikotlin.extensions.coroutines)
    api(libs.decompose)
    api(libs.decompose.extensioins.compose)
    api(libs.koin.android)
}
