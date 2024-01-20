plugins {
    alias(libs.plugins.image.toolbox.library)
    alias(libs.plugins.image.toolbox.compose)
}

android.namespace = "com.smarttoolfactory.colordetector"

dependencies {
    implementation(libs.androidx.palette.ktx)

    implementation(projects.libs.gesture)
    implementation(projects.libs.screenshot)
    implementation(projects.libs.image)
    implementation(projects.libs.zoomable)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.runtime)
    implementation(libs.compose.material3)
    implementation(libs.compose.material)
}