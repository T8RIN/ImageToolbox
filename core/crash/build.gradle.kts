plugins {
    alias(libs.plugins.image.toolbox.library)
    alias(libs.plugins.image.toolbox.hilt)
    alias(libs.plugins.image.toolbox.compose)
}

android.namespace = "ru.tech.imageresizershrinker.core.crash"

dependencies {
    implementation(projects.core.ui)
    implementation(projects.core.settings)

    "marketImplementation"(libs.firebase.crashlytics.ktx)
    "marketImplementation"(libs.firebase.analytics.ktx)
}