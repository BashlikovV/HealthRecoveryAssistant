plugins {
    alias(libs.plugins.healthrecoveryassistant.android.library.compose)
    id("kotlin-parcelize")
}

android {
    namespace = "by.bashlikovvv.feature.home"
}

dependencies {
    implementation(projects.core.ui)
    implementation(projects.core.domain)
    implementation(projects.data.common)
}
