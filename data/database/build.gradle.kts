plugins {
    alias(libs.plugins.healthrecoveryassistant.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "by.bashlikovvv.data.database"

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
}

dependencies {
    api(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
}
