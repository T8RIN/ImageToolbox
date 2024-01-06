
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
}

android {
    namespace = "ru.tech.imageresizershrinker.core.data"
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
    implementation(libs.hilt)
    kapt(libs.dagger.hilt.compiler)
    implementation(libs.coil)
    implementation(libs.coilCompose)
    implementation(libs.coilGif)
    implementation(libs.coilSvg)
    implementation(libs.avif.coder)
    api(libs.datastore.preferences.android)

    "marketImplementation"(libs.mlkit.segmentation.selfie)
    "jxlImplementation"(libs.mlkit.segmentation.selfie)
    "jxlImplementation"(libs.jxl.coder.coil)

    api(libs.androidx.exifinterface)
    api(libs.androidx.documentfile)

    implementation(libs.t8rin.bitmapscaler)

    implementation(project(":libs:gpuimage"))

    implementation(project(":core:domain"))
    implementation(project(":core:resources"))
}