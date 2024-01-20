plugins {
    alias(libs.plugins.image.toolbox.library)
    alias(libs.plugins.image.toolbox.compose)
}
android.namespace = "com.smarttoolfactory.colorpicker"

dependencies {
    implementation(projects.libs.gesture)
    implementation(projects.libs.screenshot)
    implementation(projects.libs.extendedcolors)
    implementation(projects.libs.colordetector)

    implementation(libs.compose.colorful.sliders)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.foundation)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.runtime)
    implementation(libs.compose.material3)
    implementation(libs.compose.material)
    implementation(libs.compose.material.iconsExtended)
}