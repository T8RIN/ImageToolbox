@file:Suppress("UnstableApiUsage")

import dagger.hilt.android.plugin.util.capitalize
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName
import java.util.Locale

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    var isFoss = false

    namespace = "ru.tech.imageresizershrinker"
    compileSdk = libs.versions.androidCompileSdk.get().toIntOrNull()

    defaultConfig {
        applicationId = "ru.tech.imageresizershrinker"
        minSdk = libs.versions.androidMinSdk.get().toIntOrNull()
        targetSdk = libs.versions.androidTargetSdk.get().toIntOrNull()
        versionCode = libs.versions.versionCode.get().toIntOrNull()
        versionName = libs.versions.versionName.get()

        resourceConfigurations += setOf(
            "en",
            "af",
            "ar",
            "be",
            "bn",
            "cs",
            "da",
            "de",
            "es",
            "eu",
            "fa",
            "fil",
            "fr",
            "hi",
            "hu",
            "ia",
            "in",
            "it",
            "iw",
            "ja",
            "kk",
            "kn",
            "ko",
            "nl",
            "pl",
            "pt",
            "pt-rBR",
            "ro",
            "ru",
            "sk",
            "sr",
            "te",
            "th",
            "tr",
            "uk",
            "vi",
            "zh-rCN",
            "zh-rTW"
        )
        archivesName.set("image-toolbox-$versionName${if (isFoss) "-foss" else ""}")
    }

    flavorDimensions += "app"

    productFlavors {
        create("foss") {
            dimension = "app"
            isFoss = true
            extra.set("gmsEnabled", false)
        }
        create("market") {
            dimension = "app"
            extra.set("gmsEnabled", true)
        }
        create("jxl") {
            dimension = "app"
            versionNameSuffix = "-jxl"
            extra.set("gmsEnabled", true)
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = rootProject.extra.get("javaCompile") as JavaVersion
        targetCompatibility = rootProject.extra.get("javaCompile") as JavaVersion
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += setOf(
                "META-INF/*.kotlin_module",
                "kotlin/*.kotlin_builtins",
                "kotlin/**/*.kotlin_builtins",
                "CERT.SF",
                "publicsuffixes.gz"
            )
        }
    }
    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86_64")
            isUniversalApk = true
        }
    }
    packaging {
        jniLibs {
            pickFirsts.add("lib/*/libcoder.so")
        }
    }

    lint {
        disable += "UsingMaterialAndMaterial3Libraries"
        disable += "ModifierParameter"
    }
}

dependencies {

    coreLibraryDesugaring(libs.desugaring)

    //Di
    implementation(libs.hilt)
    kapt(libs.dagger.hilt.compiler)

    implementation(project(":core:domain"))
    implementation(project(":core-ui"))
    implementation(project(":core:data"))
    implementation(project(":core:resources"))

    implementation(project(":feature:main"))

    "marketImplementation"(libs.firebase.crashlytics.ktx) {
        exclude("androidx.datastore", "datastore-preferences")
    }
    "marketImplementation"(libs.firebase.analytics.ktx)

    "jxlImplementation"(libs.firebase.crashlytics.ktx) {
        exclude("androidx.datastore", "datastore-preferences")
    }
    "jxlImplementation"(libs.firebase.analytics.ktx)

}


afterEvaluate {
    android.productFlavors.forEach { flavor ->
        tasks.matching {
            (it.name.contains("GoogleServices") || it.name.contains("Crashlytics")) && it.name.contains(
                flavor.name.capitalize(Locale.getDefault())
            )
        }.forEach {
            it.enabled = flavor.extra.get("gmsEnabled") == true
        }
    }
}