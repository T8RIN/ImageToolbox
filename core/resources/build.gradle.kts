
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "ru.tech.imageresizershrinker.core.resources"
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

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        getByName("release") {
            buildConfigField("String", "VERSION_NAME", "\"${libs.versions.versionName.get()}\"")
            buildConfigField("int", "VERSION_CODE", libs.versions.versionCode.get())
        }
        getByName("debug") {
            buildConfigField("String", "VERSION_NAME", "\"${libs.versions.versionName.get()}\"")
            buildConfigField("int", "VERSION_CODE", libs.versions.versionCode.get())
        }
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
    implementation(libs.androidxCore)
    implementation(libs.appCompat)
    implementation(libs.splashScreen)
}