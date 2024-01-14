plugins {
    alias(libs.plugins.image.toolbox.library)
    alias(libs.plugins.image.toolbox.compose)
}
android.namespace = "com.smarttoolfactory.screenshot"

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.palette.ktx)

    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.runtime)
    implementation(libs.compose.material)
    implementation(libs.compose.material3)
}