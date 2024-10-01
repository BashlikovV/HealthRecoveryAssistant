plugins {
    alias(libs.plugins.healthrecoveryassistant.android.library.compose)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    id("kotlin-parcelize")
}

android {
    namespace = "by.bashlikovvv.feature.root"
}

dependencies {
    implementation(projects.core.ui)
    implementation(projects.core.domain)
    implementation(projects.data.common)

    implementation(projects.features.home)
}
