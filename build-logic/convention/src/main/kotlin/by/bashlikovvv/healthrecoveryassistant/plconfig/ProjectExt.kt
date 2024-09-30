package by.bashlikovvv.healthrecoveryassistant.plconfig

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

val Project.plugins: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("plugins")

val Project.props: Map<String, *>
    get() = properties