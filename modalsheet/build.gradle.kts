@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.t8rin.modalsheet"
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
        compose = true
    }
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.material)
    implementation(libs.compose.material3)
    implementation(libs.lifecycle.viewmodel.savedstate)
    implementation(libs.viewModelKtx)
}
