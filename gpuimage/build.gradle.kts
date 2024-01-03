@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}


android {
    compileSdk = libs.versions.androidCompileSdk.get().toIntOrNull()

    namespace = "jp.co.cyberagent.android.gpuimage"
    defaultConfig {
        minSdk = libs.versions.androidMinSdk.get().toIntOrNull()

        ndk.abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
    }
    externalNativeBuild {
        cmake { path("src/main/cpp/CMakeLists.txt") }
    }

    compileOptions {
        sourceCompatibility = rootProject.extra.get("javaCompile") as JavaVersion
        targetCompatibility = rootProject.extra.get("javaCompile") as JavaVersion
    }

    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
    }
}