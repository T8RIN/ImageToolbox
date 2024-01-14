plugins {
    alias(libs.plugins.image.toolbox.library)
    alias(libs.plugins.image.toolbox.hilt)
}

android.namespace = "ru.tech.imageresizershrinker.core.domain"

dependencies {
    implementation(libs.coil)
    implementation(project(":core:resources"))
}