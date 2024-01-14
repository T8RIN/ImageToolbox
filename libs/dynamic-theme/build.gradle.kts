plugins {
    alias(libs.plugins.image.toolbox.library)
    alias(libs.plugins.image.toolbox.compose)
}

android.namespace = "com.t8rin.dynamic.theme"


dependencies {
    implementation(libs.androidx.palette.ktx)
    implementation(libs.androidx.ui.text)

    implementation(project(":libs:systemuicontroller"))
    implementation(libs.m3color)
}