plugins {
    alias(libs.plugins.image.toolbox.library)
    alias(libs.plugins.image.toolbox.feature)
    alias(libs.plugins.image.toolbox.hilt)
    alias(libs.plugins.image.toolbox.compose)
}

android.namespace = "ru.tech.imageresizershrinker.feature.single_edit"

dependencies {
    implementation(project(":feature:crop"))
    implementation(project(":feature:erase-background"))
    implementation(project(":feature:draw"))
    implementation(project(":feature:filters"))
    implementation(project(":feature:pick-color"))
}