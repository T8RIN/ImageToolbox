@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

android {
    namespace = "ru.tech.imageresizershrinker.coreui"
    compileSdk = libs.versions.androidCompileSdk.get().toIntOrNull()

    defaultConfig {
        minSdk = libs.versions.androidMinSdk.get().toIntOrNull()
    }

    compileOptions {
        sourceCompatibility = rootProject.extra.get("javaCompile") as JavaVersion
        targetCompatibility = rootProject.extra.get("javaCompile") as JavaVersion
    }

    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    flavorDimensions += "app"

    productFlavors {
        create("foss") {
            dimension = "app"
        }
        create("market") {
            dimension = "app"
        }
        create("jxl") {
            dimension = "app"
        }
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugaring)

    api(libs.reimagined)
    api(libs.reimagined.hilt)

    //AndroidX
    api(libs.activityCompose)
    api(libs.splashScreen)
    api(libs.androidx.exifinterface)
    api(libs.appCompat)
    api(libs.androidx.lifecycle.viewmodel.compose)

    //Konfetti
    api(libs.konfetti.compose)

    //Compose
    api(libs.androidx.material3)
    api(libs.androidx.material3.window.sizeclass)
    api(libs.androidx.material.icons.extended)
    api(libs.androidx.material)

    //Di
    implementation(libs.hilt)
    kapt(libs.dagger.hilt.compiler)

    //Coil
    api(libs.coil)
    api(libs.coilCompose)
    api(libs.coilGif)
    api(libs.coilSvg)

    //Modules
    api(project(":cropper"))
    api(project(":dynamic_theme"))
    api(project(":colordetector"))
    api(project(":beforeafter"))
    api(project(":image"))
    api(project(":modalsheet"))
    api(project(":gesture"))
    api(project(":screenshot"))
    api(project(":systemuicontroller"))
    api(project(":placeholder"))
    api(project(":logger"))
    implementation(project(":core-domain"))

    api(project(":colorpicker")) {
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

    api(project(":core-resources"))
}