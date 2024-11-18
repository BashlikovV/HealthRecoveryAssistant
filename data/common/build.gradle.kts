plugins {
    alias(libs.plugins.healthrecoveryassistant.android.library)
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

android {
    namespace = "by.bashlikovvv.data.common"
}

dependencies {
    api(projects.core.domain)
    implementation(projects.data.database)
    implementation(projects.data.bluetooth)

    api(libs.koin.android)
    implementation(libs.play.services.wearable)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.datastore.preferences)
}
