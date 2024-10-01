plugins {
    alias(libs.plugins.healthrecoveryassistant.android.library)
}

android {
    namespace = "by.bashlikovvv.domain"
}

dependencies {
    implementation(libs.androidx.core.ktx)
}
