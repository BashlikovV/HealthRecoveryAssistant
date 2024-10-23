plugins {
    alias(libs.plugins.healthrecoveryassistant.android.library)
}

android {
    namespace = "by.bashlikovvv.util"
}

dependencies {
    implementation(libs.androidx.core.ktx)
}
