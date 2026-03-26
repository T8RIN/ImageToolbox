plugins {
    alias(libs.plugins.image.toolbox.library)
    alias(libs.plugins.image.toolbox.compose)
}

android.namespace = "com.smarttoolfactory.beforeafter"

dependencies {
    implementation(libs.toolbox.gesture)
}