package by.bashlikovvv.healthrecoveryassistant

import by.bashlikovvv.healthrecoveryassistant.plconfig.ExtensionType
import by.bashlikovvv.healthrecoveryassistant.plconfig.configureBuildTypes
import by.bashlikovvv.healthrecoveryassistant.plconfig.configureKotlinAndroid
import by.bashlikovvv.healthrecoveryassistant.plconfig.props
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

const val RELEASE_SIGNING_CONFIG_NAME = "release"

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }
            extensions.configure<ApplicationExtension> {
                signingConfigs {
                    create(RELEASE_SIGNING_CONFIG_NAME) {
                        storeFile = file("../build-logic/convention/keystore.jks")
                        keyAlias = props["keyAlias"].toString()
                        keyPassword = props["keyPassword"].toString()
                        storePassword = props["storePassword"].toString()
                    }
                }

                defaultConfig {
                    applicationId = props["projectApplicationId"].toString()
                    targetSdk = props["projectTargetSdkVersion"].toString().toInt()
                    versionCode = props["projectVersionCode"].toString().toInt()
                    versionName = props["projectVersionName"].toString()
                    signingConfig = signingConfigs.getByName(RELEASE_SIGNING_CONFIG_NAME)
                }

                configureKotlinAndroid(this)

                configureBuildTypes(
                    commonExtension = this,
                    extensionType = ExtensionType.APPLICATION
                )
            }
        }
    }
}