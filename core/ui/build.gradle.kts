plugins {
    alias(libs.plugins.image.toolbox.library)
    alias(libs.plugins.image.toolbox.hilt)
    alias(libs.plugins.image.toolbox.compose)
}

android.namespace = "ru.tech.imageresizershrinker.core.ui"

dependencies {
    api(libs.reimagined)
    api(libs.reimagined.hilt)

    api(libs.androidx.documentfile)

    //AndroidX
    api(libs.activityCompose)
    api(libs.splashScreen)
    api(libs.androidx.exifinterface)
    api(libs.appCompat)
    api(libs.androidx.lifecycle.viewmodel.compose)

    //Konfetti
    api(libs.konfetti.compose)

    //Coil
    api(libs.coil)
    api(libs.coilCompose)
    api(libs.coilGif)
    api(libs.coilSvg)

    //Modules
    api(project(":libs:cropper"))
    api(project(":libs:dynamic-theme"))
    api(project(":libs:colordetector"))
    api(project(":libs:beforeafter"))
    api(project(":libs:modalsheet"))
    api(project(":libs:gesture"))
    api(project(":libs:screenshot"))
    api(project(":libs:systemuicontroller"))
    api(project(":libs:placeholder"))
    api(project(":libs:logger"))
    api(project(":libs:zoomable"))
    implementation(project(":core:domain"))

    api(project(":libs:colorpicker")) {
        exclude("com.github.SmartToolFactory", "Compose-Color-Detector")
    }

    api(libs.reorderable)

    api(libs.compose)
    api(libs.shadowsPlus)

    api(libs.kotlinx.collections.immutable)

    api(libs.fadingEdges)
    api(libs.scrollbar)

    implementation(libs.datastore.preferences.android)

    "marketImplementation"(libs.mlkit.segmentation.selfie)
    "marketImplementation"(libs.firebase.crashlytics.ktx) {
        exclude("androidx.datastore", "datastore-preferences")
    }
    "marketImplementation"(libs.firebase.analytics.ktx)
    "marketImplementation"(libs.review.ktx)
    "marketImplementation"(libs.app.update)
    "marketImplementation"(libs.app.update.ktx)

    api(project(":core:resources"))
}