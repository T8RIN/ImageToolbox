@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.gemalto.jp2"
    compileSdk = 34

    defaultConfig {
        minSdk = 14
        targetSdk = 34

        externalNativeBuild {
            cmake { cppFlags("-Wl,--build-id=none") }
        }

    }

    buildTypes {
        release {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    externalNativeBuild {
        cmake { path("CMakeLists.txt") }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    packaging {
        jniLibs {
            excludes.add("lib/arm64-v8a/liblog.so")
        }
    }
}

dependencies {
//    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation("androidx.annotation:annotation:1.6.0")
}