plugins {
    alias(libs.plugins.healthrecoveryassistant.android.library)
}

android {
    namespace = "by.bashlikovvv.data.bluetooth"
}

dependencies {
    implementation(libs.kotlin.reflect)
}
