plugins {
    alias(libs.plugins.image.toolbox.library)
    alias(libs.plugins.image.toolbox.compose)
}

android.namespace = "com.smarttoolfactory.cropper"

dependencies {
    implementation(projects.lib.gesture)
}