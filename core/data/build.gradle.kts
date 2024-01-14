plugins {
    alias(libs.plugins.image.toolbox.library)
    alias(libs.plugins.image.toolbox.hilt)
}

android.namespace = "ru.tech.imageresizershrinker.core.data"

dependencies {
    api(libs.coil)
    api(libs.coilCompose)
    api(libs.coilGif)
    api(libs.coilSvg)
    api(libs.avif.coder)
    api(libs.datastore.preferences.android)

    "marketImplementation"(libs.mlkit.segmentation.selfie)

    api(libs.jxl.coder.coil)
    api(libs.jxl.coder)

    api(libs.androidx.exifinterface)
    api(libs.androidx.documentfile)

    api(libs.tesseract)

    implementation(project(":libs:logger"))

    implementation(project(":libs:gpuimage"))

    implementation(project(":core:domain"))
    implementation(project(":core:resources"))
}