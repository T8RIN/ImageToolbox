plugins {
    alias(libs.plugins.image.toolbox.library)
    alias(libs.plugins.image.toolbox.feature)
    alias(libs.plugins.image.toolbox.hilt)
    alias(libs.plugins.image.toolbox.compose)
}

android.namespace = "ru.tech.imageresizershrinker.feature.main"

dependencies {
    implementation(project(":feature:load-net-image"))
    implementation(project(":feature:crop"))
    implementation(project(":feature:limits-resize"))
    implementation(project(":feature:cipher"))
    implementation(project(":feature:image-preview"))
    implementation(project(":feature:bytes-resize"))
    implementation(project(":feature:compare"))
    implementation(project(":feature:delete-exif"))
    implementation(project(":feature:generate-palette"))
    implementation(project(":feature:resize-convert"))
    implementation(project(":feature:pdf-tools"))
    implementation(project(":feature:single-edit"))
    implementation(project(":feature:erase-background"))
    implementation(project(":feature:draw"))
    implementation(project(":feature:filters"))
    implementation(project(":feature:image-stitch"))
    implementation(project(":feature:pick-color"))
    implementation(project(":feature:recognize-text"))
}