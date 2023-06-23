plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "ru.tech.imageresizershrinker"
    compileSdk = 34

    defaultConfig {
        applicationId = "ru.tech.imageresizershrinker"
        minSdk = 21
        targetSdk = 34
        versionCode = 69
        versionName = "2.2.0-beta3"
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
            "sk"
        )
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
        kotlinCompilerExtensionVersion = "1.4.7"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    //AndroidX
    implementation("androidx.activity:activity-compose:1.8.0-alpha06")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.exifinterface:exifinterface:1.3.6")
    implementation("androidx.appcompat:appcompat:1.7.0-alpha02")
    implementation("androidx.documentfile:documentfile:1.0.1")
    implementation("androidx.datastore:datastore-preferences:1.1.0-alpha04")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")

    //Navigation
    implementation("dev.olshevski.navigation:reimagined:1.5.0-alpha01")
    implementation("dev.olshevski.navigation:reimagined-hilt:1.5.0-alpha01")

    //Konfetti
    implementation("nl.dionsegijn:konfetti-compose:2.0.2")

    //Compose
    implementation("androidx.compose.material3:material3:1.2.0-alpha03")
    implementation("androidx.compose.material3:material3-window-size-class:1.2.0-alpha03")
    implementation("androidx.compose.material:material-icons-extended:1.6.0-alpha01")
    implementation("androidx.compose.material:material:1.6.0-alpha01")

    //Di
    implementation("com.google.dagger:hilt-android:2.46.1")
    kapt("com.google.dagger:hilt-compiler:2.46.1")

    //Accompanist
    implementation("com.google.accompanist:accompanist-placeholder-material:0.31.4-beta")

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
    implementation(project(":drawbox"))

    implementation("org.burnoutcrew.composereorderable:reorderable:0.9.6")

    implementation("com.google.accompanist:accompanist-systemuicontroller:0.31.3-beta")

}
