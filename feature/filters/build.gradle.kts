plugins {
    alias(libs.plugins.image.toolbox.library)
    alias(libs.plugins.image.toolbox.feature)
    alias(libs.plugins.image.toolbox.hilt)
    alias(libs.plugins.image.toolbox.compose)
}

android.namespace = "ru.tech.imageresizershrinker.feature.filters"

dependencies {
    api(project(":core:filters"))
    implementation(project(":feature:draw"))
    implementation(project(":feature:pick-color"))
}