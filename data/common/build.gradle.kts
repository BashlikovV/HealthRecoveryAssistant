plugins {
    alias(libs.plugins.healthrecoveryassistant.android.library)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.ksp)
}

android {
    namespace = "by.bashlikovvv.data.common"

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
}

dependencies {
    api(projects.core.domain)

    api(libs.koin.android)
    implementation(libs.play.services.wearable)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.work.runtime.ktx)

    api(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
}
