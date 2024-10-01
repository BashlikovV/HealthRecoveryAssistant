plugins {
    alias(libs.plugins.healthrecoveryassistant.android.library)
}

android {
    namespace = "by.bashlikovvv.data.common"
}

dependencies {
    api(projects.core.domain)

    api(libs.koin.android)
    implementation(libs.play.services.wearable)
}
