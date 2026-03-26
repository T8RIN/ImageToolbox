plugins {
    alias(libs.plugins.image.toolbox.library)
    alias(libs.plugins.image.toolbox.compose)
}

android.namespace = "com.smarttoolfactory.colorpicker"

dependencies {
    implementation(projects.lib.gesture)
    implementation(projects.lib.extendedcolors)
    implementation(projects.lib.colordetector)
}