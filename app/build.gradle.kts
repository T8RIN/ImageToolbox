plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "ru.tech.imageresizershrinker"
    compileSdk = 33

    defaultConfig {
        applicationId = "ru.tech.imageresizershrinker"
        minSdk = 21
        targetSdk = 33
        versionCode = 35
        versionName = "1.9.0"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.2"
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.activity:activity-compose:1.7.0-beta01")
    implementation("androidx.compose.material3:material3:1.1.0-alpha08")
    implementation("androidx.compose.material3:material3-window-size-class:1.1.0-alpha08")
    implementation("androidx.compose.material:material-icons-extended:1.4.0-rc01")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.28.0")
    implementation("io.coil-kt:coil-compose:2.2.2")
    implementation("androidx.core:core-splashscreen:1.0.0")
    implementation("androidx.exifinterface:exifinterface:1.3.6")

    implementation("dev.olshevski.navigation:reimagined:1.3.1")
    implementation("dev.olshevski.navigation:reimagined-hilt:1.3.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.0")
    implementation("nl.dionsegijn:konfetti-compose:2.0.2")
    implementation("androidx.datastore:datastore-preferences:1.1.0-alpha01")
    implementation("androidx.documentfile:documentfile:1.0.1")
    implementation("com.google.dagger:hilt-android:2.45")
    kapt("com.google.dagger:hilt-compiler:2.45")

    implementation("androidx.appcompat:appcompat:1.7.0-alpha02")

    implementation(project(":cropper"))
    implementation(project(":dynamic_theme"))
    implementation(project(":colordetector"))
    implementation(project(":beforeafter"))

//    implementation("androidx.work:work-runtime-ktx:2.8.0")
//    implementation("androidx.compose.runtime:runtime-livedata:1.3.3")
}