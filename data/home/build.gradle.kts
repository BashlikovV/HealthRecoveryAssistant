plugins {
    alias(libs.plugins.healthrecoveryassistant.android.library)
}

android {
    namespace = "by.bashlikovvv.data.home"
}

dependencies {
    implementation(projects.data.common)
}
