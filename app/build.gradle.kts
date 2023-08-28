@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

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
    namespace = "ru.tech.imageresizershrinker"
    compileSdk = 34

    defaultConfig {
        applicationId = "ru.tech.imageresizershrinker"
        minSdk = 21
        targetSdk = 34
        versionCode = 84
        versionName = "2.3.0"
        resourceConfigurations += setOf(
            "en",
            "ar",
            "de",
            "es",
            "eu",
            "fil",
            "fr",
            "hi",
            "it",
            "ja",
            "pl",
            "pt-rBR",
            "ro",
            "ru",
            "tr",
            "uk",
            "zh-rCN",
            "zh-rTW",
            "vi",
            "ko",
            "af",
            "bn",
            "th",
            "id",
            "iw",
            "cs",
            "sr",
            "da",
            "pt",
            "sk",
            "be"
        )
        archivesName.set("image-toolbox-$versionName")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }

    packaging {
        resources {
            excludes += setOf(
                "META-INF/*.kotlin_module",
                "kotlin/*.kotlin_builtins",
                "kotlin/**/*.kotlin_builtins",
                "META-INF/*",
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
}

dependencies {

    //AndroidX
    implementation("androidx.activity:activity-compose:1.8.0-alpha06")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.exifinterface:exifinterface:1.3.6")
    implementation("androidx.appcompat:appcompat:1.7.0-alpha03")
    implementation("androidx.documentfile:documentfile:1.0.1")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")

    //Navigation
    implementation("dev.olshevski.navigation:reimagined:1.5.0")
    implementation("dev.olshevski.navigation:reimagined-hilt:1.5.0")

    //Konfetti
    implementation("nl.dionsegijn:konfetti-compose:2.0.3")

    //Compose
    implementation("androidx.compose.material3:material3:1.2.0-alpha04")
    implementation("androidx.compose.material3:material3-window-size-class:1.2.0-alpha04")
    implementation("androidx.compose.material:material-icons-extended:1.6.0-alpha02")
    implementation("androidx.compose.material:material:1.6.0-alpha02")

    //Di
    implementation("com.google.dagger:hilt-android:2.47")
    kapt("com.google.dagger:hilt-compiler:2.47")

    //Coil
    implementation("io.coil-kt:coil:2.4.0")
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("io.coil-kt:coil-gif:2.4.0")
    implementation("io.coil-kt:coil-svg:2.4.0")

    //Modules
    implementation(project(":cropper"))
    implementation(project(":dynamic_theme"))
    implementation(project(":colordetector"))
    implementation(project(":beforeafter"))
    implementation(project(":image"))
    implementation(project(":modalsheet"))
    implementation(project(":gpuimage"))
    implementation(project(":gesture"))
    implementation(project(":avif-coder-coil"))
    implementation(project(":avif-coder"))
    implementation(project(":screenshot"))
    implementation(project(":systemuicontroller"))
    implementation(project(":placeholder-material3"))
    implementation(project(":placeholder"))

    implementation(project(":colorpicker")) {
        exclude("com.github.SmartToolFactory", "Compose-Color-Detector")
    }

    implementation("org.burnoutcrew.composereorderable:reorderable:0.9.6")

    //ml-kit selfie segmentation
    implementation("com.google.mlkit:segmentation-selfie:16.0.0-beta4")

    implementation("com.google.firebase:firebase-crashlytics-ktx:18.4.1")
    implementation("com.google.firebase:firebase-analytics-ktx:21.3.0")
}
