plugins {
    alias(libs.plugins.image.toolbox.library)
    alias(libs.plugins.image.toolbox.compose)
}

android.namespace = "ru.tech.imageresizershrinker.core.filters"

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:resources"))
    implementation(project(":core:ui"))
}