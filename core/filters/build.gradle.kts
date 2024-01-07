
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "ru.tech.imageresizershrinker.core.filters"
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

    flavorDimensions += "app"

    productFlavors {
        create("foss") {
            dimension = "app"
        }
        create("market") {
            dimension = "app"
        }
        
    }
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:resources"))
    implementation(project(":core:ui"))
}